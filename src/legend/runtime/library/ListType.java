package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.*;
import legend.compiletime.expression.value.function.LFunction;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.compiletime.expression.value.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.ToIntFunction;

public class ListType extends BuiltinType {

    private static ListIteratorType iterator = new ListIteratorType();

    public ListType() {
        super(new BuiltinType.Builder("List", "Any")
                .personal("*list")
                .shared("max", ListType::max)
                .shared("show", ListType::show)
                .unaryOper("for", ListType::operIterate)
                .unaryOper("#", ListType::operSize)
                .binaryOper("+", ListType::operAppend)
                .binaryOper("in", ListType::operIn)
                .binaryOper("-", ListType::operRemove)
                .binaryOper("*", ListType::operRepeat)
                .binaryOper("[]", ListType::operSubscript)
        );
    }

    private static Value createList(Value[] elements) {
        return createList(Arrays.asList(elements));
    }

    private static Value createList(List<Value> elements) {
        return new LObject(Type.of("List"), new LNative(elements));
    }

    @Override
    protected Value initialize(ArgumentList args) {
        return createList(args.args());
    }

    private static Value max(ArgumentList args) {
        Value[] list = args.target().getAttributes();
        if (list.length == 0) {
            throw new RuntimeException("Cannot compute maximum of empty list");
        }
        ToIntFunction<Value> comparator = Value::asInteger;
        if (args.size() >= 1) {
            LFunction keyExtractor = ((LFunction) args.arg(0));
            comparator = e -> keyExtractor.call(args.scope(), e).asInteger();
        }
        Value max = list[0];
        int maxComparison = comparator.applyAsInt(max);
        for (Value element : list) {
            if (comparator.applyAsInt(element) > maxComparison) {
                max = element;
            }
        }
        return max;
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target().getAttribute("*list").asNative());
        return LNull.NULL;
    }

    private static Value operAppend(Value list, Value element) {
        List<Value> oldList = (List<Value>) list.getAttribute("*list").asNative();
        List<Value> newList = new ArrayList<>(oldList);
        newList.add(element);
        return createList(newList);
    }

    private static Value operIn(Value list, Value element) {
        List<Value> javaList = (List<Value>) list.getAttribute("*list").asNative();
        for (Value value : javaList) {
            if (value.equals(element)) {
                return LBoolean.TRUE;
            }
        }
        return LBoolean.FALSE;
    }

    private static Value operIterate(Value operand) {
        return iterator.instantiate(new ArgumentList(new LInteger(0), operand.getAttribute("*list")));
    }

    private static Value operRemove(Value list, Value element) {
        List<Value> oldList = (List<Value>) list.getAttribute("*list").asNative();
        List<Value> newList = new ArrayList<>(oldList);
        newList.remove(element);
        return createList(newList);
    }

    public static Value operRepeat(Value left, Value right) {
        List list = (List) left.getAttribute("*list").asNative();
        int multiplier = right.asInteger();
        if (multiplier < 0) {
            throw new RuntimeException("List multiplier must be non-negative, got " + multiplier);
        }

        Value[] input = (Value[]) list.toArray();
        Value[] result = new Value[input.length * multiplier];
        for (int i = 0; i < multiplier; i++) {
            System.arraycopy(input, 0, result, i * input.length, input.length);
        }
        return createList(result);
    }

    private static Value operSize(Value operand) {
        return new LInteger(((Collection) operand.getAttribute("*list").asNative()).size());
    }

    private static Value operSubscript(Value target, Value subscript) {
        List<Value> list = (List<Value>) target.getAttribute("*list").asNative();
        if (subscript.isType("Integer")) {
            int index = subscript.asInteger();
            if (index >= 0 && index < list.size()) {
                return ((Value) list.get(index));
            }
            throw new RuntimeException("Cannot get index " + index + " of list with " + list.size() + " elements(s)");
        } else if (subscript.isType("Range")) {
            int left = subscript.getAttribute("left").asInteger();
            int right = subscript.getAttribute("right").asInteger();
            if (left >= 0 && right >= 0 && left < list.size() && right < list.size() && left <= right) {
                return createList(list.subList(left, right + 1));
            }
            throw new RuntimeException("Cannot get sublist [" + left + "," + right + "] of list with " + list.size()
                    + " element(s)");
        }
        throw new RuntimeException("Cannot apply operator '[]' to types 'List' and '" + subscript.type().getName() + "'");
    }

    private static class ListIteratorType extends BuiltinType {

        public ListIteratorType() {
            super(new BuiltinType.Builder("ListIterator", "Any")
                    .personal("index", "list")
                    .unaryOper("next", ListIteratorType::operNext)
            );
        }

        private static Value operNext(Value operand) {
            int index = operand.getAttribute("index").asInteger();
            List list = (List) operand.getAttribute("list").asNative();
            if (index < list.size()) {
                operand.setAttribute("index", new LInteger(index + 1));
                return (Value) list.get(index);
            }
            return LNull.NULL;
        }

    }

}
