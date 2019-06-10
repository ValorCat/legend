package runtime.library;

import compiletime.expression.value.type.BuiltinType;

public class NativeType extends BuiltinType {

    public NativeType() {
        super("*Native");
    }

}
