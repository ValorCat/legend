package execute;

import dataformat.*;

import java.util.Map;
import java.util.Scanner;

import static dataformat.FunctionValue.FunctionBody;

/**
 * @since 12/24/2018
 */
public final class StandardLibrary {

    private static Scanner input;

    public static final Map<String, Type> TYPES = Map.of(
            "func", TypeBuilder.create("func").build(),
            "int", TypeBuilder.create("int").build(),
            "list", TypeBuilder.create("list").build(),
            "range", TypeBuilder.create("range").personal("left", "right").build(),
            "str", TypeBuilder.create("str").build(),
            "type", TypeBuilder.create("type").initializer((args, env) -> {
                String[] attributes = args.keywords().keySet().toArray(new String[0]);
                // todo use attribute types/bounds
                return new Type(attributes);
            }).build()
    );

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

            func("read", (args, env) -> {
                if (args.args().length > 0) {
                    System.out.print(((StringValue) args.args()[0]).getValue());
                }
                return new StringValue(input().nextLine());
            }),

            func("showenv", (args, env) -> {
                System.out.println("==[ Environment ]==================================");
                for (Map.Entry<String, Integer> pair : env.getNamespace().entrySet()) {
                    System.out.printf("\t%-8s %-4d %s\n", pair.getKey(), pair.getValue(), env.fetch(pair.getKey()));
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
