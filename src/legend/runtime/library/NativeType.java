package legend.runtime.library;

import legend.compiletime.expression.value.type.BuiltinType;

public class NativeType extends BuiltinType {

    public NativeType() {
        super("*native", NO_PARENT);
    }

}
