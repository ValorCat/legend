package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.BoolValue;
import legend.compiletime.expression.value.IntValue;
import legend.compiletime.expression.value.Value;
import legend.runtime.type.BuiltinType;
import legend.runtime.type.PrimitiveType;

public class IntegerType extends PrimitiveType {

    public IntegerType() {
        super(new PrimitiveType.Builder("int")
                .unaryOper("-", IntegerType::operNegate)
                .unaryOper("%", IntegerType::operPercent)
                .binaryOper("+", IntegerType::operAdd)
                .binaryOper("/", IntegerType::operDivide)
                .binaryOper("//", IntegerType::operIntDivide)
                .binaryOper(">", IntegerType::operGreaterThan)
                .binaryOper(">=", IntegerType::operGreaterThanOrEqual)
                .binaryOper("<", IntegerType::operLessThan)
                .binaryOper("<=", IntegerType::operLessThanOrEqual)
                .binaryOper("*", IntegerType::operMultiply)
                .binaryOper("mod", IntegerType::operMod)
                .binaryOper("^", IntegerType::operPower)
                .binaryOper("-", IntegerType::operSubtract)
                .binaryOper("to", IntegerType::operTo)
        );
    }

    private static Value operAdd(Value left, Value right) {
        return new IntValue(left.asInteger() + right.asInteger());
    }

    private static Value operDivide(Value left, Value right) {
        return new IntValue(Math.round((float) left.asInteger() / right.asInteger()));
    }

    private static Value operGreaterThan(Value left, Value right) {
        return BoolValue.resolve(left.asInteger() > right.asInteger());
    }

    private static Value operGreaterThanOrEqual(Value left, Value right) {
        return BoolValue.resolve(left.asInteger() >= right.asInteger());
    }

    private static Value operIntDivide(Value left, Value right) {
        return new IntValue(left.asInteger() / right.asInteger());
    }

    private static Value operLessThan(Value left, Value right) {
        return BoolValue.resolve(left.asInteger() < right.asInteger());
    }

    private static Value operLessThanOrEqual(Value left, Value right) {
        return BoolValue.resolve(left.asInteger() <= right.asInteger());
    }

    private static Value operMod(Value left, Value right) {
        return new IntValue(left.asInteger() % right.asInteger());
    }

    private static Value operMultiply(Value left, Value right) {
        if (right.isType(BuiltinType.LIST)) {
            return ListType.operRepeat(right, left);
        }
        return new IntValue(left.asInteger() * right.asInteger());
    }

    private static Value operNegate(Value operand) {
        return new IntValue(-operand.asInteger());
    }

    private static Value operPercent(Value operand) {
        return new IntValue(operand.asInteger() / 100);
    }

    private static Value operPower(Value left, Value right) {
        return new IntValue((int) Math.pow(left.asInteger(), right.asInteger()));
    }

    private static Value operSubtract(Value left, Value right) {
        return new IntValue(left.asInteger() - right.asInteger());
    }

    private static Value operTo(Value left, Value right) {
        return BuiltinType.RANGE.get().buildNew(new ArgumentList(left, right));
    }

}
