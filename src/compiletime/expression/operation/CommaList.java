package compiletime.expression.operation;

import compiletime.Token;
import compiletime.TokenLine;
import compiletime.expression.Expression;
import compiletime.expression.value.Value;
import runtime.Scope;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1/19/2019
 */
public class CommaList extends Operation {

    public CommaList(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, TokenLine tokens) {
        super.parse(pos, tokens);
        if (operands.get(0).matches(",")) {
            List<Expression> newOperands = new ArrayList<>(operands.get(0).getChildren());
            newOperands.add(operands.get(1));
            operands = newOperands;
        }
    }

    @Override
    public Value evaluate(Scope scope) {
        throw new RuntimeException("Unexpected ',' in expression");
    }

}