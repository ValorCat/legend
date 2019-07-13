package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.runtime.Scope;

public class NullType extends BuiltinType {

    public NullType() {
        super(new BuiltinType.Builder("*Null")
                .shared("show", NullType::show)
        );
    }

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
