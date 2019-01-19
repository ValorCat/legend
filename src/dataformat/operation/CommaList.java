package dataformat.operation;

import dataformat.Expression;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since 1/19/2019
 */
public class CommaList extends Operation {

    public CommaList(int position, List<Token> tokens) {
        super(position, tokens);
    }

    public CommaList(Expression... elements) {
        super(",", Arrays.asList(elements));
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        super.parse(pos, tokens);
        if (operands.get(0).matches(",")) {
            operands = new ArrayList<>(operands.get(0).getChildren());
            operands.add(operands.get(1));
        }
    }

    @Override
    public Value evaluate(Environment env) {
        throw new RuntimeException("Unexpected ',' in expression");
    }

}
