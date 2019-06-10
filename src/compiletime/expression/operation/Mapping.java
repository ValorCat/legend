package compiletime.expression.operation;

import compiletime.Token;
import compiletime.expression.value.Value;
import runtime.Scope;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class Mapping extends Operation {

    public Mapping(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        throw new RuntimeException("Unexpected ':' in expression");
    }

}
