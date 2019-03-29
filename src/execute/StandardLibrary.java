package execute;

import expression.group.ArgumentList;
import expression.value.*;

import java.util.*;
import java.util.function.ToIntFunction;

import static expression.value.NativeFunction.FunctionBody;
import static expression.value.TypeBuilder.create;

/**
 * Maintains a list of built-in types and functions. These values are
 * inserted into the global environment before user source code is
 * executed.
 * @since 12/24/2018
 */
public final class StandardLibrary {

    private static Scanner input;

    public static final Map<String, Type> TYPES = new HashMap<>();

    static {
        // create is TypeBuilder.create
        define(create("Boolean")
                .shared("show", (args, env) -> {
                    System.out.println(args.target());
                    return LNull.NULL;
                }));
        define(create("Function")
                .shared("show", (args, env) -> {
                    System.out.println(args.target());
                    return LNull.NULL;
                }));
        define(create("Integer")
                .shared("show", (args, env) -> {
                    System.out.println(args.target());
                    return LNull.NULL;
                }).shared("to", (args, env) -> {
                    ArgumentList bounds = new ArgumentList(args.target(), args.arg(0));
                    return type("Range").instantiate(bounds, env);
                }));
        define(create("Iterator")
                .personal("values", "position", "has_next", "next")
                .shared("show", (args, env) -> {
                    System.out.println(args.target());
                    return LNull.NULL;
                }));
        define(create("List")
                .personal("*list")
                .initializer((args, env) -> {
                    List<Value> javaList = new ArrayList<>(Arrays.asList(args.args()));
                    return new LObject(type("List"), new NativeValue(javaList));
                }).shared("max", (args, env) -> {
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
                }).shared("show", (args, env) -> {
                    System.out.println(args.target().getAttribute("*list").asNative());
                    return LNull.NULL;
                }).shared("_loop", (args, env) -> type("Iterator").instantiate(new ArgumentList(
                        args.target(), new LInteger(0),
                        new NativeFunction("has_next", (_args, _env) -> {
                            int index = _args.target().getAttribute("position").asInteger();
                            Object javaList = _args.target().getAttribute("values").getAttribute("*list").asNative();
                            int size = ((Collection) javaList).size();
                            return LBoolean.resolve(index < size);
                        }),
                        new NativeFunction("next", (_args, _env) -> {
                            int index = _args.target().getAttribute("position").asInteger();
                            Object javaList = _args.target().getAttribute("values").getAttribute("*list").asNative();
                            _args.target().setAttribute("position", new LInteger(index + 1));
                            // todo error if out of bounds
                            return (Value) (((List) javaList).get(index));
                        })
                ), env))
                .shared("_index", (args, env) -> {
                    int index = args.arg(0).asInteger();
                    List list = (List) args.target().getAttribute("*list").asNative();
                    if (index >= 0 && index < list.size()) {
                        return ((Value) list.get(index));
                    }
                    throw new RuntimeException("Cannot get index " + index + " of list with "
                            + list.size() + " item(s)");
                }).shared("_size", (args, env) -> new LInteger(((Collection) args.target().getAttribute("*list")
                        .asNative()).size())));
        define(create("*Native"));
        define(create("*Null")
                .shared("show", (args, env) -> {
                    System.out.println(args.target());
                    return LNull.NULL;
                }));
        define(create("Range")
                .personal("left", "right")
                .shared("contains", (args, env) -> {
                    int value = args.arg(0).asInteger();
                    int left = args.target().getAttribute("left").asInteger();
                    int right = args.target().getAttribute("right").asInteger();
                    return LBoolean.resolve(value >= left && value <= right);
                }).shared("show", (args, env) -> {
                    System.out.println(args.target());
                    return LNull.NULL;
                }).shared("_loop", (args, env) -> type("Iterator").instantiate(new ArgumentList(
                        args.target(), args.target().getAttribute("left"),
                        new NativeFunction("has_next", (_args, _env) -> {
                            int current = _args.target().getAttribute("position").asInteger();
                            int max = _args.target().getAttribute("values").getAttribute("right").asInteger();
                            return LBoolean.resolve(current <= max);
                        }),
                        new NativeFunction("next", (_args, _env) -> {
                            Value current = _args.target().getAttribute("position");
                            _args.target().setAttribute("position", new LInteger(current.asInteger() + 1));
                            // todo error if out of range
                            return current;
                        })
                ), env))
                .shared("_size", (args, env) -> {
                    int left = args.target().getAttribute("left").asInteger();
                    int right = args.target().getAttribute("right").asInteger();
                    return new LInteger(right - left + 1);
                }));
        define(create("String")
                .shared("show", (args, env) -> {
                    System.out.println(args.target());
                    return LNull.NULL;
                }).shared("_loop", (args, env) -> type("Iterator").instantiate(new ArgumentList(
                        args.target(), new LInteger(0),
                        new NativeFunction("has_next", (_args, _env) -> {
                            int current = _args.target().getAttribute("position").asInteger();
                            int size = _args.target().getAttribute("values").asString().length();
                            return LBoolean.resolve(current < size);
                        }),
                        new NativeFunction("next", (_args, _env) -> {
                            int current = _args.target().getAttribute("position").asInteger();
                            String string = _args.target().getAttribute("values").asString();
                            _args.target().setAttribute("position", new LInteger(current + 1));
                            // todo error if out of range
                            return new LString(string.substring(current, current + 1));
                        })
                ), env))
                .shared("_index", (args, env) -> {
                    int index = args.arg(0).asInteger();
                    String string = args.target().asString();
                    if (index >= 0 && index < string.length()) {
                        return new LString(string.substring(index, index + 1));
                    }
                    throw new RuntimeException("Cannot get index " + index + " of string of length " + string.length());
                }).shared("_size", (args, env) -> new LInteger(args.target().asString().length())));
        define(create("Type")
                .initializer((args, env) -> {
                    String[] attributes = args.keywords().keySet().toArray(new String[0]);
                    // todo use attribute types/bounds
                    return new UserDefinedType(attributes);
                }).shared("read", (args, env) -> {
                    if (args.size() > 0) {
                        System.out.print(((LString) args.arg(0)).getValue());
                    }
                    if (!args.target().equals(type("String"))) {
                        throw new RuntimeException("Reading non-string types is not yet implemented");
                    }
                    return new LString(input().nextLine());
                }).shared("show", (args, env) -> {
                    System.out.println(args.target());
                    return LNull.NULL;
                }));
    }

    public static Type type(String type) {
        if (TYPES.containsKey(type)) {
            return TYPES.get(type);
        }
        throw new RuntimeException("No such type '" + type + "' in the standard library");
    }

    private StandardLibrary() {}

    private static Scanner input() {
        return input != null ? input : (input = new Scanner(System.in));
    }

    /**
     * Add the new built-in type specified by this type builder to the
     * standard library's type list.
     * @param builder a type builder
     */
    private static void define(TypeBuilder builder) {
        Type type = builder.build();
        TYPES.put(type.getName(), type);
    }

}
