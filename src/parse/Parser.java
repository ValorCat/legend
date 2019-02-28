package parse;

import expression.Expression;
import expression.group.Parentheses;
import statement.*;
import statement.structure.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static parse.Token.TokenType.IDENTIFIER;
import static parse.Token.TokenType.OPERATOR;

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
    private Stack<FlowController> controlStack;

    /**
     * Convert a stream of tokens into a sequence of syntax trees.
     * @param tokens the list of tokens
     * @return a list of syntax trees
     */
    public List<Statement> parse(List<List<Token>> tokens) {
        List<Statement> trees = new ArrayList<>(tokens.size());
        controlStack = new Stack<>();
        for (address = 0; address < tokens.size(); address++) {
            trees.add(parseStatement(tokens.get(address)));
        }
        if (!controlStack.isEmpty()) {
            throw new RuntimeException("Expected 'end' to close '" + controlStack.peek().getKeyword() + "'");
        }
        return trees;
    }

    /**
     * Convert a line of tokens into a statement.
     * @param tokens the tokens to convert
     * @return the parsed statement
     */
    private Statement parseStatement(List<Token> tokens) {
        Token initial = tokens.get(0);
        if (initial.TYPE != OPERATOR) {
            // check if assignment
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).matches("=")) {
                    return new Assignment(tokens, i, this);
                }
            }
        } else {
            // check if flow control statement
            Statement statement = null;
            switch (initial.VALUE) {
                case "def":    statement = new FunctionDefinition(tokens, this); break;
                case "end":    statement = new EndStatement(tokens, this); break;
                case "for":    statement = new ForLoop(tokens, this); break;
                case "if":     statement = new IfStatement(tokens, this); break;
                case "repeat": statement = new RepeatLoop(tokens); break;
                case "return": statement = new ReturnStatement(tokens); break;
                case "while":  statement = new WhileLoop(tokens, this); break;
                case "elsif": case "else":
                    if (controlStack.isEmpty()) {
                        throw new RuntimeException("Unexpected symbol '" + initial.VALUE + "'");
                    }
                    controlStack.peek().setJumpPoint(tokens, this);
                    break;
            }
            if (statement != null) {
                if (statement instanceof FlowController) {
                    controlStack.push((FlowController) statement);
                }
                return statement;
            }
        }
        // statement is expression
        return parseExpression(tokens);
    }

    public Expression parseFrom(List<Token> expression, int startIndex) {
        return parseExpression(expression.subList(startIndex, expression.size()));
    }

    /**
     * Convert an expression into a parsed syntax tree.
     * @param expression the tokens to convert, which will be consumed and destroyed
     * @return the root of a syntax tree
     */
    public Expression parseExpression(List<Token> expression) {
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
            if (position < 0) {
                throw new RuntimeException("Couldn't find operator '" + operator.VALUE + "';\n  expression="
                        + expression + "\n  precedence=" + precedence);
            }
            OperatorTable.parseOperation(position, expression);
        }
        if (expression.size() > 1) {
            // this happens if an operator is missing from the expression
            // for example: 3 x * 2
            throw new RuntimeException("Expression resolved to multiple values: " + expression);
        }
        return expression.get(0).asExpression();
    }

    public int getAddress() {
        return address;
    }

    public Stack<FlowController> getControlStack() {
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
            if (token.matches("()")) {
                Expression value = parseExpression(token.CHILDREN);
                if (value != Parentheses.EMPTY_PARENS) {
                    value = new Parentheses(value);
                }
                tokens.set(i, Token.newExpression("()", value));
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
