package legend.runtime;

import legend.compiletime.expression.value.type.BuiltinType;
import legend.runtime.library.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintain the list of built-in datatypes. These are loaded into the global
 * namespace at runtime when this class is loaded.
 * @see legend.runtime.library
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

    public static void updateNamespace(Scope namespace) {
        // add the standard library types to the global namespace
        for (BuiltinType type : BUILTIN_TYPE_ARRAY) {
            BUILTIN_TYPES.put(type.getName(), type);
            namespace.setLocalVariable(type.getName(), type);
        }
    }

    /**
     * Retrieve a built-in type from its name. This is intended to be used only by
     * {@link legend.compiletime.expression.value.type.Type#of}, which should be used instead.
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
