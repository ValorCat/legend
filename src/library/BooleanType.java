package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.NativeFunction;
import expression.value.type.NativeType;

public class BooleanType extends NativeType {

    public BooleanType() {
        super("Boolean",
                new NativeFunction("show", BooleanType::show));
    }

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
