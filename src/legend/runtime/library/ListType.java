package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.*;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.compiletime.expression.value.function.LFunction;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.compiletime.expression.value.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.ToIntFunction;

public class ListType extends BuiltinType {

    public ListType() {
        super(new BuiltinType.Builder("List")
                .personal("*list")
                .shared("max", ListType::max)
                .shared("show", ListType::show)
                .operation("[]", ListType::operIndex)
                .operation("for", ListType::operIterate)
                .operation("#", ListType::operLength)
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

    private static Value operIndex(ArgumentList args) {
        List list = (List) args.arg(0).getAttribute("*list").asNative();
        int index = args.arg(1).asInteger();
        if (index >= 0 && index < list.size()) {
            return ((Value) list.get(index));
        }
        throw new RuntimeException("Cannot get index " + index + " of list with "
                + list.size() + " item(s)");
    }

    private static Value operIterate(ArgumentList args) {
        LFunction hasNext = new BuiltinFunction("has_next", _args -> {
            int index = _args.target().getAttribute("position").asInteger();
            Object javaList = _args.target().getAttribute("values").getAttribute("*list").asNative();
            int size = ((Collection) javaList).size();
            return LBoolean.resolve(index < size);
        });
        LFunction getNext = new BuiltinFunction("next", _args -> {
            int index = _args.target().getAttribute("position").asInteger();
            Object javaList = _args.target().getAttribute("values").getAttribute("*list").asNative();
            _args.target().setAttribute("position", new LInteger(index + 1));
            // todo error if out of bounds
            return (Value) (((List) javaList).get(index));
        });
        return Type.of("Iterator").instantiate(
                new ArgumentList(args.scope(), args.arg(0), new LInteger(0), hasNext, getNext));
    }

    private static Value operLength(ArgumentList args) {
        return new LInteger(((Collection) args.arg(0).getAttribute("*list").asNative()).size());
    }

}
