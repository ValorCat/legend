package library;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.NativeType;
import expression.value.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.ToIntFunction;

public class ListType extends NativeType {

    public ListType() {
        super("List", new String[] {"*list"},
                new NativeFunction("max", ListType::max),
                new NativeFunction("show", ListType::show),
                new NativeFunction("_index", ListType::metaIndex),
                new NativeFunction("_loop", ListType::metaLoop),
                new NativeFunction("_size", ListType::metaSize));
    }

    @Override
    protected Value initialize(ArgumentList args, Environment env) {
        List<Value> javaList = new ArrayList<>(Arrays.asList(args.args()));
        return new LObject(Type.of("List"), new NativeValue(javaList));
    }

    private static Value max(ArgumentList args, Environment env) {
        Value[] list = args.target().getAttributes();
        if (list.length == 0) {
            throw new RuntimeException("Cannot compute maximum of empty list");
        }
        ToIntFunction<Value> comparator = Value::asInteger;
        if (args.size() >= 1) {
            LFunction keyExtractor = ((LFunction) args.arg(0));
            comparator = e -> keyExtractor.call(env, e).asInteger();
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

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target().getAttribute("*list").asNative());
        return LNull.NULL;
    }

    private static Value metaIndex(ArgumentList args, Environment env) {
        int index = args.arg(0).asInteger();
        List list = (List) args.target().getAttribute("*list").asNative();
        if (index >= 0 && index < list.size()) {
            return ((Value) list.get(index));
        }
        throw new RuntimeException("Cannot get index " + index + " of list with "
                + list.size() + " item(s)");
    }

    private static Value metaLoop(ArgumentList args, Environment env) {
        LFunction hasNext = new NativeFunction("has_next", (_args, _env) -> {
            int index = _args.target().getAttribute("position").asInteger();
            Object javaList = _args.target().getAttribute("values").getAttribute("*list").asNative();
            int size = ((Collection) javaList).size();
            return LBoolean.resolve(index < size);
        });
        LFunction getNext = new NativeFunction("next", (_args, _env) -> {
            int index = _args.target().getAttribute("position").asInteger();
            Object javaList = _args.target().getAttribute("values").getAttribute("*list").asNative();
            _args.target().setAttribute("position", new LInteger(index + 1));
            // todo error if out of bounds
            return (Value) (((List) javaList).get(index));
        });
        return Type.of("Iterator").instantiate(
                new ArgumentList(args.target(), new LInteger(0), hasNext, getNext), env);
    }

    private static Value metaSize(ArgumentList args, Environment env) {
        return new LInteger(((Collection) args.target().getAttribute("*list").asNative()).size());
    }

}
