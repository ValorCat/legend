package legend.runtime.library;

import legend.runtime.type.PrimitiveType;

public class FunctionType extends PrimitiveType {

    public FunctionType() {
        super(new PrimitiveType.Builder("function"));
    }

}
