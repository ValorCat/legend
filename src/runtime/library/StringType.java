package runtime.library;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.*;
import compiletime.expression.value.function.BuiltinFunction;
import compiletime.expression.value.function.LFunction;
import compiletime.expression.value.type.BuiltinType;
import compiletime.expression.value.type.Type;
import runtime.Scope;

public class StringType extends BuiltinType {

    public StringType() {
        super("String",
                new BuiltinFunction("show", StringType::show),
                new BuiltinFunction("_index", StringType::metaIndex),
                new BuiltinFunction("_loop", StringType::metaLoop),
                new BuiltinFunction("_size", StringType::metaSize));
    }

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target().asString());
        return LNull.NULL;
    }

    private static Value metaIndex(ArgumentList args, Scope scope) {
        int index = args.arg(0).asInteger();
        String string = args.target().asString();
        if (index >= 0 && index < string.length()) {
            return new LString(string.substring(index, index + 1));
        }
        throw new RuntimeException("Cannot get index " + index + " of string of length " + string.length());
    }

    private static Value metaLoop(ArgumentList args, Scope scope) {
        LFunction hasNext = new BuiltinFunction("has_next", (_args, _scope) -> {
            int current = _args.target().getAttribute("position").asInteger();
            int size = _args.target().getAttribute("values").asString().length();
            return LBoolean.resolve(current < size);
        });
        LFunction getNext = new BuiltinFunction("next", (_args, _scope) -> {
            int current = _args.target().getAttribute("position").asInteger();
            String string = _args.target().getAttribute("values").asString();
            _args.target().setAttribute("position", new LInteger(current + 1));
            // todo error if out of range
            return new LString(string.substring(current, current + 1));
        });
        return Type.of("Iterator").instantiate(
                new ArgumentList(args.target(), new LInteger(0), hasNext, getNext), scope);
    }

    private static Value metaSize(ArgumentList args, Scope scope) {
        return new LInteger(args.target().asString().length());
    }

}