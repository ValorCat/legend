package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.NativeFunction;
import expression.value.NativeType;
import expression.value.Value;

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
