package library;


import execute.Environment;
import expression.group.ArgumentList;
import expression.value.NativeType;
import expression.value.*;

import java.util.Scanner;

public class TypeType extends NativeType {

    private static Scanner scanner;

    public TypeType() {
        super("Type",
                new NativeFunction("read", TypeType::read),
                new NativeFunction("show", TypeType::show));
    }

    @Override
    protected Value initialize(ArgumentList args, Environment env) {
        String[] attributes = args.keywords().keySet().toArray(new String[0]);
        // todo use attribute types/bounds
        return new UserDefinedType(attributes);
    }

    private static Value read(ArgumentList args, Environment env) {
        if (args.size() > 0) {
            System.out.print(((LString) args.arg(0)).getValue());
        }
        if (!args.target().equals(Type.of("String"))) {
            throw new RuntimeException("Reading non-string types is not yet implemented");
        }
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return new LString(scanner.nextLine());
    }

    private static Value show(ArgumentList args, Environment env) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
