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

import static compiletime.error.ErrorDescription.BAD_PRINT_STRING;

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
            throw ErrorLog.raise(BAD_PRINT_STRING, "Print string cannot be used in an expression (did you " +
                    "mean single quotes?)");
        }
        return new Statement(this, tokens.get(0).VALUE);
    }

    @Override
    public List<Instruction> build(Statement data) {
        return List.of(new PrintInstruction(data.STRING));
    }

    @Override
    public String getName() {
        return "<print>";
    }
}
