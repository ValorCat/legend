package library;

import execute.Scope;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.BuiltinFunction;
import expression.value.type.BuiltinType;

public class BooleanType extends BuiltinType {

    public BooleanType() {
        super("Boolean",
                new BuiltinFunction("show", BooleanType::show));
    }

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
