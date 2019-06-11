package compiletime.statement.block;

import compiletime.Parser;
import compiletime.Token.TokenType;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.expression.group.Parentheses;
import compiletime.statement.Statement;
import compiletime.statement.block.clause.Clause;
import runtime.instruction.DefineFunctionInstruction;
import runtime.instruction.Instruction;
import runtime.instruction.JumpInstruction;
import runtime.instruction.ReturnInstruction;

import java.util.List;

/**
 * @since 2/16/2019
 */
public class FunctionDefinition implements BlockStatementType {

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.get("Expected function name after 'def'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("()", TokenType.GROUP)) {
            throw ErrorLog.get("Expected function parameters after '%s'", tokens.get(1));
        }
        return new Statement(this, parser.parseFrom(tokens, 2), tokens.get(1).VALUE);
    }

    @Override
    public List<Instruction> build(Clause base, List<Clause> optional) {
        String name = base.HEADER.STRING;
        Parentheses params = (Parentheses) base.HEADER.EXPRESSION;
        List<Instruction> body = base.BODY;

        if (!(body.get(body.size() - 1) instanceof ReturnInstruction)) {
            body.add(new ReturnInstruction());
        }

        return asList(body.size() + 2,
                new DefineFunctionInstruction(name, params),
                new JumpInstruction(body.size() + 1),
                body);
    }

    @Override
    public String getName() {
        return "def";
    }

}
