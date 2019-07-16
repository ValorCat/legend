package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.Variable;
import legend.compiletime.expression.value.Attribute;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.runtime.Scope;

import java.util.List;

/**
 * @since 2/9/2019
 */
public class MemberSelectOperation extends Operation {

    private static final String OPERATOR = ".";

    private Expression target;
    private String member;

    public MemberSelectOperation(Expression target, String member) {
        super(OPERATOR);
        this.target = target;
        this.member = member;
    }

    @Override
    public Value evaluate(Scope scope) {
        if (target.matches("_")) {
            return new BuiltinFunction(args -> args.arg(0).getAttribute(member));
        } else {
            Value targetValue = target.evaluate(scope);
            return new Attribute(targetValue, targetValue.getAttribute(member));
        }
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(target, new Variable(member));
    }

    public static void parse(int operIndex, TokenLine line) {
        Token left = null, right = null;
        if (operIndex > 0) left = line.get(operIndex - 1);
        if (operIndex < line.size() - 1) right = line.get(operIndex + 1);
        if (left == null || right == null || !left.isValue() || !right.isValue()) {
            throw ErrorLog.get("The '%s' operator requires values on both sides", OPERATOR);
        }
        Expression target = left.asExpression();
        String member = right.asExpression().getIdentifier();

        MemberSelectOperation operation = new MemberSelectOperation(target, member);
        line.consolidate(Token.newExpression(OPERATOR, operation), operIndex - 1, 3);
    }

}
