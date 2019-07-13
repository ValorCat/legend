package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.compiletime.expression.value.type.Type;

public class IntegerType extends BuiltinType {

    public IntegerType() {
        super(new BuiltinType.Builder("Integer")
                .shared("show", IntegerType::show)
                .shared("to", IntegerType::to)
        );
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value to(ArgumentList args) {
        ArgumentList bounds = new ArgumentList(args.scope(), args.target(), args.arg(0));
        return Type.of("Range").instantiate(bounds);
    }

}
