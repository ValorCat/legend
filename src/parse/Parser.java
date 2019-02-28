package parse;

import dataformat.Expression;
import dataformat.Statement;
import dataformat.group.Parentheses;
import dataformat.operation.flow.FlowController;

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
 * in the {@link dataformat} package and its subpackages.
 * @see Expression
 * @since 12/22/2018
 */
public class Parser {

    /**
     * Convert a stream of tokens into a sequence of syntax trees.
     * @param tokens the list of tokens
     * @return a list of syntax trees
     */
    public List<Statement> parse(List<List<Token>> tokens) {
        List<Statement> trees = new ArrayList<>(tokens.size());
        Stack<FlowController> controlStack = new Stack<>();
        for (int i = 0; i < tokens.size(); i++) {
            trees.add(parseStatement(tokens.get(i), i, controlStack));
        }
        if (!controlStack.isEmpty()) {
            throw new RuntimeException("Expected 'end' to close '" + controlStack.peek().getKeyword() + "'");
        }
        return trees;
    }

    /**
     * Convert a line of tokens into a statement.
     * @param tokens the tokens to convert
     * @param address the current instruction's address
     * @param controlStack the stack of flow control structures
     * @return the parsed statement
     */
    public Statement parseStatement(List<Token> tokens, int address, Stack<FlowController> controlStack) {
        return parseExpression(tokens, address, controlStack);
//        Statement dummy = env -> {};
//        Token initial = tokens.get(0);
//        if (initial.TYPE != OPERATOR) {
//            // check if assignment
//            for (Token token : tokens) {
//                if (token.matches("=")) {
//                    return dummy;
//                }
//            }
//        } else {
//            // check if flow control statement
//            switch (initial.VALUE) {
//                case "def":    return dummy;
//                case "end":    return dummy;
//                case "for":    return dummy;
//                case "if":     return dummy;
//                case "repeat": return dummy;
//                case "return": return dummy;
//                case "while":  return dummy;
//            }
//        }
//        // statement is expression
//        return dummy;
    }

    /**
     * Convert a statement into a parsed syntax tree.
     * @param statement the statement to convert, which will be consumed and destroyed
     * @param address the current instruction's address
     * @param controlStack the stack of flow control structures
     * @return the root of a syntax tree
     */
    public Expression parseExpression(List<Token> statement, int address, Stack<FlowController> controlStack) {
        if (statement.isEmpty()) {
            // if there are no tokens (such as in a function call with no args)
            // just return empty parentheses
            return Parentheses.EMPTY_PARENS;
        }
        injectImplicitOperators(statement);
        resolveGroups(statement, address, controlStack);
        List<Token> precedence = getPrecedence(statement);
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
        return statement.get(0).asExpression();
    }

    /**
     * Recursively search through a statement for subexpressions (pairs of (),
     * [], or {}) and parse them.
     * @param statement the statement to search through
     * @param address the current instruction's address
     * @param controlStack the stack of flow control structures
     */
    private void resolveGroups(List<Token> statement, int address, Stack<FlowController> controlStack) {
        for (int i = 0; i < statement.size(); i++) {
            Token token = statement.get(i);
            if (token.matches("()")) {
                Expression value = parseExpression(token.CHILDREN, address, controlStack);
                if (value != Parentheses.EMPTY_PARENS) {
                    value = new Parentheses(value);
                }
                statement.set(i, Token.newExpression("()", value));
            }
        }
    }

    /**
     * Return a list of the operators in a statement, sorted by their precedence
     * level from high to low.
     * @param statement the statement from which the operators should be drawn
     * @return a list of operators sorted by precedence
     */
    private static List<Token> getPrecedence(List<Token> statement) {
        List<Token> ordering = new ArrayList<>();
        for (Token token : statement) {
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
