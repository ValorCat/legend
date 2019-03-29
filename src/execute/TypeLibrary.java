package execute;

import expression.value.type.BuiltinType;
import library.*;

import java.util.HashMap;
import java.util.Map;

public class TypeLibrary {

    private static final BuiltinType[] BUILTIN_TYPE_ARRAY = {
            new BooleanType(),
            new FunctionType(),
            new IntegerType(),
            new IteratorType(),
            new ListType(),
            new NativeType(),
            new NullType(),
            new RangeType(),
            new StringType(),
            new TypeType()
    };

    private static final Map<String, BuiltinType> BUILTIN_TYPES = new HashMap<>();

    static {
        // add the standard library types to the global environment
        for (BuiltinType type : BUILTIN_TYPE_ARRAY) {
            BUILTIN_TYPES.put(type.getName(), type);
            Environment.GLOBAL.assignLocal(type.getName(), type);
        }
    }

    public static BuiltinType getType(String name) {
        if (BUILTIN_TYPES.containsKey(name)) {
            return BUILTIN_TYPES.get(name);
        }
        throw new RuntimeException("No such type '" + name + "' in the standard library");
    }

}
