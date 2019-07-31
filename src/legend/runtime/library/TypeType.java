package legend.runtime.library;


import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.LString;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.type.BuiltinType;
import legend.compiletime.expression.value.type.NullableType;
import legend.compiletime.expression.value.type.Type;

import java.util.Scanner;

public class TypeType extends BuiltinType {

    private static Scanner scanner;

    public TypeType() {
        super(new BuiltinType.Builder("type", "any")
                .shared("read", TypeType::read)
                .shared("show", TypeType::show)
                .unaryOper("?", TypeType::operNullify)
        );
    }

    private static Value read(ArgumentList args) {
        if (args.size() > 0) {
            System.out.print(args.arg(0).asString());
        }
        if (!args.target().equals(Type.of("str"))) {
            throw new RuntimeException("Reading non-string types is not yet implemented");
        }
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return new LString(scanner.nextLine());
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target());
        return LNull.NULL;
    }

    private static Value operNullify(Value operand) {
        return new NullableType((Type) operand);
    }

}
