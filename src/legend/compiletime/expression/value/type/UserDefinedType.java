package legend.compiletime.expression.value.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LObject;
import legend.compiletime.expression.value.Value;

/**
 * @since 3/28/2019
 */
public class UserDefinedType extends Type {

    public UserDefinedType(String[] personal) {
        super("(anonymous type)", "any", personal);
    }

    @Override
    public Value instantiate(ArgumentList args) {
        if (!args.keywords().isEmpty()) {
            throw new RuntimeException("Type '" + getName() + "' does not accept keyword arguments");
        }
        // todo check params = args
        return new LObject(this, args.args());
    }

}
