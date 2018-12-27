package dataformat;

/**
 * @since 12/27/2018
 */
public class Attribute extends Value {

    private Value owner, value;

    public Attribute(Value owner, Value value) {
        super(value.type(), value.getAttributes());
        this.owner = owner;
        this.value = value;
    }

    @Override
    public boolean hasOwner() {
        return true;
    }

    @Override
    public Value getOwner() {
        return owner;
    }

    public Value getValue() {
        return value;
    }

}
