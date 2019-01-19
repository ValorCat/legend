package dataformat;

import dataformat.value.Value;
import execute.Environment;
import execute.OperationsExecution;
import execute.OperationsExecution.OperationHandler;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @since 12/23/2018
 */
public class Operation implements Expression {

    private String operator;
    private List<Expression> operands;

    public Operation(String operator, List<Expression> operands) {
        this.operator = operator;
        this.operands = operands;
    }

    public Operation(String operator, Expression... operands) {
        this.operator = operator;
        this.operands = Arrays.asList(operands);
    }

    @Override
    public Value evaluate(Environment env) {
        OperationHandler executor = OperationsExecution.OPERATIONS.get(operator);
        if (executor == null) {
            throw new RuntimeException("Expected a value, found expression with '" + operator + "'");
        }
        return executor.apply(operands, env);
    }

    @Override
    public boolean matches(String pattern) {
        return this.operator.equals(pattern);
    }

    @Override
    public List<Expression> getChildren() {
        return operands;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ", "(", ")");
        for (Expression operand : operands) {
            joiner.add(operand.toString());
        }
        return operator + joiner;
    }

}
