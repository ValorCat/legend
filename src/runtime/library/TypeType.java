package runtime.library;


import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.LNull;
import compiletime.expression.value.LString;
import compiletime.expression.value.Value;
import compiletime.expression.value.function.BuiltinFunction;
import compiletime.expression.value.type.BuiltinType;
import compiletime.expression.value.type.Type;
import compiletime.expression.value.type.UserDefinedType;
import runtime.Scope;

import java.util.Scanner;

public class TypeType extends BuiltinType {

    private static Scanner scanner;

    public TypeType() {
        super("Type",
                new BuiltinFunction("read", TypeType::read),
                new BuiltinFunction("show", TypeType::show));
    }

    @Override
    protected Value initialize(ArgumentList args, Scope scope) {
        String[] attributes = args.keywords().keySet().toArray(new String[0]);
        // todo use attribute types/bounds
        return new UserDefinedType(attributes);
    }

    private static Value read(ArgumentList args, Scope scope) {
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

    private static Value show(ArgumentList args, Scope scope) {
        System.out.println(args.target());
        return LNull.NULL;
    }

}
