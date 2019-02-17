package execute;

import dataformat.ArgumentList;
import dataformat.TypeBuilder;
import dataformat.value.*;

import java.util.*;
import java.util.function.ToIntFunction;

import static dataformat.TypeBuilder.create;
import static dataformat.value.NativeFunction.FunctionBody;

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
                    ArgumentList bounds = new ArgumentList(args.target(), args.arg(0));
                    return type("Range").instantiate(bounds, env);
                }));
        define(create("Iterator")
                .personal("values", "position", "has_next", "next"));
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
                    System.out.println(Arrays.toString(args.target().getAttributes()));
                    return LNull.NULL;
                }).shared("iterator", (args, env) -> type("Iterator").instantiate(new ArgumentList(
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
                ), env)));
        define(create("*Native"));
        define(create("*Null"));
        define(create("Range")
                .personal("left", "right")
                .shared("iterator", (args, env) -> type("Iterator").instantiate(new ArgumentList(
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
                ), env)));
        define(create("String")
                .shared("iterator", (args, env) -> type("Iterator").instantiate(new ArgumentList(
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

    public static final LFunction[] FUNCTIONS = {
            define("autosave", (args, env) -> args.arg(0)), // todo autosave

            define("exit", (args, env) -> {
                System.exit(0);
                return LNull.NULL;
            }),

            define("read", (args, env) -> {
                if (args.size() > 0) {
                    System.out.print(((LString) args.arg(0)).getValue());
                }
                return new LString(input().nextLine());
            }),

            define("show", (args, env) -> {
                System.out.println(args.arg(0).asString());
                return LNull.NULL;
            }),

            define("showenv", (args, env) -> {
                System.out.println("==[ Environment ]==================================");
                for (Map.Entry<String, Integer> pair : env.getNamespace().entrySet()) {
                    Value value = env.fetch(pair.getKey());
                    System.out.printf("\t%-8s 0x%04X  %-6s  %s\n", pair.getKey(), pair.getValue(),
                            value.type().getName(), value);
                }
                System.out.println("===================================================");
                return LNull.NULL;
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
    private static LFunction define(String name, FunctionBody body) {
        return new NativeFunction(name, body);
    }

}
