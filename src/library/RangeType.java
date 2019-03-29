package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LBoolean;
import expression.value.LInteger;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.BuiltinFunction;
import expression.value.function.LFunction;
import expression.value.type.BuiltinType;
import expression.value.type.Type;

public class RangeType extends BuiltinType {

    public RangeType() {
        super("Range", new String[] {"left", "right"},
                new BuiltinFunction("contains", RangeType::contains),
                new BuiltinFunction("show", RangeType::show),
                new BuiltinFunction("_loop", RangeType::metaLoop),
                new BuiltinFunction("_size", RangeType::metaSize));
    }

    private static Value contains(ArgumentList args, Environment env) {
        int value = args.arg(0).asInteger();
        int left = args.target().getAttribute("left").asInteger();
        int right = args.target().getAttribute("right").asInteger();
        return LBoolean.resolve(value >= left && value <= right);
    }

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value metaLoop(ArgumentList args, Environment env) {
        LFunction hasNext = new BuiltinFunction("has_next", (_args, _env) -> {
            int current = _args.target().getAttribute("position").asInteger();
            int max = _args.target().getAttribute("values").getAttribute("right").asInteger();
            return LBoolean.resolve(current <= max);
        });
        LFunction getNext = new BuiltinFunction("next", (_args, _env) -> {
            Value current = _args.target().getAttribute("position");
            _args.target().setAttribute("position", new LInteger(current.asInteger() + 1));
            // todo error if out of range
            return current;
        });
        return Type.of("Iterator").instantiate(
                new ArgumentList(args.target(), args.target().getAttribute("left"), hasNext, getNext), env);
    }

    private static Value metaSize(ArgumentList args, Environment env) {
        int left = args.target().getAttribute("left").asInteger();
        int right = args.target().getAttribute("right").asInteger();
        return new LInteger(right - left + 1);
    }

}
