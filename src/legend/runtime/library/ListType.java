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
                .binaryOper("[]", ListType::operSubscript)
        );
    }

    @Override
    protected Value initialize(ArgumentList args) {
        List<Value> javaList = new ArrayList<>(Arrays.asList(args.args()));
        return new LObject(Type.of("List"), new LNative(javaList));
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

    private static Value operIterate(Value operand) {
        return iterator.instantiate(new ArgumentList(new LInteger(0), operand.getAttribute("*list")));
    }

    private static Value operSize(Value operand) {
        return new LInteger(((Collection) operand.getAttribute("*list").asNative()).size());
    }

    private static Value operSubscript(Value target, Value subscript) {
        List list = (List) target.getAttribute("*list").asNative();
        int index = subscript.asInteger();
        if (index >= 0 && index < list.size()) {
            return ((Value) list.get(index));
        }
        throw new RuntimeException("Cannot get index " + index + " of list with "
                + list.size() + " item(s)");
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
