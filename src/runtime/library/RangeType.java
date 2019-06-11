package runtime.library;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.LBoolean;
import compiletime.expression.value.LInteger;
import compiletime.expression.value.LNull;
import compiletime.expression.value.Value;
import compiletime.expression.value.function.BuiltinFunction;
import compiletime.expression.value.function.LFunction;
import compiletime.expression.value.type.BuiltinType;
import compiletime.expression.value.type.Type;
import runtime.Scope;

public class RangeType extends BuiltinType {

    public RangeType() {
        super("Range", new String[] {"left", "right"},
                new BuiltinFunction("contains", RangeType::contains),
                new BuiltinFunction("show", RangeType::show),
                new BuiltinFunction("_loop", RangeType::metaLoop),
                new BuiltinFunction("_size", RangeType::metaSize));
    }

    private static Value contains(ArgumentList args, Scope scope) {
        int value = args.arg(0).asInteger();
        int left = args.target().getAttribute("left").asInteger();
        int right = args.target().getAttribute("right").asInteger();
        return LBoolean.resolve(value >= left && value <= right);
    }

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value metaLoop(ArgumentList args, Scope scope) {
        LFunction hasNext = new BuiltinFunction("has_next", (_args, _scope) -> {
            int current = _args.target().getAttribute("position").asInteger();
            int max = _args.target().getAttribute("values").getAttribute("right").asInteger();
            return LBoolean.resolve(current <= max);
        });
        LFunction getNext = new BuiltinFunction("next", (_args, _scope) -> {
            Value current = _args.target().getAttribute("position");
            _args.target().setAttribute("position", new LInteger(current.asInteger() + 1));
            // todo error if out of range
            return current;
        });
        return Type.of("Iterator").instantiate(
                new ArgumentList(args.target(), args.target().getAttribute("left"), hasNext, getNext), scope);
    }

    private static Value metaSize(ArgumentList args, Scope scope) {
        int left = args.target().getAttribute("left").asInteger();
        int right = args.target().getAttribute("right").asInteger();
        return new LInteger(right - left + 1);
    }

}