package library;

import execute.Scope;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.BuiltinFunction;
import expression.value.type.BuiltinType;
import expression.value.type.Type;

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
