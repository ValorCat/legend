package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.Token;
import legend.compiletime.Token.TokenType;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.PrintInstruction;

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
        return "pstr";
    }
}
