package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.runtime.Scope;

/**
 * @since 2/9/2019
 */
public class WhereOperation extends BinaryOperation {

    private static final String OPERATOR = "where";

    public WhereOperation(Expression input, Expression predicate) {
        super(OPERATOR, input, predicate);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value input = left.evaluate(scope);
        Scope predScope = new Scope(scope);
        Value predicate = new BuiltinFunction(args -> {
            predScope.setLocalVariable("*", args.arg(0));
            return right.evaluate(predScope);
        });
        return input.operateBinary("where", predicate);
    }

}
