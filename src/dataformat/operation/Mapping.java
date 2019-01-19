package dataformat.operation;

import dataformat.value.Value;
import execute.Environment;
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
    public Value evaluate(Environment env) {
        throw new RuntimeException("Unexpected ':' in expression");
    }

}
