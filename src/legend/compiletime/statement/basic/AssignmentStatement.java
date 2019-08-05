package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.Token.TokenType;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.AssignTypedInstruction;
import legend.runtime.instruction.AssignUntypedInstruction;
import legend.runtime.instruction.Instruction;

import java.util.List;

public class AssignmentStatement implements BasicStatement {

    private String target;
    private Expression type;
    private Expression value;

    public AssignmentStatement() {}

    private AssignmentStatement(String target, Expression type, Expression value) {
        this.target = target;
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.contains("=");
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        int equalsPos = tokens.indexOf("=");
        if (equalsPos == 0 || tokens.get(equalsPos - 1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.get("Missing assignment target on left of '='");
        } else if (equalsPos == tokens.size() - 1) {
            throw ErrorLog.get("Missing assignment value on right of '='");
        }
        String target = tokens.get(equalsPos - 1).VALUE;
        Expression type = null;
        Expression value = parser.parseFrom(tokens, equalsPos + 1);
        if (equalsPos > 1) {
            type = parser.parseBetween(tokens, 0, equalsPos - 2);
            if (!type.isCompact()) {
                throw ErrorLog.get("Assignment type expression must be wrapped in parentheses");
            }
        }
        return new AssignmentStatement(target, type, value);
    }

    @Override
    public List<Instruction> build() {
         return List.of(
                 type == null
                         ? new AssignUntypedInstruction(target, value)
                         : new AssignTypedInstruction(target, type, value)
         );
    }

    @Override
    public String getName() {
        return "=";
    }

}
