package legend.compiletime.expression.group;

import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;
import legend.runtime.type.BuiltinType;
import legend.runtime.type.DynamicType;
import legend.runtime.type.RuntimeType;
import legend.runtime.type.Type;

/**
 * @since 2/17/2019
 */
public class ParameterList {

    private String funcName;
    private String[] paramNames;
    private String[] paramTypeNames;
    private Type[] paramTypes;

    public ParameterList(String funcName, String[] paramNames, String[] paramTypes) {
        this.funcName = funcName;
        this.paramNames = paramNames;
        this.paramTypeNames = paramTypes;
    }

    public void resolveTypes(Scope scope) {
        paramTypes = new Type[paramTypeNames.length];
        for (int i = 0; i < paramTypeNames.length; i++) {
            if (paramTypeNames[i] != null) {
                Value type = scope.getVariable(paramTypeNames[i]);
                if (!type.isType(BuiltinType.TYPE)) {
                    throw new RuntimeException("Type for parameter '" + paramTypeNames[i] + "' resolved to '"
                            + type.type().getName() + "'");
                }
                paramTypes[i] = type.asType();
            } else {
                paramTypes[i] = DynamicType.UNTYPED;
            }
        }
    }

    public void accept(ArgumentList args, Scope scope) {
        if (args.size() != paramNames.length) {
            throw new RuntimeException(String.format("Function '%s' requires %d arguments but received %d",
                    funcName, paramNames.length, args.size()));
        }
        for (int i = 0; i < args.size(); i++) {
            RuntimeType argType = args.arg(i).type();
            if (paramTypes[i].isSupertypeOf(argType)) {
                scope.setLocalVariable(paramNames[i], paramTypes[i], args.arg(i));
            } else {
                throw new RuntimeException(String.format("Cannot assign %s value '%s' to %s parameter '%s'",
                        argType.getName(), args.arg(i).asString(), paramTypes[i].getName(), paramNames[i]));
            }
        }
    }

}
