package legend.compiletime.expression.value.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LObject;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.compiletime.expression.value.function.BuiltinFunction.FunctionBody;
import legend.compiletime.expression.value.function.LFunction;
import legend.runtime.Scope;

import java.util.HashMap;
import java.util.Map;

public abstract class BuiltinType extends Type {

    public BuiltinType(String name) {
        super(name, new String[0], Map.of(), Map.of());
    }

    public BuiltinType(Builder builder) {
        super(builder.name, builder.personal, builder.shared, builder.operations);
    }

    @Override
    public Value instantiate(ArgumentList args, Scope scope) {
        Value instance = initialize(args, scope);
        if (instance == null) {
            return new LObject(this, args.args());
        }
        instance.setType(this);
        return instance;
    }

    @Override
    protected boolean setShared(String attribute, Value value) {
        throw new RuntimeException("Attribute '" + attribute + "' of type '" + name + "' is read-only");
    }

    @Override
    public void deanonymize(String name) {}

    protected Value initialize(ArgumentList args, Scope scope) {
        if (!args.keywords().isEmpty()) {
            throw new RuntimeException("Type '" + getName() + "' does not accept keyword arguments");
        }
        return null;
    }

    public static class Builder {

        private String name;
        private String[] personal;
        private Map<String, Value> shared;
        private Map<String, LFunction> operations;

        public Builder(String name) {
            this.name = name;
            this.personal = new String[0];
            this.shared = new HashMap<>();
            this.operations = new HashMap<>();
        }

        public Builder personal(String... names) {
            personal = names;
            return this;
        }

        public Builder shared(String name, Value value) {
            shared.put(name, value);
            return this;
        }

        public Builder shared(String name, FunctionBody method) {
            shared.put(name, new BuiltinFunction(name, method));
            return this;
        }

        public Builder operation(String operator, FunctionBody handler) {
            operations.put(operator, new BuiltinFunction(name, handler));
            return this;
        }

    }

}
