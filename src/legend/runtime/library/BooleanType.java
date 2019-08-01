package legend.runtime.library;

import legend.compiletime.expression.type.PrimitiveType;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.Value;

public class BooleanType extends PrimitiveType {

    public BooleanType() {
        super(new PrimitiveType.Builder("bool", "any")
                .unaryOper("not", BooleanType::operNot)
                .binaryOper("and", BooleanType::operAnd)
                .binaryOper("nor", BooleanType::operNor)
                .binaryOper("or", BooleanType::operOr)
        );
    }

    private static Value operAnd(Value left, Value right) {
        return LBoolean.resolve(left.asBoolean() && right.asBoolean());
    }

    private static Value operNor(Value left, Value right) {
        return LBoolean.resolve(!left.asBoolean() && !right.asBoolean());
    }

    private static Value operNot(Value operand) {
        return LBoolean.resolve(!operand.asBoolean());
    }

    private static Value operOr(Value left, Value right) {
        return LBoolean.resolve(left.asBoolean() || right.asBoolean());
    }

}
