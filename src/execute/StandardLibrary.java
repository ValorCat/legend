package execute;

import dataformat.FunctionValue;
import dataformat.IntValue;
import dataformat.StringValue;

import java.util.Map;
import java.util.Scanner;

import static dataformat.FunctionValue.FunctionBody;

/**
 * @since 12/24/2018
 */
public final class StandardLibrary {

    private static Scanner input;

    private StandardLibrary() {}

    public static FunctionValue[] getFunctions() {
        return new FunctionValue[] {
                define("autosave", (args, env) -> args.args()[0]), // todo autosave

                define("exit", (args, env) -> {
                    System.exit(0);
                    return new IntValue(0);
                }),

                define("read", (args, env) -> {
                    if (input == null) input = new Scanner(System.in);
                    if (args.args().length > 0) {
                        System.out.print(((StringValue) args.args()[0]).getValue());
                    }
                    return new StringValue(input.nextLine());
                }),

                define("showenv", (args, env) -> {
                    System.out.println("==[ Environment ]==================================");
                    for (Map.Entry<String, Integer> pair : env.getNamespace().entrySet()) {
                        System.out.printf("\t%-8s %-4d %s\n", pair.getKey(), pair.getValue(), env.fetch(pair.getKey()));
                    }
                    System.out.println("===================================================");
                    return new IntValue(0);
                })
        };
    }

    private static FunctionValue define(String name, FunctionBody body) {
        return new FunctionValue(name, body);
    }

}
