package execute;

import expression.value.type.BuiltinType;
import library.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintain the list of built-in datatypes. These are loaded into the global
 * environment at runtime when this class is loaded.
 * @see library
 * @since 3/28/19
 */
public class TypeLibrary {

    /* the types to be loaded upon initialization */
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

    /* types are stored in this map during initialization to allow for
       O(1) type name resolution */
    private static final Map<String, BuiltinType> BUILTIN_TYPES = new HashMap<>();

    static {
        // add the standard library types to the global environment
        for (BuiltinType type : BUILTIN_TYPE_ARRAY) {
            BUILTIN_TYPES.put(type.getName(), type);
            Environment.GLOBAL.assignLocal(type.getName(), type);
        }
    }

    /**
     * Retrieve a built-in type from its name. This is intended to be used only by
     * {@link expression.value.type.Type#of}, which should be used instead.
     * @param name the type name to retrieve
     * @return the type object
     * @throws RuntimeException if the specified type does not exist
     */
    public static BuiltinType getType(String name) {
        if (BUILTIN_TYPES.containsKey(name)) {
            return BUILTIN_TYPES.get(name);
        }
        throw new RuntimeException("No such type '" + name + "' in the standard library");
    }

}
