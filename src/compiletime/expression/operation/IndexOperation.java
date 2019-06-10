package compiletime.expression.operation;

import compiletime.Token;
import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.Value;
import runtime.Scope;

import java.util.List;

/**
 * @since 3/17/2019
 */
public class IndexOperation extends Operation {

    public IndexOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value target = operands.get(0).evaluate(scope);
        Value index = operands.get(1).getChildren().get(0).evaluate(scope);
        ArgumentList args = new ArgumentList(index);
        args.setTarget(target);
        return target.callMetamethod("_index", args, scope, "target of [] expression");
    }

}
