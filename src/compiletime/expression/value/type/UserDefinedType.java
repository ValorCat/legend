package compiletime.expression.value.type;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.LObject;
import compiletime.expression.value.Value;
import runtime.Scope;

import java.util.Map;

/**
 * @since 3/28/2019
 */
public class UserDefinedType extends Type {

    private boolean anonymous;

    public UserDefinedType(String[] personal) {
        super("(anonymous type)", personal, Map.of());
        this.anonymous = true;
    }

    @Override
    public Value instantiate(ArgumentList args, Scope scope) {
        if (!args.keywords().isEmpty()) {
            throw new RuntimeException("Type '" + getName() + "' does not accept keyword arguments");
        }
        // todo check params = args
        return new LObject(this, args.args());
    }

    public void deanonymize(String name) {
        if (anonymous) {
            this.name = name;
            this.anonymous = false;
        }
    }

}