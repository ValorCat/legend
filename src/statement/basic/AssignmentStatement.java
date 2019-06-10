package statement.basic;

import instruction.AssignInstruction;
import instruction.Instruction;
import parse.Parser;
import parse.Token.TokenType;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.StatementData;

import java.util.List;

import static parse.error.ErrorDescription.BAD_ASSIGN;

public class AssignmentStatement implements BasicStatement {

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.contains("=");
    }

    @Override
    public StatementData parse(TokenLine tokens, Parser parser) {
        int equalsPos = tokens.indexOf("=");
        if (equalsPos == 0 || tokens.get(equalsPos - 1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.raise(BAD_ASSIGN, "Missing assignment target on left of '='");
        } else if (equalsPos == tokens.size() - 1 || !tokens.get(equalsPos + 1).isValue()) {
            throw ErrorLog.raise(BAD_ASSIGN, "Missing assignment value on right of '='");
        }
        return new StatementData(
                parser.parseFrom(tokens, equalsPos + 1),  // value
                tokens.get(equalsPos - 1).VALUE                    // target
        );
    }

    @Override
    public List<Instruction> compile(StatementData data) {
         return List.of(new AssignInstruction(data.STRING, data.EXPRESSION));
    }

    @Override
    public String getKeyword() {
        return "=";
    }
}
