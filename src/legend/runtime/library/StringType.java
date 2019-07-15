package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.*;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.compiletime.expression.value.function.LFunction;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.compiletime.expression.value.type.Type;

public class StringType extends BuiltinType {

    public StringType() {
        super(new BuiltinType.Builder("String", "Any")
                .shared("show", StringType::show)
                .unaryOper("for", StringType::operIterate)
                .unaryOper("#", StringType::operSize)
                .binaryOper("[]", StringType::operSubscript)
        );
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target().asString());
        return LNull.NULL;
    }

    private static Value operIterate(Value operand) {
        LFunction hasNext = new BuiltinFunction("has_next", _args -> {
            int current = _args.target().getAttribute("position").asInteger();
            int size = _args.target().getAttribute("values").asString().length();
            return LBoolean.resolve(current < size);
        });
        LFunction getNext = new BuiltinFunction("next", _args -> {
            int current = _args.target().getAttribute("position").asInteger();
            String string = _args.target().getAttribute("values").asString();
            _args.target().setAttribute("position", new LInteger(current + 1));
            // todo error if out of range
            return new LString(string.substring(current, current + 1));
        });
        return Type.of("Iterator").instantiate(new ArgumentList(operand, new LInteger(0), hasNext, getNext));
    }

    private static Value operSize(Value operand) {
        return new LInteger(operand.asString().length());
    }

    private static Value operSubscript(Value target, Value subscript) {
        String string = target.asString();
        int index = subscript.asInteger();
        if (index >= 0 && index < string.length()) {
            return new LString(string.substring(index, index + 1));
        }
        throw new RuntimeException("Cannot get index " + index + " of string of length " + string.length());
    }

}
