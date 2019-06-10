package statement;

import expression.Expression;

public class StatementData {

    public static final StatementData EMPTY = new StatementData();

    public final Expression EXPRESSION;
    public final String STRING;

    private StatementData() {
        this(null, null);
    }

    public StatementData(String string) {
        this(null, string);
    }

    public StatementData(Expression expression) {
        this(expression, null);
    }

    public StatementData(Expression expression, String string) {
        this.EXPRESSION = expression;
        this.STRING = string;
    }

}
