package expression.group;

import execute.Scope;
import expression.Expression;
import expression.operation.FunctionCall;
import expression.value.Value;
import expression.value.type.Type;

import java.util.List;

/**
 * @since 2/17/2019
 */
public class ParameterList {

    private String name;
    private String[] params;
    private Expression[] bounds;

    public ParameterList(String name, List<Expression> params) {
        this.name = name;
        this.params = new String[params.size()];
        this.bounds = new Expression[params.size()];

        for (int i = 0; i < params.size(); i++) {
            Expression param = params.get(i);
            if (param.matches(":")) {
                this.params[i] = param.getChildren().get(0).getIdentifier();
                this.bounds[i] = param.getChildren().get(1);
            } else {
                this.params[i] = param.getIdentifier();
                this.bounds[i] = null;
            }
        }
    }

    public void accept(ArgumentList args, Scope defScope, Scope funcScope) {
        if (args.size() != params.length) {
            throw new RuntimeException(String.format("Function '%s' requires %d arguments but received %d",
                    name, params.length, args.size()));
        }
        for (int i = 0; i < args.size(); i++) {
            Value arg = args.arg(i);
            if (bounds[i] != null) {
                Value bound = bounds[i].evaluate(defScope);
                if (!withinBound(arg, bound, defScope)) {
                    throw new RuntimeException(String.format("Argument #%d (%s) of function '%s' is out of bounds: %s",
                            i + 1, arg.asString(), name, bound.asString()));
                }
            }
            funcScope.setLocalVariable(params[i], args.arg(i));
        }
    }

    public String[] getParamNames() {
        return params;
    }

    private boolean withinBound(Value value, Expression boundExpr, Scope scope) {
        Value bound = boundExpr.evaluate(scope);
        switch (bound.type().getName()) {
            case "Type":     return ((Type) bound).encompasses(value.type());
            case "Range":    return bound.callMethod("contains", scope, value).asBoolean();
            case "Function": return FunctionCall.call(bound, new ArgumentList(value), scope).asBoolean();
            default: throw new RuntimeException(String.format("Invalid bound '%s' (type %s) for function '%s'",
                    bound, bound.type().getName(), name));
        }
    }

}
