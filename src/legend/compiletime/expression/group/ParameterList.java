package legend.compiletime.expression.group;

import legend.compiletime.expression.Declaration;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;
import legend.runtime.type.BuiltinType;
import legend.runtime.type.RuntimeType;
import legend.runtime.type.Type;

import java.util.List;

/**
 * @since 2/17/2019
 */
public class ParameterList {

    private String funcName;
    private List<Declaration> params;
    private Type[] paramTypes;

    public ParameterList(String funcName, List<Declaration> params) {
        this.funcName = funcName;
        this.params = params;
    }

    public void resolveTypes(Scope scope) {
        paramTypes = new Type[params.size()];
        for (int i = 0; i < params.size(); i++) {
            Value type = params.get(i).TYPE.evaluate(scope);
            if (!type.isType(BuiltinType.TYPE)) {
                throw new RuntimeException("Type for parameter '" + params.get(i).NAME + "' resolved to '"
                        + type.type().getName() + "' instead of 'type'");
            }
            paramTypes[i] = type.asType();
        }
    }

    public void accept(ArgumentList args, Scope scope) {
        if (args.size() != params.size()) {
            throw new RuntimeException(String.format("Function '%s' requires %d arguments but received %d",
                    funcName, params.size(), args.size()));
        }
        for (int i = 0; i < args.size(); i++) {
            RuntimeType argType = args.arg(i).type();
            if (paramTypes[i].isSupertypeOf(argType)) {
                scope.setLocalVariable(params.get(i).NAME, paramTypes[i], args.arg(i));
            } else {
                throw new RuntimeException(String.format("Cannot assign %s value '%s' to %s parameter '%s'",
                        argType.getName(), args.arg(i).asString(), paramTypes[i].getName(), params.get(i).NAME));
            }
        }
    }

}
