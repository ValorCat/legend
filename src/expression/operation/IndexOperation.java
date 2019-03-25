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
        ArgumentList args = new ArgumentList(index);
        args.setTarget(target);
        return target.callMetamethod("_index", args, env, "target of [] expression");
    }

}
