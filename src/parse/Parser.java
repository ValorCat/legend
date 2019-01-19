package parse;

import dataformat.Expression;
import dataformat.operation.CommaList;

import java.util.ArrayList;
import java.util.List;

import static parse.Token.TokenType.OPERATOR;
import static parse.Token.TokenType.PARENS;

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
        for (List<Token> statement : tokens) {
            trees.add(parseExpression(statement));
        }
        return trees;
    }

    /**
     * Convert a statement into a parsed syntax tree.
     * @param statement the statement to convert, which will be consumed and destroyed
     * @return the root of a syntax tree
     */
    private Expression parseExpression(List<Token> statement) {
        List<Token> precedence = getPrecedence(statement);
        for (Token operator : precedence) {
            int position = statement.indexOf(operator);
            if (position < 0) {
                throw new RuntimeException("Couldn't find operator '" + operator.VALUE + "';\n  statement="
                        + statement + "\n  precedence=" + precedence);
            }
            OperatorTable.parseOperation(position, statement);
        }
        if (statement.isEmpty()) {
            // if the expression was empty (such as empty parentheses to a function call),
            // then treat it like a comma with no children
            statement.add(new Token(",", new CommaList()));
        } else if (statement.size() > 1) {
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

}
