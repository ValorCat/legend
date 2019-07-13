package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.type.BuiltinType;

public class IteratorType extends BuiltinType {

    public IteratorType() {
        super(new BuiltinType.Builder("Iterator")
                .personal("values", "position", "has_next", "next")
                .shared("show", IteratorType::show)
        );
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
