package legend.runtime.library;

import legend.compiletime.expression.value.BoolValue;
import legend.compiletime.expression.value.NullValue;
import legend.compiletime.expression.value.StrValue;
import legend.compiletime.expression.value.Value;
import legend.runtime.type.PrimitiveType;

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
        return new StrValue(left.asString() + right.asString());
    }

    private static Value operEquals(Value left, Value right) {
        return BoolValue.resolve(left.equals(right));
    }

    private static Value operNonNullSelect(Value left, Value right) {
        return (left == NullValue.NULL) ? right : left;
    }

    private static Value operNotEquals(Value left, Value right) {
        return BoolValue.resolve(!left.equals(right));
    }

    private static Value operNotIn(Value aggregate, Value element) {
        return aggregate.operateBinary("in", element).operateUnary("not");
    }

}
