package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

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
        Value subscript = operands.get(1).getChildren().get(0).evaluate(scope);
        return target.operateBinary("[]", subscript);
    }

}
