package legend.runtime.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class PrimitiveType extends RuntimeType {

    public PrimitiveType(Builder builder) {
        super(builder.name, builder.supertype, builder.unaryOps, builder.binaryOps);
    }

    @Override
    public Value buildNew(ArgumentList args) {
        throw new RuntimeException("Cannot build object of primitive type '" + getName() + "'");
    }

    @Override
    public Value getAttribute(String attribute, Value target) {
        throw new RuntimeException("Type '" + getName() + "' has no attribute '" + attribute + "'");
    }

    @Override
    public void setAttribute(String attribute, Value target, Value value) {
        throw new RuntimeException("Type '" + getName() + "' has no attribute '" + attribute + "'");
    }

    public static class Builder {

        private String name;
        private TypeReference supertype;
        private Map<String, UnaryOperator<Value>> unaryOps;
        private Map<String, BinaryOperator<Value>> binaryOps;

        public Builder(String name) {
            this(name, BuiltinType.ANY);
        }

        public Builder(String name, TypeReference supertype) {
            this.name = name;
            this.supertype = supertype;
            this.unaryOps = new HashMap<>();
            this.binaryOps = new HashMap<>();
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
