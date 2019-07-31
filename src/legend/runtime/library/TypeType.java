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
        super(new PrimitiveType.Builder("type", "any"));
    }

}
