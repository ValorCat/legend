package parse;

import expression.Expression;
import expression.group.Parentheses;
import expression.group.SquareBrackets;
import parse.Token.TokenType;
import parse.error.ErrorLog;
import parse.error.ParserException;
import statement.Statement;
import statement.StatementType;
import statement.block.BlockStatementType;
import statement.block.clause.ClauseStatementType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringJoiner;

import static parse.Token.TokenType.IDENTIFIER;
import static parse.Token.TokenType.OPERATOR;
import static parse.error.ErrorDescription.*;

public class Parser {

    private Stack<BlockStatementType> controlStack;

    public List<Statement> parse(List<TokenLine> tokens) {
        List<Statement> statements = new ArrayList<>(tokens.size());
        controlStack = new Stack<>();
        for (TokenLine line : tokens) {
            try {
                StatementType type = StatementType.resolve(line);
                if (type instanceof ClauseStatementType) {
                    checkEnclosingStructure(type, line);
                }
                statements.add(type.parse(line, this));
            } catch (ParserException e) {
                e.setLineNumber(line.getLineNumber());
            }
        }
        checkMissingEnd(tokens);
        return statements;
    }

    private void checkEnclosingStructure(StatementType type, TokenLine line) {
        if (controlStack.isEmpty()) {
            throw ErrorLog.raise(BAD_JUMP_POINT, "'%s' statement must be inside a control structure",
                    type.getKeyword());
        } else if (!controlStack.peek().allowsClause(type.getKeyword())) {
            String structType = controlStack.peek().getKeyword();
            ErrorLog.log(BAD_JUMP_POINT, line.getLineNumber(), "Structure '%s' does not support '%s' clauses",
                    structType, type.getKeyword());
        }
    }

    private void checkMissingEnd(List<TokenLine> tokens) {
        if (!controlStack.isEmpty()) {
            int lastLineNumber = tokens.get(tokens.size() - 1).getLineNumber();
            ErrorLog.log(BAD_NESTING, lastLineNumber, "Expected 'end' to close '%s'",
                    controlStack.peek().getKeyword());
        }
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

    public Stack<BlockStatementType> getControlStack() {
        return controlStack;
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
