package parse;

import dataformat.Expression;
import dataformat.operation.CommaList;
import dataformat.operation.flow.FlowController;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static parse.Token.TokenType.*;

/**
 * Convert the tokens produced by the {@link Tokenizer} class into a syntax tree.
 * Each internal node in the tree represents an operation to be performed, and the
 * leaves represent literals and variables. A tree node implements the {@link Expression}
 * interface. Each type of node, i.e. each class that implements Expression, is defined
 * in the {@link dataformat} package and its subpackages.
 * @see Expression
 * @since 12/22/2018
 */
public class Parser {

    /**
     * Convert a list of statements into a list of syntax trees.
     * @param statements the list of statements
     * @return a list of syntax trees
     */
    public List<Expression> parse(List<List<Token>> statements) {
        List<Expression> trees = new ArrayList<>(statements.size());
        Stack<FlowController> controlStack = new Stack<>();
        for (int i = 0; i < statements.size(); i++) {
            Token root = parseExpression(statements.get(i), i, controlStack);
            trees.add(root.asExpression());
        }
        if (!controlStack.isEmpty()) {
            throw new RuntimeException("Expected 'end' to close '" + controlStack.peek().getKeyword() + "'");
        }
        return trees;
    }

    /**
     * Convert a statement into a parsed syntax tree.
     * @param statement the statement to convert, which will be consumed and destroyed
     * @return the root of a syntax tree
     */
    private Token parseExpression(List<Token> statement, int address, Stack<FlowController> controlStack) {
        if (statement.isEmpty()) {
            // if there are no tokens (such as from empty parens to a function call),
            // just return an empty list
            return new Token(PARENS, List.of());
        }
        injectImplicitOperators(statement);
        List<Token> precedence = getPrecedence(statement, address, controlStack);
        for (Token operator : precedence) {
            int position = statement.indexOf(operator);
            if (position < 0) {
                throw new RuntimeException("Couldn't find operator '" + operator.VALUE + "';\n  statement="
                        + statement + "\n  precedence=" + precedence);
            }
            OperatorTable.parseOperation(position, statement, address, controlStack);
        }
        if (statement.size() > 1) {
            // this happens if an operator is missing from the expression
            // for example: 3 x * 2
            throw new RuntimeException("Expression resolved to multiple values: " + statement);
        }
        return statement.get(0);
    }

    /**
     * Return a list of the operators in a statement, sorted by their precedence
     * level from high to low.
     * @param statement the statement from which the operators should be drawn
     * @return a list of operators sorted by precedence
     */
    private List<Token> getPrecedence(List<Token> statement, int address, Stack<FlowController> controlStack) {
        List<Token> ordering = new ArrayList<>();
        for (int i = 0; i < statement.size(); i++) {
            Token token = statement.get(i);
            if (token.TYPE == OPERATOR) {
                ordering.add(token);
            } else if (token.TYPE == PARENS) {
                // todo move paren parsing to another method
                Token parens = parseExpression(token.CHILDREN, address, controlStack);
                statement.set(i, parens);
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
                if (next.TYPE == PARENS && (i == 0 || !tokens.get(i - 1).matches("def"))) {
                    tokens.add(i + 1, new Token(OPERATOR, "call"));
                } else if (next.TYPE == IDENTIFIER) {
                    if (distanceFromEnd == 1 || !tokens.get(i + 2).isValue()) {
                        tokens.add(i + 1, new Token(OPERATOR, "unop"));
                    } else {
                        tokens.add(i + 1, new Token(OPERATOR, "."));
                        tokens.add(i + 3, new Token(OPERATOR, "biop"));
                    }
                }
            }
        }
    }

}
