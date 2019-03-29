package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.NativeFunction;
import expression.value.NativeType;
import expression.value.Value;

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
