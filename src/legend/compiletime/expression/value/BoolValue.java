package legend.compiletime.expression.value;

import legend.runtime.type.BuiltinType;

/**
 * @since 12/28/2018
 */
public final class BoolValue extends Value {

    public static final BoolValue TRUE = new BoolValue(true);
    public static final BoolValue FALSE = new BoolValue(false);

    public static BoolValue resolve(boolean b) {
        return b ? TRUE : FALSE;
    }

    private boolean value;
    private String string;

    private BoolValue(boolean value) {
        super(BuiltinType.BOOL);
        this.value = value;
        this.string = String.valueOf(value);
    }

    @Override
    public boolean matches(String pattern) {
        return string.equals(pattern);
    }

    @Override
    public String asString() {
        return string;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    public BoolValue not() {
        return this == TRUE ? FALSE : TRUE;
    }

    @Override
    public boolean equals(Value other) {
        return this == other;
    }

    @Override
    public String toString() {
        return string;
    }

}
