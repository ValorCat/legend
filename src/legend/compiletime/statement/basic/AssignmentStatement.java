package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.Token.TokenType;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.AssignInstruction;
import legend.runtime.instruction.Instruction;

import java.util.List;

public class AssignmentStatement implements BasicStatementType {

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
        return new Statement(this,
                parser.parseFrom(tokens, equalsPos + 1),  // value
                tokens.get(equalsPos - 1).VALUE                    // target
        );
    }

    @Override
    public List<Instruction> build(Statement stmt) {
         return List.of(new AssignInstruction(stmt.STRING, stmt.EXPRESSION));
    }

    @Override
    public String getName() {
        return "=";
    }
}
