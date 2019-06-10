package statement;

import expression.Expression;

public class StatementData {

    public final StatementType TYPE;
    public final Expression EXPRESSION;
    public final String STRING;

    public StatementData(StatementType type) {
        this(type, null, null);
    }

    public StatementData(StatementType type, String string) {
        this(type, null, string);
    }

    public StatementData(StatementType type, Expression expression) {
        this(type, expression, null);
    }

    public StatementData(StatementType type, Expression expression, String string) {
        this.TYPE = type;
        this.EXPRESSION = expression;
        this.STRING = string;
    }

}
