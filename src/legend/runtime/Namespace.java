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
            checkType(value, mapping.type);
            mapping.value = value;
        } else {
            namespace.put(name, new Definition(DynamicType.UNTYPED, value));
        }
    }

    public void assign(String name, Type type, Value value) {
        Definition mapping = namespace.get(name);
        if (mapping != null) {
            checkType(value, mapping.type);
            mapping.type = type;
            mapping.value = value;
        } else {
            namespace.put(name, new Definition(type, value));
        }
    }

    private static void checkType(Value value, Type type) {
        if (!type.isSupertypeOf(value.type())) {
            throw new RuntimeException("Cannot assign '" + value.asString() + "' (type '" + value.type().getName()
                    + "') to '" + type.getName() + "' variable");
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
