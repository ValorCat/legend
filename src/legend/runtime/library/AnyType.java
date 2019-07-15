package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.LString;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.type.BuiltinType;

public class AnyType extends BuiltinType {

    public AnyType() {
        super(new Builder("Any", NO_PARENT)
                .shared("show", AnyType::show)
                .binaryOper("&", AnyType::operConcat)
                .binaryOper("==", AnyType::operEquals)
                .binaryOper("!=", AnyType::operNotEquals)
        );
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value operConcat(Value left, Value right) {
        return new LString(left.asString() + right.asString());
    }

    private static LBoolean operEquals(Value left, Value right) {
        return LBoolean.resolve(left.equals(right));
    }

    private static Value operNotEquals(Value left, Value right) {
        return operEquals(left, right).not();
    }

}
