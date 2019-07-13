package legend.compiletime.expression.value.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LObject;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.compiletime.expression.value.function.BuiltinFunction.FunctionBody;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public abstract class BuiltinType extends Type {

    public BuiltinType(String name) {
        super(name, new String[0]);
    }

    public BuiltinType(Builder builder) {
        super(builder.name, builder.personal, builder.shared, builder.unaryOps, builder.binaryOps);
    }

    @Override
    public Value instantiate(ArgumentList args) {
        Value instance = initialize(args);
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

    protected Value initialize(ArgumentList args) {
        if (!args.keywords().isEmpty()) {
            throw new RuntimeException("Type '" + getName() + "' does not accept keyword arguments");
        }
        return null;
    }

    public static class Builder {

        private String name;
        private String[] personal;
        private Map<String, Value> shared;
        private Map<String, UnaryOperator<Value>> unaryOps;
        private Map<String, BinaryOperator<Value>> binaryOps;

        public Builder(String name) {
            this.name = name;
            this.personal = new String[0];
            this.shared = new HashMap<>();
            this.unaryOps = new HashMap<>();
            this.binaryOps = new HashMap<>();
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

        public Builder unaryOper(String operator, UnaryOperator<Value> handler) {
            unaryOps.put(operator, handler);
            return this;
        }

        public Builder binaryOper(String operator, BinaryOperator<Value> handler) {
            binaryOps.put(operator, handler);
            return this;
        }

    }

}
