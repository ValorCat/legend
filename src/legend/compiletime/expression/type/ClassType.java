package legend.compiletime.expression.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LObject;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.BuiltinFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static legend.compiletime.expression.value.function.BuiltinFunction.FunctionBody;

public class ClassType extends Type {

    private Map<String, Integer> personal;
    private Map<String, Value> shared;

    public ClassType(Builder builder) {
        super(builder.name, builder.supertype, builder.unaryOps, builder.binaryOps);
        this.personal = buildPersonalMap(builder.personal);
        this.shared = builder.shared;
    }

    @Override
    public Value buildNew(ArgumentList args) {
        return new LObject(this, args.args());
    }

    @Override
    public Value getAttribute(String attribute, Value target) {
        return getPersonal(target, attribute)
                .or(() -> getShared(attribute))
                .orElseThrow(() -> new RuntimeException("Type '" + getName() + "' has no attribute '" + attribute + "'"));
    }

    @Override
    public void setAttribute(String attribute, Value target, Value value) {
        if (!setPersonal(target, attribute, value) && !setShared(attribute, value)) {
            throw new RuntimeException("Type '" + getName() + "' has no attribute '" + attribute + "'");
        }
    }

    private Optional<Value> getPersonal(Value target, String attribute) {
        return Optional.ofNullable(personal.get(attribute))
                .map(target::getAttribute);
    }

    private boolean setPersonal(Value target, String attribute, Value value) {
        Integer index = personal.get(attribute);
        if (index != null) {
            target.setAttribute(index, value);
            return true;
        }
        return false;
    }

    private Optional<Value> getShared(String attribute) {
        return Optional.ofNullable(shared.get(attribute));
    }

    private boolean setShared(String attribute, Value value) {
        if (shared.containsKey(attribute)) {
            shared.put(attribute, value);
            return true;
        }
        return false;
    }

    private static Map<String, Integer> buildPersonalMap(String[] names) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], i);
        }
        return map;
    }

    public static class Builder {

        private String name;
        private String supertype;
        private Map<String, UnaryOperator<Value>> unaryOps;
        private Map<String, BinaryOperator<Value>> binaryOps;
        private String[] personal;
        private Map<String, Value> shared;

        public Builder(String name, String supertype) {
            this.name = name;
            this.supertype = supertype;
            this.unaryOps = new HashMap<>();
            this.binaryOps = new HashMap<>();
            this.personal = new String[0];
            this.shared = new HashMap<>();
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
