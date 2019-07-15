package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.LInteger;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.compiletime.expression.value.function.LFunction;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.compiletime.expression.value.type.Type;

public class RangeType extends BuiltinType {

    public RangeType() {
        super(new BuiltinType.Builder("Range", "Any")
                .personal("left", "right")
                .shared("contains", RangeType::contains)
                .shared("show", RangeType::show)
                .unaryOper("for", RangeType::operIterate)
                .unaryOper("#", RangeType::operSize)
        );
    }

    private static Value contains(ArgumentList args) {
        int value = args.arg(0).asInteger();
        int left = args.target().getAttribute("left").asInteger();
        int right = args.target().getAttribute("right").asInteger();
        return LBoolean.resolve(value >= left && value <= right);
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value operIterate(Value operand) {
        LFunction hasNext = new BuiltinFunction("has_next", _args -> {
            int current = _args.target().getAttribute("position").asInteger();
            int max = _args.target().getAttribute("values").getAttribute("right").asInteger();
            return LBoolean.resolve(current <= max);
        });
        LFunction getNext = new BuiltinFunction("next", _args -> {
            Value current = _args.target().getAttribute("position");
            _args.target().setAttribute("position", new LInteger(current.asInteger() + 1));
            // todo error if out of range
            return current;
        });
        return Type.of("Iterator").instantiate(new ArgumentList(operand, operand.getAttribute("left"), hasNext, getNext));
    }

    private static Value operSize(Value operand) {
        int left = operand.getAttribute("left").asInteger();
        int right = operand.getAttribute("right").asInteger();
        return new LInteger(right - left + 1);
    }

}
