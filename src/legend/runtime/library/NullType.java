package legend.runtime.library;

import legend.runtime.type.PrimitiveType;

public class NullType extends PrimitiveType {

    public NullType() {
        super(new PrimitiveType.Builder("*null"));
    }

}
