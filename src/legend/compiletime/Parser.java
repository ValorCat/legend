package legend.compiletime;

import legend.compiletime.Token.TokenType;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.error.InterpreterException;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.group.Parentheses;
import legend.compiletime.expression.group.SquareBrackets;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.StatementType;
import legend.compiletime.statement.basic.EndStatement;
import legend.compiletime.statement.block.BlockStatementType;
import legend.compiletime.statement.block.clause.ClauseStatementType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static legend.compiletime.Token.TokenType.GROUP;

/**
 * Parsing is the second step in interpretation, after lexing. The parser translates the sequence of tokens generated by
 * the {@link Lexer} into abstract syntax trees representing expressions, and classes representing statements. A
 * statement is a top-level unit of code that may contain 0 or more expressions. For example,
 *
 *   return 10 + compute(x)
 *
 * translates to a single statement of type {@link legend.compiletime.statement.basic.ReturnStatement} that contains an
 * expression of type {@link legend.compiletime.expression.operation.BinaryOperation}. The expression is actually only the
 * root of an expression tree represented by nested sub-expressions:
 *
 *                 +
 *               /   \
 *             10    call
 *                 /      \
 *            compute     ( )
 *                         |
 *                         x
 *
 * @see Lexer
 * @see Token
 * @see Statement
 * @see StatementType
 * @see Expression
 */
public class Parser {

    /* tracks the nesting of control structures */
    private Stack<BlockStatementType> controlStack;

    /**
     * Translate a sequence of tokens into parsed statements, as detailed at the top of this class.
     * @param tokens the tokens to parse
     * @return a list of parsed statements
     */
    public List<Statement> parse(List<TokenLine> tokens) {
        List<Statement> statements = new ArrayList<>(tokens.size());
        controlStack = new Stack<>();
        for (TokenLine line : tokens) {
            try {
                StatementType type = StatementType.resolve(line);
                if (type instanceof ClauseStatementType) {
                    checkEnclosingStructure(type);
                }
                statements.add(type.parse(line, this));
            } catch (InterpreterException e) {
                e.setLineNumber(line.getLineNumber());
            }
        }
        checkMissingEnd(statements, tokens);
        return statements;
    }

    /**
     * Verify that the specified statement type is permitted as a clause header within the current control structure.
     * Log and raise an interpreter error if the statement is not supported, or there is no active control structure.
     * @param type the clause type to verify
     * @throws InterpreterException if the clause is not supported by the current control structure, or the clause is not
     *                         inside a control structure
     * @see BlockStatementType#allowsClause
     */
    private void checkEnclosingStructure(StatementType type) {
        if (controlStack.isEmpty()) {
            throw ErrorLog.get("'%s' statement must be inside a control structure", type.getName());
        } else if (!controlStack.peek().allowsClause(type.getName())) {
            throw ErrorLog.get("Structure '%s' does not support '%s' clauses", controlStack.peek().getName(),
                    type.getName());
        }
    }

    /**
     * Verify that the control stack is empty. If it isn't, log an interpreter error. If the control stack is not empty
     * after the input has finished parsing, this indicates that the source code is missing 1 or more 'end' statements.
     * @param tokens the list of input tokens
     */
    private void checkMissingEnd(List<Statement> statements, List<TokenLine> tokens) {
        if (!controlStack.isEmpty()) {
            int lastLineNumber = tokens.get(tokens.size() - 1).getLineNumber();
            ErrorLog.log(lastLineNumber, "Expected 'end' to close '%s'", controlStack.peek().getName());
            while (!controlStack.isEmpty()) {
                statements.add(new Statement(new EndStatement()));
                controlStack.pop();
            }
        }
    }

    /**
     * Translate a list of tokens into an abstract syntax tree, beginning at the specified index.
     * @param expression the tokens to translate, which are mutated during parsing
     * @param startIndex the leftmost index to consider while parsing; indices to the left are completely ignored
     * @return the root of a parsed abstract syntax tree
     */
    public Expression parseFrom(TokenLine expression, int startIndex) {
        return parseExpression(expression.subList(startIndex, expression.size()));
    }

    /**
     * Translate a list of tokens into an abstract syntax tree.
     * @param expression the tokens to translate, which are mutated during parsing
     * @return the root of a parsed abstract syntax tree
     */
    public Expression parseExpression(TokenLine expression) {
        if (expression.isEmpty()) {
            // if there are no tokens (such as in a function call with no args)
            // just return empty parentheses
            return Parentheses.EMPTY_PARENS;
        }

        injectImplicitOperators(expression);
        resolveGroups(expression);

        int operatorCount = getOperatorCount(expression);
        for (OperatorTable.TableRow level : OperatorTable.OPERATORS) {
            for (int i = 0; i < expression.size(); i++) {
                Token token = expression.get(i);
                if (token.TYPE == TokenType.OPERATOR && level.OPERATORS.contains(token.VALUE)) {
                    boolean hasLeft = expression.exists(i - 1);
                    boolean hasRight = expression.exists(i + 1);
                    if (level.DEGREE.matches(hasLeft, hasRight)) {
                        if (level.DEGREE == OperationDegree.BINARY) {
                            OperatorTable.parseBinary(expression, i);
                        } else {
                            OperatorTable.parseUnary(expression, i, level.DEGREE);
                        }
                        i--;
                        operatorCount--;
                    }
                }
            }
            if (operatorCount <= 0) {
                break;
            }
        }

        if (expression.size() > 1) {
            throw ErrorLog.get("Malformed expression");
        }
        return expression.get(0).asExpression();
    }

    /**
     * Recursively search through a statement for subexpressions--pairs of (), [], or {}--and parse them.
     * @param tokens the statement to search through
     */
    private void resolveGroups(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.TYPE == GROUP) {
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
     * Insert special implict operators into the token list prior to beginning the main process of parsing. Examples of
     * implicit operations include function calls and custom operators.
     * @param tokens the token list to insert into
     */
    private static void injectImplicitOperators(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token current = tokens.get(i);
            int distanceFromEnd = tokens.size() - i - 1;
            if (current.isValue() && distanceFromEnd > 0) {
                Token next = tokens.get(i + 1);
                if (next.matches("()", GROUP) && (i == 0 || !tokens.get(i - 1).matches("def"))) {
                    tokens.add(i + 1, Token.newOperator("()"));
                } else if (next.matches("[]", GROUP)) {
                    tokens.add(i + 1, Token.newOperator("[]"));
                }
            }
        }
    }

    private static int getOperatorCount(TokenLine expression) {
        int operatorCount = 0;
        for (Token token : expression) {
            if (token.TYPE == TokenType.OPERATOR) {
                operatorCount++;
            }
        }
        return operatorCount;
    }

}
