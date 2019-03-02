package expression.operation;

import expression.Expression;
import parse.Token;
import parse.TokenLine;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * @since 12/23/2018
 */
public abstract class Operation implements Expression {

    protected String operator;
    protected List<Expression> operands;

    public Operation(int position, List<Token> tokens) {
        this.operator = tokens.get(position).VALUE;
        this.operands = Collections.emptyList();
        parse(position, (TokenLine) tokens);
    }

    public Operation(String operator, List<Expression> operands) {
        this.operator = operator;
        this.operands = operands;
    }

    protected void parse(int pos, TokenLine tokens) {
        // can be overridden in subclasses to implement custom parsing
        parseBinaryOperation(pos, tokens);
    }

    @Override
    public boolean matches(String pattern) {
        return operator.equals(pattern);
    }

    @Override
    public List<Expression> getChildren() {
        return operands;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ", "(", ")");
        for (Expression operand : operands) {
            joiner.add(operand.toString());
        }
        return operator + joiner;
    }

    private void parseBinaryOperation(int pos, TokenLine tokens) {
        String operator = tokens.get(pos).VALUE;

        // check that there are values on each side
        Token left = null, right = null;
        if (pos > 0) left = tokens.get(pos - 1);
        if (pos < tokens.size() - 1) right = tokens.get(pos + 1);
        if (left == null || right == null || !left.isValue() || !right.isValue()) {
            throw new RuntimeException("Operator '" + operator + "' requires values on both sides");
        }

        // convert this operation into a tree
        operands = List.of(left.asExpression(), right.asExpression());
        tokens.consolidate(Token.newExpression(operator, this), pos - 1, 3);
    }

    protected void parseLeftUnaryOperation(int pos, TokenLine tokens) {
        String operator = tokens.get(pos).VALUE;
        if (pos == tokens.size() - 1 || !tokens.get(pos + 1).isValue()) {
            throw new RuntimeException("Operator '" + operator + "' requires a value on the left");
        }
        operands = List.of(tokens.get(pos + 1).asExpression());
        tokens.consolidate(Token.newExpression(operator, this), pos, 2);
    }

    protected void parseRightUnaryOperation(int pos, TokenLine tokens) {
        String operator = tokens.get(pos).VALUE;
        if (pos == 0 || !tokens.get(pos - 1).isValue()) {
            throw new RuntimeException("Operator '" + operator + "' requires a value on the right");
        }
        operands = List.of(tokens.get(pos - 1).asExpression());
        tokens.consolidate(Token.newExpression(operator, this), pos - 1, 2);
    }

}
