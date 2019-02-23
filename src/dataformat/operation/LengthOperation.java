package dataformat.operation;

import dataformat.ArgumentList;
import dataformat.operation.function.FunctionCall;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 2/22/2019
 */
public class LengthOperation extends Operation {

    public LengthOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        parseLeftUnaryOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        Value operand = operands.get(0).evaluate(env);
        Value method = operand.getOptionalAttribute("_size").orElseThrow(
                () -> new RuntimeException("Cannot use '#' operator on value of type '"
                        + operand.type().getName() + "' with no '_size' method")
        );
        ArgumentList args = new ArgumentList();
        args.setTarget(operand);
        return FunctionCall.call(method, args, env);
    }

}
