package legend.runtime.type;

import legend.Interpreter;
import legend.compiletime.expression.value.TypeValue;
import legend.runtime.Scope;
import legend.runtime.library.*;

public enum BuiltinType implements TypeReference {

    ANY(new AnyType()),
    BOOL(new BooleanType()),
    FUNCTION(new FunctionType()),
    INT(new IntegerType()),
    LIST(new ListType()),
    NULL(new NullType()),
    RANGE(new RangeType()),
    STR(new StringType()),
    TYPE(new TypeType());

    private final RuntimeType type;

    BuiltinType(RuntimeType type) {
        this.type = type;
    }

    public RuntimeType get() {
        return type;
    }

    public static void addAllToScope(Scope scope) {
        Type metaType = Interpreter.strictTyping ? TYPE.get() : NoType.NO_TYPE;
        for (BuiltinType ref : values()) {
            RuntimeType type = ref.get();
            if (Interpreter.strictTyping) {
                scope.setLocalVariable(type.getName(), TYPE.get(), new TypeValue(type));
            } else {
                scope.setLocalVariable(type.getName(), metaType, new TypeValue(type));
            }
        }
    }

}
