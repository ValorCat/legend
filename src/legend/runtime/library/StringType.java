package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LInteger;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.LString;
import legend.compiletime.expression.value.Value;
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
        LFunction func = new BuiltinFunction("next", args -> {
            int current = args.target().getAttribute("position").asInteger();
            String string = args.target().getAttribute("values").asString();
            if (current < string.length()) {
                args.target().setAttribute("position", new LInteger(current + 1));
                return new LString(string.substring(current, current + 1));
            }
            return LNull.NULL;
        });
        return Type.of("Iterator").instantiate(new ArgumentList(operand, new LInteger(0), func));
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
