package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.NativeFunction;
import expression.value.type.NativeType;

public class FunctionType extends NativeType {

    public FunctionType() {
        super("Function",
                new NativeFunction("show", FunctionType::show));
    }

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
