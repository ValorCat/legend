package parse;

import dataformat.Expression;
import dataformat.Operation;
import dataformat.Variable;
import dataformat.value.BoolValue;
import dataformat.value.IntValue;
import dataformat.value.StringValue;
import dataformat.value.Value;
import parse.Token.TokenType;

import java.util.ArrayList;
import java.util.List;

import static parse.OperatorTable.OPERATORS;
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
            Token root = parseExpression(statement);
            trees.add(convertToTree(root));
        }
        return trees;
    }

    /**
     * Convert a statement into a tree of tokens, arranged by their precedence levels.
     * Note that this tree is only an intermediate step in creating the final syntax
     * tree for a particular statement. The tree returned by this method is intended
     * to be passed to the {@link #convertToTree(Token)} method.
     * @param statement the statement to convert; it is not preserved by this method
     * @return the root of a token tree
     */
    private Token parseExpression(List<Token> statement) {
        List<Token> precedence = getPrecedence(statement);
        for (Token operator : precedence) {
            int position = statement.indexOf(operator);
            if (position < 0) {
                throw new RuntimeException("Couldn't find operator '" + operator.VALUE + "';\n  statement="
                        + statement + "\n  precedence=" + precedence);
            }
            OPERATORS.validate(position, statement);
        }
        if (statement.isEmpty()) {
            // if the expression was empty (such as empty parentheses to a function call),
            // then treat it like a comma with no children
            statement.add(new Token(TokenType.OPERATION, ",", List.of()));
        } else if (statement.size() > 1) {
            // this happens if an operator is missing from the expression
            // for example: 3 x * 2
            throw new RuntimeException("Expression resolved to multiple values: " + statement);
        }
        return statement.get(0);
    }

    /**
     * Convert a token tree into the final syntax tree.
     * @param node the root of the token tree
     * @return a syntax tree representing a single statement
     */
    private static Expression convertToTree(Token node) {
        switch (node.TYPE) {
            case LITERAL:
                return parseLiteral(node.VALUE);
            case IDENTIFIER:
                return new Variable(node.VALUE);
            case OPERATION:
                List<Expression> children = new ArrayList<>(node.CHILDREN.size());
                for (Token child : node.CHILDREN) {
                    children.add(convertToTree(child));
                }
                return new Operation(node.VALUE, children);
            default:
                throw new RuntimeException("Unexpected token: " + node);
        }
    }

    /**
     * Convert a string representing a literal, like "5" or "false", into an
     * Expression object.
     * @param literal the string to convert
     * @return an object that represents the same literal
     */
    private static Value parseLiteral(String literal) {
        if (literal.charAt(0) == '\'') {
            return new StringValue(literal);
        } else if (Character.isDigit(literal.charAt(0))) {
            return new IntValue(Integer.parseInt(literal));
        } else if (literal.equals("true") || literal.equals("false")) {
            return BoolValue.resolve(literal.equals("true"));
        }
        throw new RuntimeException("Invalid literal type: " + literal);
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
                parseExpression(token.CHILDREN);
                statement.set(i, token.CHILDREN.get(0));
            }
        }
        ordering.sort(OperatorTable.byPrecedence());
        return ordering;
    }

}
