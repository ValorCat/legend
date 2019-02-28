package expression.operation;

import expression.Expression;
import expression.value.Value;
import execute.Environment;
import parse.Token;

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
    protected void parse(int pos, List<Token> tokens) {
        super.parse(pos, tokens);
        if (operands.get(0).matches(",")) {
            List<Expression> newOperands = new ArrayList<>(operands.get(0).getChildren());
            newOperands.add(operands.get(1));
            operands = newOperands;
        }
    }

    @Override
    public Value evaluate(Environment env) {
        throw new RuntimeException("Unexpected ',' in expression");
    }

}
