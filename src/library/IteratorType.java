package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.NativeFunction;
import expression.value.type.NativeType;

public class IteratorType extends NativeType {

    public IteratorType() {
        super("Iterator", new String[] {"values", "position", "has_next", "next"},
                new NativeFunction("show", IteratorType::show));
    }

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
