package statement;

import expression.Expression;

public class Statement {

    public final StatementType TYPE;
    public final Expression EXPRESSION;
    public final String STRING;

    public Statement(StatementType type) {
        this(type, null, null);
    }

    public Statement(StatementType type, String string) {
        this(type, null, string);
    }

    public Statement(StatementType type, Expression expression) {
        this(type, expression, null);
    }

    public Statement(StatementType type, Expression expression, String string) {
        this.TYPE = type;
        this.EXPRESSION = expression;
        this.STRING = string;
    }

}
