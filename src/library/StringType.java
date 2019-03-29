package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.*;
import expression.value.function.BuiltinFunction;
import expression.value.function.LFunction;
import expression.value.type.BuiltinType;
import expression.value.type.Type;

public class StringType extends BuiltinType {

    public StringType() {
        super("String",
                new BuiltinFunction("show", StringType::show),
                new BuiltinFunction("_index", StringType::metaIndex),
                new BuiltinFunction("_loop", StringType::metaLoop),
                new BuiltinFunction("_size", StringType::metaSize));
    }

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target().asString());
        return LNull.NULL;
    }

    private static Value metaIndex(ArgumentList args, Environment env) {
        int index = args.arg(0).asInteger();
        String string = args.target().asString();
        if (index >= 0 && index < string.length()) {
            return new LString(string.substring(index, index + 1));
        }
        throw new RuntimeException("Cannot get index " + index + " of string of length " + string.length());
    }

    private static Value metaLoop(ArgumentList args, Environment env) {
        LFunction hasNext = new BuiltinFunction("has_next", (_args, _env) -> {
            int current = _args.target().getAttribute("position").asInteger();
            int size = _args.target().getAttribute("values").asString().length();
            return LBoolean.resolve(current < size);
        });
        LFunction getNext = new BuiltinFunction("next", (_args, _env) -> {
            int current = _args.target().getAttribute("position").asInteger();
            String string = _args.target().getAttribute("values").asString();
            _args.target().setAttribute("position", new LInteger(current + 1));
            // todo error if out of range
            return new LString(string.substring(current, current + 1));
        });
        return Type.of("Iterator").instantiate(
                new ArgumentList(args.target(), new LInteger(0), hasNext, getNext), env);
    }

    private static Value metaSize(ArgumentList args, Environment env) {
        return new LInteger(args.target().asString().length());
    }

}
