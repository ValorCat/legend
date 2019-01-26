package execute;

import dataformat.ArgumentList;
import dataformat.TypeBuilder;
import dataformat.value.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
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
        define(create("bool"));
        define(create("func"));
        define(create("int")
                .shared("to", (args, env) -> {
                    ArgumentList bounds = new ArgumentList(args.target(), args.args()[0]);
                    return TYPES.get("range").instantiate(bounds, env);
                }));
        define(create("list")
                .shared("max", (args, env) -> {
                    Value[] list = args.target().getAttributes();
                    if (list.length == 0) {
                        throw new RuntimeException("Cannot compute maximum of empty list");
                    }
                    ToIntFunction<Value> comparator = Value::asInt;
                    if (args.args().length >= 1) {
                        FunctionValue keyExtractor = ((FunctionValue) args.args()[0]);
                        comparator = e -> keyExtractor.call(env, e).asInt();
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
                    return new IntValue(0);
                }));
        define(create("range")
                .personal("left", "right"));
        define(create("str"));
        define(create("type")
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
                return new IntValue(0);
            }),

            define("exitif", (args, env) -> {
                if (args.args()[0].asBool()) {
                    System.exit(0);

                }
                return new IntValue(0);
            }),

            define("read", (args, env) -> {
                if (args.args().length > 0) {
                    System.out.print(((StringValue) args.args()[0]).getValue());
                }
                return new StringValue(input().nextLine());
            }),

            define("show", (args, env) -> {
                System.out.println(args.args()[0].asStr());
                return new IntValue(0);
            }),

            define("showenv", (args, env) -> {
                System.out.println("==[ Environment ]==================================");
                for (Map.Entry<String, Integer> pair : env.getNamespace().entrySet()) {
                    Value value = env.fetch(pair.getKey());
                    System.out.printf("\t%-8s 0x%04X  %-6s  %s\n", pair.getKey(), pair.getValue(),
                            value.type().getName(), value);
                }
                System.out.println("===================================================");
                return new IntValue(0);
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
