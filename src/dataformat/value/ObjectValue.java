package dataformat.value;

import java.util.Arrays;

/**
 * @since 1/18/2019
 */
public class ObjectValue extends Value {

    public ObjectValue(Type type, Value... attributes) {
        super(type, attributes);
    }

    @Override
    public String asStr() {
        return toString();
    }

    @Override
    public boolean equals(Value other) {
        return (this == other)
                || ((type() == other.type()) && Arrays.equals(getAttributes(), other.getAttributes()));
    }

    @Override
    public String toString() {
        return type().getName() + Arrays.toString(getAttributes());
    }

}
