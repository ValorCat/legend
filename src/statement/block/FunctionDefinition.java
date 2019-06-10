package statement.block;

import expression.group.Parentheses;
import instruction.DefineFunctionInstruction;
import instruction.Instruction;
import instruction.JumpInstruction;
import instruction.ReturnInstruction;
import parse.Parser;
import parse.Token.TokenType;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.StatementData;
import statement.block.clause.ClauseData;

import java.util.List;

import static parse.error.ErrorDescription.BAD_FUNC_DEF;

/**
 * @since 2/16/2019
 */
public class FunctionDefinition implements BlockStatementType {

    @Override
    public StatementData parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.raise(BAD_FUNC_DEF, "Expected function name after 'def'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("()")) {
            throw ErrorLog.raise(BAD_FUNC_DEF, "Expected function parameters after '%s'", tokens.get(1));
        }
        return new StatementData(this, parser.parseFrom(tokens, 2), tokens.get(1).VALUE);
        //params = new ParameterList(name, ((Parentheses) parser.parseFrom(tokens, 2)).getContents());
    }

    @Override
    public List<Instruction> build(List<ClauseData> clauses) {
        String name = clauses.get(0).HEADER.STRING;
        Parentheses params = (Parentheses) clauses.get(0).HEADER.EXPRESSION;
        List<Instruction> body = clauses.get(0).BODY;

        if (!(body.get(body.size() - 1) instanceof ReturnInstruction)) {
            body.add(new ReturnInstruction());
        }

        return asList(body.size() + 2,
                new DefineFunctionInstruction(name, params),
                new JumpInstruction(body.size() + 1),
                body);
    }

    @Override
    public String getKeyword() {
        return "def";
    }

}
