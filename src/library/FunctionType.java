package library;

import execute.Scope;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.BuiltinFunction;
import expression.value.type.BuiltinType;

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
