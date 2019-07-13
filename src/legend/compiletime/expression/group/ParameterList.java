package legend.compiletime.expression.group;

import legend.compiletime.expression.Expression;
import legend.runtime.Scope;

import java.util.List;

/**
 * @since 2/17/2019
 */
public class ParameterList {

    private String name;
    private String[] params;

    public ParameterList(String name, List<Expression> params) {
        this.name = name;
        this.params = new String[params.size()];

        for (int i = 0; i < params.size(); i++) {
            this.params[i] = params.get(i).getIdentifier();
        }
    }

    public void accept(ArgumentList args, Scope scope) {
        if (args.size() != params.length) {
            throw new RuntimeException(String.format("Function '%s' requires %d arguments but received %d",
                    name, params.length, args.size()));
        }
        for (int i = 0; i < args.size(); i++) {
            scope.setLocalVariable(params[i], args.arg(i));
        }
    }

}
