package legend.compiletime.expression.operation;

import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;

import java.util.List;
import java.util.StringJoiner;

/**
 * An operation is a sequence of contiguous tokens, consisting of an operator and 0 or more operands, that resolves to a
 * value when evaluated. Most operations are implemented on a per-datatype basis, while others have special
 * implementations that cannot be overridden or overloaded.
 * @see BinaryOperation
 * @see UnaryOperation
 * @see legend.runtime.library for operator implementations for each built-in type
 */
public abstract class Operation implements Expression {

    protected String operator;

    public Operation(String operator) {
        this.operator = operator;
    }

    /**
     * Consolidate the tokens that comprise this operation into a single token. If the operation is not syntactically
     * valid, raise an exception.
     * @param line the list of tokens
     * @param operIndex the index of the operator token
     */
    public abstract void parse(TokenLine line, int operIndex);

    @Override
    public abstract List<Expression> getChildren();

    @Override
    public boolean matches(String pattern) {
        return operator.equals(pattern);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ", "(", ")");
        for (Expression operand : getChildren()) {
            joiner.add(operand.toString());
        }
        return operator + joiner;
    }

}
