package execute;

import dataformat.TypeBuilder;
import dataformat.value.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.ToIntFunction;

import static dataformat.value.FunctionValue.FunctionBody;

/**
 * @since 12/24/2018
 */
public final class StandardLibrary {

    private static Scanner input;

    public static final Map<String, Type> TYPES = new HashMap<>();

    static {
        TYPES.put("func", TypeBuilder.create("func").build());
        TYPES.put("bool", TypeBuilder.create("bool").build());
        TYPES.put("int", TypeBuilder.create("int").build());
        TYPES.put("list", TypeBuilder.create("list")
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
                })
                .build());
        TYPES.put("range", TypeBuilder.create("range").personal("left", "right").build());
        TYPES.put("str", TypeBuilder.create("str").build());
        TYPES.put("type", TypeBuilder.create("type").initializer((args, env) -> {
            String[] attributes = args.keywords().keySet().toArray(new String[0]);
            // todo use attribute types/bounds
            return new Type(attributes);
        }).build());
    }

    public static Type type(String type) {
        if (TYPES.containsKey(type)) {
            return TYPES.get(type);
        }
        throw new RuntimeException("No such type '" + type + "' in the standard library");
    }

    public static final FunctionValue[] FUNCTIONS = {
            func("autosave", (args, env) -> args.args()[0]), // todo autosave

            func("exit", (args, env) -> {
                System.exit(0);
                return new IntValue(0);
            }),

            func("exitif", (args, env) -> {
                if (args.args()[0].asBool()) {
                    System.exit(0);

                }
                return new IntValue(0);
            }),

            func("read", (args, env) -> {
                if (args.args().length > 0) {
                    System.out.print(((StringValue) args.args()[0]).getValue());
                }
                return new StringValue(input().nextLine());
            }),

            func("show", (args, env) -> {
                System.out.println(args.args()[0].asStr());
                return new IntValue(0);
            }),

            func("showenv", (args, env) -> {
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

    private static FunctionValue func(String name, FunctionBody body) {
        return new FunctionValue(name, body);
    }

}
