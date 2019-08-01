package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.PrintInstruction;

import java.util.List;

public class PrintStatement implements BasicStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.get(0).matches(">>");
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        return new Statement(this, parser.parseFrom(tokens, 1));
    }

    @Override
    public List<Instruction> build(Statement stmt) {
        return List.of(new PrintInstruction(stmt.EXPRESSION));
    }

    @Override
    public String getName() {
        return "print";
    }
}
