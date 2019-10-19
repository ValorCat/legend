package legend.runtime;

import legend.Interpreter;
import legend.compiletime.expression.value.Value;
import legend.runtime.type.NoType;
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

    public void assign(String name, Type type, Value value) {
        checkType(value, type, name);
        Definition mapping = namespace.get(name);
        if (mapping == null) {
            define(name, type, value);
        } else {
            reassign(name, type, value, mapping);
        }
    }

    private static void checkType(Value value, Type type, String name) {
        if (!type.isSupertypeOf(value.type())) {
            throw new RuntimeException(String.format("Cannot assign %s value '%s' to %s variable '%s'",
                    value.type().getName(), value.asString(), type.getName(), name));
        }
    }

    private void define(String name, Type type, Value value) {
        if (Interpreter.strictTyping && type == NoType.NO_TYPE) {
            throw new RuntimeException("Expected type definition for variable '" + name + "'");
        }
        namespace.put(name, new Definition(type, value));
    }

    private static void reassign(String name, Type newType, Value newValue, Definition current) {
        if (newType == NoType.NO_TYPE) {
            checkType(newValue, current.type, name);
        } else if (newType != current.type) {
            throw new RuntimeException("Variable '" + name + "' already exists");
        }
        current.value = newValue;
    }

    private static class Definition {

        public Type type;
        public Value value;

        public Definition(Type type, Value value) {
            this.type = type;
            this.value = value;
        }

        public String toString() {
            return String.format("Definition(%s,%s)", type.getName(), value);
        }

    }

}
