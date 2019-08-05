package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.EvaluateInstruction;
import legend.runtime.instruction.Instruction;

import java.util.List;

public class ExpressionStatement implements BasicStatement {

    private Expression expression;

    public ExpressionStatement() {}

    private ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean matches(TokenLine tokens) {
        return true;
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        return new ExpressionStatement(parser.parseExpression(tokens));
    }

    @Override
    public List<Instruction> build() {
         return List.of(new EvaluateInstruction(expression));
    }

    @Override
    public String getName() {
        return "expr";
    }

}
