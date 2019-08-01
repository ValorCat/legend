package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.type.ClassType;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.LInteger;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;

public class RangeType extends ClassType {

    private static RangeIteratorType iterator = new RangeIteratorType();

    public RangeType() {
        super(new ClassType.Builder("range", "any")
                .personal("left", "right")
                .shared("show", RangeType::show)
                .unaryOper("for", RangeType::operIterate)
                .unaryOper("#", RangeType::operSize)
                .binaryOper("in", RangeType::operIn)
        );
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value operIn(Value range, Value element) {
        int left = range.getAttribute("left").asInteger();
        int right = range.getAttribute("right").asInteger();
        int value = element.asInteger();
        int min = Math.min(left, right);
        int max = Math.max(left, right);
        return LBoolean.resolve(value >= min && value <= max);
    }

    private static Value operIterate(Value operand) {
        return iterator.buildNew(new ArgumentList(operand.getAttribute("left"), operand.getAttribute("right")));
    }

    private static Value operSize(Value operand) {
        int left = operand.getAttribute("left").asInteger();
        int right = operand.getAttribute("right").asInteger();
        return new LInteger(right - left + 1);
    }

    private static class RangeIteratorType extends ClassType {

        public RangeIteratorType() {
            super(new ClassType.Builder("RangeIterator", "any")
                    .personal("pos", "end")
                    .unaryOper("next", RangeIteratorType::operNext)
            );
        }

        private static Value operNext(Value operand) {
            Value pos = operand.getAttribute("pos");
            int end = operand.getAttribute("end").asInteger();
            if (pos.asInteger() <= end) {
                operand.setAttribute("pos", new LInteger(pos.asInteger() + 1));
                return pos;
            }
            return LNull.NULL;
        }

    }

}
