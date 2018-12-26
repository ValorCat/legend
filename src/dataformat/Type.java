package dataformat;

import execute.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @since 12/23/2018
 */
public class Type extends Value {

    public interface TypeInitializer extends BiFunction<ArgumentList, Environment, Value> {}

    private String name;
    private Map<String, Integer> attributes; // todo avoid Integer -> int unboxing
    private TypeInitializer initializer;
    private boolean anonymous;

    public Type(String[] attrNames) {
        this("(anonymous type)", attrNames);
        this.anonymous = true;
    }

    public Type(String name, String... attrNames) {
        this(name, null, attrNames);
    }

    public Type(String name, TypeInitializer init, String... attrNames) {
        super(null);
        this.name = name;
        this.attributes = new HashMap<>(attrNames.length);
        for (int i = 0; i < attrNames.length; i++) {
            this.attributes.put(attrNames[i], i);
        }
        this.initializer = init;
        this.anonymous = false;
    }

    @Override
    public boolean matches(String pattern) {
        return getName().equals(pattern);
    }

    public String getName() {
        return name;
    }

    public void deanonymize(String name) {
        if (anonymous) {
            this.name = name;
            this.anonymous = false;
        }
    }

    public int getAttrIndex(String attrName) {
        Integer index = attributes.get(attrName);
        if (index == null) {
            throw new RuntimeException("Type '" + name + "' has no attribute '" + attrName + "'");
        }
        return index;
    }

    public Value create(Value... attributes) {
        return new Value(this, attributes);
    }

    public Value instantiate(ArgumentList args, Environment env) {
        if (initializer == null) {
            if (!args.keywords().isEmpty()) {
                throw new RuntimeException("Type '" + getName() + "' does not accept keyword arguments");
            }
            // todo check params = args
            return new Value(this, args.args());
        } else {
            Value instance = initializer.apply(args, env);
            instance.setType(this);
            return instance;
        }
    }

    @Override
    public String toString() {
        return "type[" + getName() + "]";
    }
}
