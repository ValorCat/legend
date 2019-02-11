package execute;

import dataformat.ArgumentList;
import dataformat.TypeBuilder;
import dataformat.value.*;

import java.util.*;
import java.util.function.ToIntFunction;

import static dataformat.TypeBuilder.create;
import static dataformat.value.FunctionValue.FunctionBody;

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
        define(create("Boolean"));
        define(create("Function"));
        define(create("Integer")
                .shared("to", (args, env) -> {
                    ArgumentList bounds = new ArgumentList(args.target(), args.args()[0]);
                    return type("Range").instantiate(bounds, env);
                }));
        define(create("Iterator")
                .personal("values", "position", "has_next", "next"));
        define(create("List")
                .personal("*list")
                .initializer((args, env) -> {
                    List<Value> javaList = new ArrayList<>(Arrays.asList(args.args()));
                    return new ObjectValue(type("List"), new NativeValue(javaList));
                }).shared("max", (args, env) -> {
                    Value[] list = args.target().getAttributes();
                    if (list.length == 0) {
                        throw new RuntimeException("Cannot compute maximum of empty list");
                    }
                    ToIntFunction<Value> comparator = Value::asInteger;
                    if (args.args().length >= 1) {
                        FunctionValue keyExtractor = ((FunctionValue) args.args()[0]);
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
                    System.out.println(Arrays.toString(args.target().getAttributes()));
                    return NullValue.NULL;
                }).shared("iterator", (args, env) -> type("Iterator").instantiate(new ArgumentList(
                        args.target(), new IntValue(0),
                        new FunctionValue("has_next", (_args, _env) -> {
                            int index = _args.target().getAttribute("position").asInteger();
                            Object javaList = _args.target().getAttribute("values").getAttribute("*list").asNative();
                            int size = ((Collection) javaList).size();
                            return BoolValue.resolve(index < size);
                        }),
                        new FunctionValue("next", (_args, _env) -> {
                            int index = _args.target().getAttribute("position").asInteger();
                            Object javaList = _args.target().getAttribute("values").getAttribute("*list").asNative();
                            _args.target().setAttribute("position", new IntValue(index + 1));
                            // todo error if out of bounds
                            return (Value) (((List) javaList).get(index));
                        })
                ), env)));
        define(create("*Native"));
        define(create("*Null"));
        define(create("Range")
                .personal("left", "right")
                .shared("iterator", (args, env) -> type("Iterator").instantiate(new ArgumentList(
                        args.target(), args.target().getAttribute("left"),
                        new FunctionValue("has_next", (_args, _env) -> {
                            int current = _args.target().getAttribute("position").asInteger();
                            int max = _args.target().getAttribute("values").getAttribute("right").asInteger();
                            return BoolValue.resolve(current <= max);
                        }),
                        new FunctionValue("next", (_args, _env) -> {
                            Value current = _args.target().getAttribute("position");
                            _args.target().setAttribute("position", new IntValue(current.asInteger() + 1));
                            // todo error if out of range
                            return current;
                        })
                ), env)));
        define(create("String")
                .shared("iterator", (args, env) -> type("Iterator").instantiate(new ArgumentList(
                        args.target(), new IntValue(0),
                        new FunctionValue("has_next", (_args, _env) -> {
                            int current = _args.target().getAttribute("position").asInteger();
                            int size = _args.target().getAttribute("values").asString().length();
                            return BoolValue.resolve(current < size);
                        }),
                        new FunctionValue("next", (_args, _env) -> {
                            int current = _args.target().getAttribute("position").asInteger();
                            String string = _args.target().getAttribute("values").asString();
                            _args.target().setAttribute("position", new IntValue(current + 1));
                            // todo error if out of range
                            return new StringValue(string.substring(current, current + 1));
                        })
                ), env)));
        define(create("Type")
                .initializer((args, env) -> {
                    String[] attributes = args.keywords().keySet().toArray(new String[0]);
                    // todo use attribute types/bounds
                    return new Type(attributes);
                }));
    }

    public static Type type(String type) {
        if (TYPES.containsKey(type)) {
            return TYPES.get(type);
        }
        throw new RuntimeException("No such type '" + type + "' in the standard library");
    }

    public static final FunctionValue[] FUNCTIONS = {
            define("autosave", (args, env) -> args.args()[0]), // todo autosave

            define("exit", (args, env) -> {
                System.exit(0);
                return NullValue.NULL;
            }),

            define("read", (args, env) -> {
                if (args.args().length > 0) {
                    System.out.print(((StringValue) args.args()[0]).getValue());
                }
                return new StringValue(input().nextLine());
            }),

            define("show", (args, env) -> {
                System.out.println(args.args()[0].asString());
                return NullValue.NULL;
            }),

            define("showenv", (args, env) -> {
                System.out.println("==[ Environment ]==================================");
                for (Map.Entry<String, Integer> pair : env.getNamespace().entrySet()) {
                    Value value = env.fetch(pair.getKey());
                    System.out.printf("\t%-8s 0x%04X  %-6s  %s\n", pair.getKey(), pair.getValue(),
                            value.type().getName(), value);
                }
                System.out.println("===================================================");
                return NullValue.NULL;
            })
    };

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

    /**
     * Construct a new built-in function for the standard library's
     * function list.
     * @param name the function's name
     * @param body the function's body
     * @return a new function value
     */
    private static FunctionValue define(String name, FunctionBody body) {
        return new FunctionValue(name, body);
    }

}
