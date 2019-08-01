package legend.runtime.library;

import legend.compiletime.expression.type.PrimitiveType;

public class NullType extends PrimitiveType {

    public NullType() {
        super(new PrimitiveType.Builder("*null", "any"));
    }

}
