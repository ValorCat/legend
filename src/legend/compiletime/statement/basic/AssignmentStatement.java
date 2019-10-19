package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.Token.TokenType;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Declaration;
import legend.compiletime.expression.Expression;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.AssignInstruction;
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
        Declaration target = Declaration.parseSingle(tokens.subList(0, equalsPos), parser);
        Expression value = parser.parseFrom(tokens, equalsPos + 1);
        return new AssignmentStatement(target.NAME, target.TYPE, value);
    }

    @Override
    public List<Instruction> build() {
         return List.of(new AssignInstruction(target, type, value));
    }

    @Override
    public String getName() {
        return "=";
    }

}
