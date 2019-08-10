package legend.runtime;

import legend.compiletime.expression.value.Value;
import legend.runtime.type.DynamicType;
import legend.runtime.type.Type;

import java.util.HashMap;
import java.util.Map;

public class Namespace {

    private Map<String, Definition> namespace;

    public Namespace() {
        this.namespace = new HashMap<>();
    }

    public boolean defines(String name) {
        return namespace.containsKey(name);
    }

    public Value retrieve(String name) {
        return namespace.get(name).value;
    }

    public void assign(String name, Value value) {
        Definition mapping = namespace.get(name);
        if (mapping != null) {
            checkType(value, mapping.type, name);
            mapping.value = value;
        } else {
            namespace.put(name, new Definition(DynamicType.UNTYPED, value));
        }
    }

    public void assign(String name, Type type, Value value) {
        checkType(value, type, name);
        Definition mapping = namespace.get(name);
        if (mapping == null) {
            namespace.put(name, new Definition(type, value));
        } else {
            throw new RuntimeException("Variable '" + name + "' already exists");
        }
    }

    private static void checkType(Value value, Type type, String name) {
        if (!type.isSupertypeOf(value.type())) {
            throw new RuntimeException(String.format("Cannot assign %s value '%s' to %s variable '%s'",
                    value.type().getName(), value.asString(), type.getName(), name));
        }
    }

    private static class Definition {

        public Type type;
        public Value value;

        public Definition(Type type, Value value) {
            this.type = type;
            this.value = value;
        }

    }

}
