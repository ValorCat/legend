package legend.runtime.library;

import legend.compiletime.expression.type.PrimitiveType;

public class FunctionType extends PrimitiveType {

    public FunctionType() {
        super(new PrimitiveType.Builder("function", "any"));
    }

}
