package legend.compiletime.expression.group;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;
import legend.runtime.type.BuiltinType;
import legend.runtime.type.DynamicType;
import legend.runtime.type.RuntimeType;
import legend.runtime.type.Type;

import java.util.List;

/**
 * @since 2/17/2019
 */
public class ParameterList {

    private String funcName;
    private List<String> paramNames;
    private List<Expression> paramTypes;
    private Type[] paramTypeValues;

    public ParameterList(String funcName, List<String> paramNames, List<Expression> paramTypes) {
        this.funcName = funcName;
        this.paramNames = paramNames;
        this.paramTypes = paramTypes;
    }

    public void resolveTypes(Scope scope) {
        paramTypeValues = new Type[paramNames.size()];
        for (int i = 0; i < paramNames.size(); i++) {
            if (paramTypes.get(i) != null) {
                Value type = paramTypes.get(i).evaluate(scope);
                if (!type.isType(BuiltinType.TYPE)) {
                    throw new RuntimeException("Type for parameter '" + paramNames.get(i) + "' resolved to '"
                            + type.type().getName() + "'");
                }
                paramTypeValues[i] = type.asType();
            } else {
                paramTypeValues[i] = DynamicType.UNTYPED;
            }
        }
    }

    public void accept(ArgumentList args, Scope scope) {
        if (args.size() != paramNames.size()) {
            throw new RuntimeException(String.format("Function '%s' requires %d arguments but received %d",
                    funcName, paramNames.size(), args.size()));
        }
        for (int i = 0; i < args.size(); i++) {
            RuntimeType argType = args.arg(i).type();
            if (paramTypeValues[i].isSupertypeOf(argType)) {
                scope.setLocalVariable(paramNames.get(i), paramTypeValues[i], args.arg(i));
            } else {
                throw new RuntimeException(String.format("Cannot assign %s value '%s' to %s parameter '%s'",
                        argType.getName(), args.arg(i).asString(), paramTypeValues[i].getName(), paramNames.get(i)));
            }
        }
    }

}
