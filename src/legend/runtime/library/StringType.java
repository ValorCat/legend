package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.*;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.compiletime.expression.value.function.LFunction;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.compiletime.expression.value.type.Type;
import legend.runtime.Scope;

public class StringType extends BuiltinType {

    public StringType() {
        super(new BuiltinType.Builder("String")
                .shared("show", StringType::show)
                .operation("[]", StringType::operIndex)
                .operation("for", StringType::operIterate)
                .operation("#", StringType::operLength)
        );
    }

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target().asString());
        return LNull.NULL;
    }

    private static Value operIndex(ArgumentList args, Scope scope) {
        String string = args.arg(0).asString();
        int index = args.arg(1).asInteger();
        if (index >= 0 && index < string.length()) {
            return new LString(string.substring(index, index + 1));
        }
        throw new RuntimeException("Cannot get index " + index + " of string of length " + string.length());
    }

    private static Value operIterate(ArgumentList args, Scope scope) {
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
                new ArgumentList(args.arg(0), new LInteger(0), hasNext, getNext), scope);
    }

    private static Value operLength(ArgumentList args, Scope scope) {
        return new LInteger(args.arg(0).asString().length());
    }

}
