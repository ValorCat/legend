package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.PrintInstruction;

import java.util.List;

public class PrintStatement implements BasicStatement {

    private Expression value;

    public PrintStatement() {}

    private PrintStatement(Expression value) {
        this.value = value;
    }

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.get(0).matches(">>");
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        return new PrintStatement(parser.parseFrom(tokens, 1));
    }

    @Override
    public List<Instruction> build() {
        return List.of(new PrintInstruction(value));
    }

    @Override
    public String getName() {
        return "print";
    }

}
