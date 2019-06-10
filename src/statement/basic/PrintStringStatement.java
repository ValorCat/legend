package statement.basic;

import instruction.Instruction;
import instruction.PrintInstruction;
import parse.Parser;
import parse.Token;
import parse.Token.TokenType;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.StatementData;

import java.util.List;

import static parse.error.ErrorDescription.BAD_PRINT_STRING;

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
    public StatementData parse(TokenLine tokens, Parser parser) {
        if (tokens.get(0).TYPE != TokenType.PRINT_STRING || tokens.size() > 1) {
            throw ErrorLog.raise(BAD_PRINT_STRING, "Print string cannot be used in an expression (did you " +
                    "mean single quotes?)");
        }
        return new StatementData(this, tokens.get(0).VALUE);
    }

    @Override
    public List<Instruction> build(StatementData data) {
        return List.of(new PrintInstruction(data.STRING));
    }

    @Override
    public String getKeyword() {
        return "<print>";
    }
}
