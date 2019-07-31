package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.type.BuiltinType;

public class BooleanType extends BuiltinType {

    public BooleanType() {
        super(new BuiltinType.Builder("bool", "any")
                .shared("show", BooleanType::show)
                .unaryOper("not", BooleanType::operNot)
                .binaryOper("and", BooleanType::operAnd)
                .binaryOper("nor", BooleanType::operNor)
                .binaryOper("or", BooleanType::operOr)
        );
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target());
        return LNull.NULL;
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
