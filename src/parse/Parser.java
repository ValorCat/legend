package parse;

import dataformat.*;
import tokenize.Token;
import tokenize.Token.TokenType;
import tokenize.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static parse.OperatorTable.OPERATORS;
import static tokenize.Token.TokenType.OPERATOR;
import static tokenize.Token.TokenType.PARENS;

/**
 * @since 12/22/2018
 */
public class Parser {

    public static void main(String[] args) throws IOException {
        String input = new String(Files.readAllBytes(Paths.get("src/input.txt")));

        Tokenizer t = new Tokenizer();
        List<List<Token>> tokens = t.tokenize(input);
        for (List<Token> line : tokens) {
            for (Token token : line) {
                System.out.print(token + "  ");
            }
            System.out.println();
        }
        System.out.println("\n---------------------------------------------\n");

        Parser p = new Parser();
        List<Expression> trees = p.parse(tokens);
        for (List<Token> line : tokens) {
            for (Token token : line) {
                System.out.print(token + "  ");
            }
            System.out.println();
        }
        System.out.println("\n---------------------------------------------\n");

        for (Expression tree : trees) {
            System.out.println(tree);
        }
    }

    public List<Expression> parse(List<List<Token>> tokens) {
        List<Expression> parseTrees = new ArrayList<>(tokens.size());
        for (List<Token> statement : tokens) {
            parseExpression(statement);
            parseTrees.add(convertToTree(statement.get(0)));
        }
        return parseTrees;
    }

    private void parseExpression(List<Token> statement) {
        List<Token> precedence = getPrecedence(statement);
        for (Token operator : precedence) {
            int position = statement.indexOf(operator);
            if (position < 0) {
                throw new RuntimeException("Couldn't find operator '" + operator.VALUE + "';\n  statement="
                        + statement + "\n  precedence=" + precedence);
            }
            OPERATORS.getHandler(operator.VALUE).accept(position, statement);
        }
        if (statement.isEmpty()) {
            statement.add(new Token(TokenType.OPERATION, ",", List.of()));
        } else if (statement.size() > 1) {
            throw new RuntimeException("Expression resolved to multiple values: " + statement);
        }
    }

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
