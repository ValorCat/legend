package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.Variable;
import legend.compiletime.expression.value.Attribute;
import legend.compiletime.expression.value.Value;
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
    public void parse(TokenLine line, int operIndex) {
        line.consolidate(Token.newExpression(operator, this), operIndex - 1, 3);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value targetValue = target.evaluate(scope);
        return new Attribute(targetValue, targetValue.getAttribute(member));
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(target, new Variable(member));
    }

}
