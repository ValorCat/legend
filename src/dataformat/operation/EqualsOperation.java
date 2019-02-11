package dataformat.operation;

import dataformat.value.BoolValue;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

import static dataformat.value.BoolValue.FALSE;
import static dataformat.value.BoolValue.TRUE;

/**
 * @since 1/19/2019
 */
public class EqualsOperation extends Operation {

    private final boolean NEGATE;

    public EqualsOperation(int position, List<Token> tokens) {
        super(position, tokens);
        NEGATE = operator.equals("!=");
    }

    @Override
    public Value evaluate(Environment env) {
        Value left = operands.get(0).evaluate(env);
        Value right = operands.get(1).evaluate(env);
        if (left.equals(right)) return NEGATE ? FALSE : TRUE;
        List<String> types = List.of(left.type().getName(), right.type().getName());
        if (types.contains("str") && (types.contains("bool") || types.contains("int"))) {
            return BoolValue.resolve(NEGATE != left.asString().equals(right.asString()));
        }
        return FALSE;
    }

}
