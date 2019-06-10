package runtime.library;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.LNull;
import compiletime.expression.value.Value;
import compiletime.expression.value.function.BuiltinFunction;
import compiletime.expression.value.type.BuiltinType;
import compiletime.expression.value.type.Type;
import runtime.Scope;

public class IntegerType extends BuiltinType {

    public IntegerType() {
        super("Integer",
                new BuiltinFunction("show", IntegerType::show),
                new BuiltinFunction("to", IntegerType::to));
    }

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value to(ArgumentList args, Scope scope) {
        ArgumentList bounds = new ArgumentList(args.target(), args.arg(0));
        return Type.of("Range").instantiate(bounds, scope);
    }

}
