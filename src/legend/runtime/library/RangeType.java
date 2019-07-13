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
        super(new BuiltinType.Builder("Range")
                .personal("left", "right")
                .shared("contains", RangeType::contains)
                .shared("show", RangeType::show)
                .operation("for", RangeType::operIterate)
                .operation("#", RangeType::operLength)
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

    private static Value operIterate(ArgumentList args) {
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
        return Type.of("Iterator").instantiate(
                new ArgumentList(args.scope(), args.arg(0), args.arg(0).getAttribute("left"), hasNext, getNext));
    }

    private static Value operLength(ArgumentList args) {
        int left = args.arg(0).getAttribute("left").asInteger();
        int right = args.arg(0).getAttribute("right").asInteger();
        return new LInteger(right - left + 1);
    }

}
