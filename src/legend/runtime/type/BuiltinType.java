package legend.runtime.type;

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

    private final Type type;

    BuiltinType(Type type) {
        this.type = type;
    }

    public Type get() {
        return type;
    }

    public static void addAllToScope(Scope scope) {
        for (BuiltinType ref : values()) {
            Type type = ref.get();
            scope.setLocalVariable(type.getName(), new TypeValue(type));
        }
    }

}
