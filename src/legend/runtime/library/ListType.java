package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.*;
import legend.compiletime.expression.value.function.LFunction;
import legend.compiletime.expression.value.type.BuiltinType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListType extends BuiltinType {

    private static ListIteratorType iterator = new ListIteratorType();

    public ListType() {
        super(new BuiltinType.Builder("list", "any")
                .shared("show", ListType::show)
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
        return new LList(Arrays.asList(args.args()));
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target().asString());
        return LNull.NULL;
    }

    private static Value operAppend(Value list, Value element) {
        List<Value> oldList = list.asList();
        List<Value> newList = new ArrayList<>(oldList);
        newList.add(element);
        return new LList(newList);
    }

    private static Value operIn(Value list, Value element) {
        List<Value> javaList = list.asList();
        for (Value value : javaList) {
            if (value.equals(element)) {
                return LBoolean.TRUE;
            }
        }
        return LBoolean.FALSE;
    }

    private static Value operIterate(Value operand) {
        return iterator.buildNew(new ArgumentList(new LInteger(0), operand));
    }

    private static Value operRemove(Value list, Value element) {
        List<Value> oldList = list.asList();
        List<Value> newList = new ArrayList<>(oldList);
        newList.remove(element);
        return new LList(newList);
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
        return new LList(Arrays.asList(result));
    }

    private static Value operSize(Value operand) {
        return new LInteger(operand.asList().size());
    }

    private static Value operSubscript(Value target, Value subscript) {
        List<Value> list = target.asList();
        if (subscript.isType("int")) {
            int index = subscript.asInteger();
            if (index >= 0 && index < list.size()) {
                return list.get(index);
            }
            throw new RuntimeException("Cannot get index " + index + " of list with " + list.size() + " elements(s)");
        } else if (subscript.isType("range")) {
            int left = subscript.getAttribute("left").asInteger();
            int right = subscript.getAttribute("right").asInteger();
            if (left >= 0 && right >= 0 && left < list.size() && right < list.size() && left <= right) {
                return new LList(list.subList(left, right + 1));
            }
            throw new RuntimeException("Cannot get sublist [" + left + "," + right + "] of list with " + list.size()
                    + " element(s)");
        }
        throw new RuntimeException("Cannot apply operator '[]' to types 'list' and '" + subscript.type().getName() + "'");
    }

    private static Value operWhere(Value left, Value right) {
        List<Value> input = left.asList();
        LFunction predicate = (LFunction) right;
        List<Value> output = new ArrayList<>();
        for (Value element : input) {
            if (predicate.call(new ArgumentList(element)).asBoolean()) {
                output.add(element);
            }
        }
        return new LList(output);
    }

    private static class ListIteratorType extends BuiltinType {

        public ListIteratorType() {
            super(new BuiltinType.Builder("ListIterator", "any")
                    .personal("index", "list")
                    .unaryOper("next", ListIteratorType::operNext)
            );
        }

        private static Value operNext(Value operand) {
            int index = operand.getAttribute("index").asInteger();
            List<Value> list = operand.getAttribute("list").asList();
            if (index < list.size()) {
                operand.setAttribute("index", new LInteger(index + 1));
                return list.get(index);
            }
            return LNull.NULL;
        }

    }

}
