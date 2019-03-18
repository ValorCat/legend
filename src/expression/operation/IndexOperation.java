package expression.operation;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.Value;
import parse.Token;

import java.util.List;

/**
 * @since 3/17/2019
 */
public class IndexOperation extends Operation {

    public IndexOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        Value target = operands.get(0).evaluate(env);
        Value index = operands.get(1).getChildren().get(0).evaluate(env);
        Value method = target.getOptionalAttribute("_index").orElseThrow(
                () -> new RuntimeException("Cannot index with [] on value of type '"
                        + target.type().getName() + "' with no '_index' method")
        );
        ArgumentList args = new ArgumentList(index);
        args.setTarget(target);
        return FunctionCall.call(method, args, env);
    }

}
