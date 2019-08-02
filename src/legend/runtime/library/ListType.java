package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.*;
import legend.runtime.type.BuiltinType;
import legend.runtime.type.ClassType;
import legend.runtime.type.PrimitiveType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListType extends PrimitiveType {

    private static ListIteratorType iterator = new ListIteratorType();

    public ListType() {
        super(new PrimitiveType.Builder("list")
                .unaryOper("for", ListType::operIterate)
                .unaryOper("#", ListType::operSize)
                .binaryOper("+", ListType::operAppend)
                .binaryOper("in", ListType::operIn)
                .binaryOper("-", ListType::operRemove)
                .binaryOper("*", ListType::operRepeat)
                .binaryOper("[]", ListType::operSubscript)
                .binaryOper("where", ListType::operWhere)
        );
    }

    @Override
    public Value buildNew(ArgumentList args) {
        return new ListValue(Arrays.asList(args.args()));
    }

    private static Value operAppend(Value list, Value element) {
        List<Value> oldList = list.asList();
        List<Value> newList = new ArrayList<>(oldList);
        newList.add(element);
        return new ListValue(newList);
    }

    private static Value operIn(Value list, Value element) {
        List<Value> javaList = list.asList();
        for (Value value : javaList) {
            if (value.equals(element)) {
                return BoolValue.TRUE;
            }
        }
        return BoolValue.FALSE;
    }

    private static Value operIterate(Value operand) {
        return iterator.buildNew(new ArgumentList(new IntValue(0), operand));
    }

    private static Value operRemove(Value list, Value element) {
        List<Value> oldList = list.asList();
        List<Value> newList = new ArrayList<>(oldList);
        newList.remove(element);
        return new ListValue(newList);
    }

    public static Value operRepeat(Value left, Value right) {
        List<Value> list = left.asList();
        int multiplier = right.asInteger();
        if (multiplier < 0) {
            throw new RuntimeException("List multiplier must be non-negative, got " + multiplier);
        }

        Value[] input = (Value[]) list.toArray();
        Value[] result = new Value[input.length * multiplier];
        for (int i = 0; i < multiplier; i++) {
            System.arraycopy(input, 0, result, i * input.length, input.length);
        }
        return new ListValue(Arrays.asList(result));
    }

    private static Value operSize(Value operand) {
        return new IntValue(operand.asList().size());
    }

    private static Value operSubscript(Value target, Value subscript) {
        List<Value> list = target.asList();
        if (subscript.isType(BuiltinType.INT)) {
            int index = subscript.asInteger();
            if (index >= 0 && index < list.size()) {
                return list.get(index);
            }
            throw new RuntimeException("Cannot get index " + index + " of list with " + list.size() + " elements(s)");
        } else if (subscript.isType(BuiltinType.RANGE)) {
            int left = subscript.getAttribute("left").asInteger();
            int right = subscript.getAttribute("right").asInteger();
            if (left >= 0 && right >= 0 && left < list.size() && right < list.size() && left <= right) {
                return new ListValue(list.subList(left, right + 1));
            }
            throw new RuntimeException("Cannot get sublist [" + left + "," + right + "] of list with " + list.size()
                    + " element(s)");
        }
        throw new RuntimeException("Cannot apply operator '[]' to types 'list' and '" + subscript.type().getName() + "'");
    }

    private static Value operWhere(Value left, Value right) {
        List<Value> input = left.asList();
        FunctionValue predicate = (FunctionValue) right;
        List<Value> output = new ArrayList<>();
        for (Value element : input) {
            if (predicate.call(new ArgumentList(element)).asBoolean()) {
                output.add(element);
            }
        }
        return new ListValue(output);
    }

    private static class ListIteratorType extends ClassType {

        public ListIteratorType() {
            super(new ClassType.Builder("ListIterator")
                    .personal("index", "list")
                    .unaryOper("next", ListIteratorType::operNext)
            );
        }

        private static Value operNext(Value operand) {
            int index = operand.getAttribute("index").asInteger();
            List<Value> list = operand.getAttribute("list").asList();
            if (index < list.size()) {
                operand.setAttribute("index", new IntValue(index + 1));
                return list.get(index);
            }
            return NullValue.NULL;
        }

    }

}
