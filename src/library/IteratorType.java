package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;
import expression.value.function.BuiltinFunction;
import expression.value.type.BuiltinType;

public class IteratorType extends BuiltinType {

    public IteratorType() {
        super("Iterator", new String[] {"values", "position", "has_next", "next"},
                new BuiltinFunction("show", IteratorType::show));
    }

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
