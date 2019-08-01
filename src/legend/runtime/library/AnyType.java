package legend.runtime.library;

import legend.compiletime.expression.type.PrimitiveType;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.LString;
import legend.compiletime.expression.value.Value;

public class AnyType extends PrimitiveType {

    public AnyType() {
        super(new Builder("any", null)
                .binaryOper("&", AnyType::operConcat)
                .binaryOper("==", AnyType::operEquals)
                .binaryOper("?", AnyType::operNonNullSelect)
                .binaryOper("!=", AnyType::operNotEquals)
                .binaryOper("not in", AnyType::operNotIn)
        );
    }

    private static Value operConcat(Value left, Value right) {
        return new LString(left.asString() + right.asString());
    }

    private static Value operEquals(Value left, Value right) {
        return LBoolean.resolve(left.equals(right));
    }

    private static Value operNonNullSelect(Value left, Value right) {
        return (left == LNull.NULL) ? right : left;
    }

    private static Value operNotEquals(Value left, Value right) {
        return LBoolean.resolve(!left.equals(right));
    }

    private static Value operNotIn(Value aggregate, Value element) {
        return aggregate.operateBinary("in", element).operateUnary("not");
    }

}
