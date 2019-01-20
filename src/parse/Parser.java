package parse;

import dataformat.Expression;
import dataformat.operation.CommaList;
import dataformat.operation.flow.EndStatement;
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
     * @param tokens the list of statements
     * @return a list of syntax trees
     */
    public List<Expression> parse(List<List<Token>> tokens) {
        List<Expression> trees = new ArrayList<>(tokens.size());
        Stack<FlowController> controlStack = new Stack<>();
        for (int i = 0; i < tokens.size(); i++) {
            Expression treeRoot = parseExpression(tokens.get(i));
            trees.add(treeRoot);
            if (treeRoot instanceof FlowController) {
                controlStack.push(((FlowController) treeRoot));
            } else if (treeRoot instanceof EndStatement) {
                if (controlStack.isEmpty()) {
                    throw new RuntimeException("Unexpected 'end'");
                }
                FlowController controller = controlStack.pop();
                controller.setEndIndex(i);
            }
        }
        return trees;
    }

    /**
     * Convert a statement into a parsed syntax tree.
     * @param statement the statement to convert, which will be consumed and destroyed
     * @return the root of a syntax tree
     */
    private Expression parseExpression(List<Token> statement) {
        if (statement.isEmpty()) {
            // if there are no tokens (such as from empty parens to a function call),
            // just return an empty list
            return new CommaList();
        }
        injectImplicitOperators(statement);
        List<Token> precedence = getPrecedence(statement);
        for (Token operator : precedence) {
            int position = statement.indexOf(operator);
            if (position < 0) {
                throw new RuntimeException("Couldn't find operator '" + operator.VALUE + "';\n  statement="
                        + statement + "\n  precedence=" + precedence);
            }
            OperatorTable.parseOperation(position, statement);
        }
        if (statement.size() > 1) {
            // this happens if an operator is missing from the expression
            // for example: 3 x * 2
            throw new RuntimeException("Expression resolved to multiple values: " + statement);
        }
        return statement.get(0).EXPRESSION;
    }

    /**
     * Return a list of the operators in a statement, sorted by their precedence
     * level from high to low.
     * @param statement the statement from which the operators should be drawn
     * @return a list of operators sorted by precedence
     */
    private List<Token> getPrecedence(List<Token> statement) {
        List<Token> ordering = new ArrayList<>();
        for (int i = 0; i < statement.size(); i++) {
            Token token = statement.get(i);
            if (token.TYPE == OPERATOR) {
                ordering.add(token);
            } else if (token.TYPE == PARENS) {
                // todo move paren parsing to another method
                parseExpression(token.CHILDREN);
                statement.set(i, token.CHILDREN.get(0));
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
                if (next.TYPE == PARENS) {
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
