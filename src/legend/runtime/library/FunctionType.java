package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.runtime.Scope;

public class FunctionType extends BuiltinType {

    public FunctionType() {
        super("Function",
                new BuiltinFunction("show", FunctionType::show));
    }

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
