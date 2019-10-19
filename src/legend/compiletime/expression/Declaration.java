package legend.compiletime.expression;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.value.TypeValue;
import legend.runtime.type.NoType;

import java.util.ArrayList;
import java.util.List;

public final class Declaration {

    public final String NAME;
    public final Expression TYPE;

    private Declaration(String name, Expression type) {
        this.NAME = name;
        this.TYPE = type;
    }

    private Declaration(String name) {
        this(name, new TypeValue(NoType.NO_TYPE));
    }

    public String toString() {
        return "(" + NAME + "," + TYPE + ")";
    }

    public static List<Declaration> parse(TokenLine tokens, Parser parser) {
        List<Declaration> decs = new ArrayList<>(1);
        int current = 0, nextComma;
        while ((nextComma = tokens.indexOf(",", current)) >= 0) {
            decs.add(parseSingle(tokens.subList(current, nextComma), parser));
            current = nextComma + 1;
        }
        if (current < tokens.size()) {
            decs.add(parseSingle(tokens.subList(current, tokens.size()), parser));
        }
        return decs;
    }

    public static Declaration parseSingle(TokenLine tokens, Parser parser) {
        int length = tokens.size();
        if (length == 1) {
            String name = tokens.get(0).asExpression().getIdentifier();
            return new Declaration(name);
        } else if (length == 0) {
            throw ErrorLog.get("Missing declaration");
        } else {
            String name = tokens.get(length - 1).asExpression().getIdentifier();
            Expression type = parser.parseBetween(tokens, 0, length - 2);
            if (!type.isCompact()) {
                throw ErrorLog.get("Type expression for '%s' must be wrapped in parentheses", name);
            }
            return new Declaration(name, type);
        }
    }

}
