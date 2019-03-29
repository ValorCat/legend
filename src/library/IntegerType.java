package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.NativeFunction;
import expression.value.type.NativeType;
import expression.value.type.Type;

public class IntegerType extends NativeType {

    public IntegerType() {
        super("Integer",
                new NativeFunction("show", IntegerType::show),
                new NativeFunction("to", IntegerType::to));
    }

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value to(ArgumentList args, Environment env) {
        ArgumentList bounds = new ArgumentList(args.target(), args.arg(0));
        return Type.of("Range").instantiate(bounds, env);
    }

}
