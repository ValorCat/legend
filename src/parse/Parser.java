package parse;

import expression.Expression;
import expression.group.Parentheses;
import expression.group.SquareBrackets;
import instruction.Instruction;
import parse.Token.TokenType;
import parse.error.ErrorLog;
import parse.error.ParserException;
import statement.StatementData;
import statement.StatementType;
import statement.block.BlockStatementType;
import statement.block.clause.ClauseData;
import statement.block.clause.ClauseStatementType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringJoiner;

import static parse.Token.TokenType.IDENTIFIER;
import static parse.Token.TokenType.OPERATOR;
import static parse.error.ErrorDescription.*;

/**
 * Convert the tokens produced by the {@link Lexer} class into a syntax tree.
 * Each internal node in the tree represents an operation to be performed, and the
 * leaves represent literals and variables. A tree node implements the {@link Expression}
 * interface. Each type of node, i.e. each class that implements Expression, is defined
 * in the {@link expression} package and its subpackages.
 * @see Expression
 * @since 12/22/2018
 */
public class Parser {

    private int address;
    private List<TokenLine> lines;
    private Stack<BlockStatementType> controlStack;

    /**
     * Convert a stream of tokens into a sequence of syntax trees.
     * @param tokens the list of tokens
     * @return a list of syntax trees
     */
    public List<Instruction> parse(List<TokenLine> tokens) {
        address = -1;
        lines = tokens;
        controlStack = new Stack<>();
        List<Instruction> output = parseBlock();
        if (!controlStack.isEmpty()) {
            int lastLineNumber = tokens.get(tokens.size() - 1).getLineNumber();
            ErrorLog.log(BAD_NESTING, lastLineNumber, "Expected 'end' to close '%s'",
                    controlStack.peek().getKeyword());
        }
        return output;
    }

    private List<Instruction> parseBlock() {
        List<Instruction> output = new ArrayList<>();
        for (address++; address < lines.size(); address++) {
            TokenLine line = lines.get(address);
            StatementType stmtType = StatementType.resolve(line);
            try {
                if (stmtType instanceof ClauseStatementType) {
                    if (!controlStack.isEmpty()) {
                        break;
                    }
                    throw ErrorLog.raise(BAD_JUMP_POINT, "'%s' statement must be inside a control structure",
                            stmtType.getKeyword());
                }
                int startStackSize = controlStack.size();
                StatementData parsedStmt = stmtType.parse(line, this);
                if (controlStack.size() < startStackSize) {
                    break;
                }
                output.addAll(stmtType.compile(parsedStmt, this));
            } catch (ParserException e) {
                e.setLineNumber(line.getLineNumber());
            }
        }
        return output;
    }

    public List<Instruction> parseBlockStatement(BlockStatementType statement, StatementData data) {
        List<Instruction> baseClauseBody = parseBlock();
        List<ClauseData> clauses = new ArrayList<>(1);
        clauses.add(new ClauseData("base", data, baseClauseBody));
        while (!StatementType.isEnd(lines.get(address))) {
            ClauseData clause = parseClause(lines);
            if (!statement.allowsClause(clause.TYPE)) {
                int lineNumber = lines.get(address).getLineNumber();
                ErrorLog.log(BAD_JUMP_POINT, lineNumber, "Structure '%s' does not support '%s' clauses",
                        statement.getKeyword(), clause.TYPE);
            }
            clauses.add(clause);
        }
        return statement.build(clauses);
    }

    private ClauseData parseClause(List<TokenLine> lines) {
        TokenLine line = lines.get(address);
        ClauseStatementType clauseType = (ClauseStatementType) StatementType.resolve(line);
        StatementData clauseData = clauseType.parse(line, this);
        List<Instruction> clauseBody = parseBlock();
        return new ClauseData(clauseType.getKeyword(), clauseData, clauseBody);
    }

    public Expression parseFrom(List<Token> expression, int startIndex) {
        return parseExpression(expression.subList(startIndex, expression.size()));
    }

    /**
     * Convert an expression into a parsed syntax tree.
     * @param expression the tokens to convert, which will be consumed and destroyed
     * @return the root of a syntax tree
     */
    private Expression parseExpression(List<Token> expression) {
        if (expression.isEmpty()) {
            // if there are no tokens (such as in a function call with no args)
            // just return empty parentheses
            return Parentheses.EMPTY_PARENS;
        }
        injectImplicitOperators(expression);
        resolveGroups(expression);
        List<Token> precedence = getPrecedence(expression);
        for (Token operator : precedence) {
            int position = expression.indexOf(operator);
            OperatorTable.parseOperation(position, expression);
        }
        if (expression.size() > 1) {
            // this happens if an operator is missing from the expression
            // for example: 3 x * 2
            StringJoiner joiner = new StringJoiner(" ");
            expression.forEach(x -> joiner.add(x.VALUE));
            throw ErrorLog.raise(MISSING_OPER, "Missing operator in expression: %s", joiner);
        }
        return expression.get(0).asExpression();
    }

    public Stack<BlockStatementType> getControlStack() {
        return controlStack;
    }

    /**
     * Recursively search through a statement for subexpressions (pairs of (),
     * [], or {}) and parse them.
     * @param tokens the statement to search through
     */
    private void resolveGroups(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.TYPE == TokenType.GROUP) {
                Expression value = parseExpression(token.CHILDREN);
                if (value != Parentheses.EMPTY_PARENS) {
                    if (token.VALUE.equals("()")) {
                        value = new Parentheses(value);
                    } else if (token.VALUE.equals("[]")) {
                        value = new SquareBrackets(value);
                    }
                }
                tokens.set(i, Token.newExpression(token.VALUE, value));
            }
        }
    }

    /**
     * Return a list of the operators in a statement, sorted by their precedence
     * level from high to low.
     * @param tokens the statement from which the operators should be drawn
     * @return a list of operators sorted by precedence
     */
    private static List<Token> getPrecedence(List<Token> tokens) {
        // todo use more efficient data structure
        List<Token> ordering = new ArrayList<>();
        for (Token token : tokens) {
            if (token.TYPE == OPERATOR) {
                ordering.add(token);
            }
        }
        ordering.sort(OperatorTable.byPrecedence());
        return ordering;
    }

    /**
     * Insert special implict operators into the token list prior to
     * beginning the main process of parsing. Examples of implicit
     * operations include function calls and custom operators.
     * @param tokens the token list to insert into
     */
    private static void injectImplicitOperators(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token current = tokens.get(i);
            int distanceFromEnd = tokens.size() - i - 1;
            if (current.isValue() && distanceFromEnd > 0) {
                Token next = tokens.get(i + 1);
                if (next.matches("()") && (i == 0 || !tokens.get(i - 1).matches("def"))) {
                    tokens.add(i + 1, Token.newOperator("call"));
                } else if (next.matches("[]")) {
                    tokens.add(i + 1, Token.newOperator("index"));
                } else if (next.TYPE == IDENTIFIER) {
                    if (distanceFromEnd == 1 || !tokens.get(i + 2).isValue()) {
                        tokens.add(i + 1, Token.newOperator("unop"));
                    } else {
                        tokens.add(i + 1, Token.newOperator("."));
                        tokens.add(i + 3, Token.newOperator("biop"));
                    }
                }
            }
        }
    }

}
