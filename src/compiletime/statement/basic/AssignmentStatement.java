package compiletime.statement.basic;

import compiletime.Parser;
import compiletime.Token.TokenType;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.statement.Statement;
import runtime.instruction.AssignInstruction;
import runtime.instruction.Instruction;

import java.util.List;

import static compiletime.error.ErrorDescription.BAD_ASSIGN;

public class AssignmentStatement implements BasicStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.contains("=");
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        int equalsPos = tokens.indexOf("=");
        if (equalsPos == 0 || tokens.get(equalsPos - 1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.raise(BAD_ASSIGN, "Missing assignment target on left of '='");
        } else if (equalsPos == tokens.size() - 1 || !tokens.get(equalsPos + 1).isValue()) {
            throw ErrorLog.raise(BAD_ASSIGN, "Missing assignment value on right of '='");
        }
        return new Statement(this,
                parser.parseFrom(tokens, equalsPos + 1),  // value
                tokens.get(equalsPos - 1).VALUE                    // target
        );
    }

    @Override
    public List<Instruction> build(Statement data) {
         return List.of(new AssignInstruction(data.STRING, data.EXPRESSION));
    }

    @Override
    public String getName() {
        return "=";
    }
}
