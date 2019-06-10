package runtime.library;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.LNull;
import compiletime.expression.value.Value;
import compiletime.expression.value.function.BuiltinFunction;
import compiletime.expression.value.type.BuiltinType;
import runtime.Scope;

public class NullType extends BuiltinType {

    public NullType() {
        super("*Null",
                new BuiltinFunction("show", NullType::show));
    }

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
