package compiletime.statement.basic;

import compiletime.Parser;
import compiletime.Token;
import compiletime.Token.TokenType;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.statement.Statement;
import runtime.instruction.Instruction;
import runtime.instruction.PrintInstruction;

import java.util.List;

public class PrintStringStatement implements BasicStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        for (Token token : tokens) {
            if (token.TYPE == TokenType.PRINT_STRING) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.get(0).TYPE != TokenType.PRINT_STRING || tokens.size() > 1) {
            throw ErrorLog.get("Print string cannot be used in an expression (did you mean single quotes?)");
        }
        return new Statement(this, tokens.get(0).VALUE);
    }

    @Override
    public List<Instruction> build(Statement stmt) {
        return List.of(new PrintInstruction(stmt.STRING));
    }

    @Override
    public String getName() {
        return "print";
    }
}
