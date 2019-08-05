package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.Token.TokenType;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.AssignInstruction;
import legend.runtime.instruction.Instruction;

import java.util.List;

public class AssignmentStatement implements BasicStatement {

    private String target;
    private Expression value;

    public AssignmentStatement() {}

    private AssignmentStatement(String target, Expression value) {
        this.target = target;
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
        } else if (equalsPos == tokens.size() - 1 || !tokens.get(equalsPos + 1).isValue()) {
            throw ErrorLog.get("Missing assignment value on right of '='");
        }
        String target = tokens.get(equalsPos - 1).VALUE;
        Expression value = parser.parseFrom(tokens, equalsPos + 1);
        return new AssignmentStatement(target, value);
    }

    @Override
    public List<Instruction> build() {
         return List.of(new AssignInstruction(target, value));
    }

    @Override
    public String getName() {
        return "=";
    }

}
