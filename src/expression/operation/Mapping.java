package expression.operation;

import execute.Scope;
import expression.value.Value;
import parse.Token;

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
