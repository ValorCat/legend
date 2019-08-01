package legend.runtime.library;


import legend.compiletime.expression.type.PrimitiveType;

public class TypeType extends PrimitiveType {

    public TypeType() {
        super(new PrimitiveType.Builder("type", "any"));
    }

}
