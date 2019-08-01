package legend.runtime.library;

import legend.compiletime.expression.value.BoolValue;
import legend.compiletime.expression.value.Value;
import legend.runtime.type.PrimitiveType;

public class BooleanType extends PrimitiveType {

    public BooleanType() {
        super(new PrimitiveType.Builder("bool")
                .unaryOper("not", BooleanType::operNot)
                .binaryOper("and", BooleanType::operAnd)
                .binaryOper("nor", BooleanType::operNor)
                .binaryOper("or", BooleanType::operOr)
        );
    }

    private static Value operAnd(Value left, Value right) {
        return BoolValue.resolve(left.asBoolean() && right.asBoolean());
    }

    private static Value operNor(Value left, Value right) {
        return BoolValue.resolve(!left.asBoolean() && !right.asBoolean());
    }

    private static Value operNot(Value operand) {
        return BoolValue.resolve(!operand.asBoolean());
    }

    private static Value operOr(Value left, Value right) {
        return BoolValue.resolve(left.asBoolean() || right.asBoolean());
    }

}
