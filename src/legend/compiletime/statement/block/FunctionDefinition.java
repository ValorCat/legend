package legend.compiletime.statement.block;

import legend.compiletime.Parser;
import legend.compiletime.Token.TokenType;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Declaration;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.group.ParameterList;
import legend.compiletime.expression.value.TypeValue;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.block.clause.Clause;
import legend.runtime.instruction.DefineFunctionInstruction;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.JumpInstruction;
import legend.runtime.instruction.ReturnInstruction;
import legend.runtime.type.NoType;

import java.util.List;

/**
 * @since 2/16/2019
 */
public class FunctionDefinition implements BlockStatement {

    private String name;
    private ParameterList params;
    private Expression returnType;

    public FunctionDefinition() {}

    private FunctionDefinition(String name, ParameterList params, Expression returnType) {
        this.name = name;
        this.params = params;
        this.returnType = returnType;
    }

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.get("Expected function name after 'def'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("()", TokenType.GROUP)) {
            throw ErrorLog.get("Expected function parameters after '%s'", tokens.get(1).VALUE);
        } else if (tokens.size() > 3 && !tokens.get(3).matches("->")) {
            throw ErrorLog.get("Unexpected symbol '%s' in function header", tokens.get(3).VALUE);
        } else if (tokens.size() == 4) {
            throw ErrorLog.get("Expected function return type after '->'");
        }

        String name = tokens.get(1).VALUE;
        ParameterList params = new ParameterList(name, Declaration.parse(tokens.get(2).CHILDREN, parser));
        Expression returnType = tokens.size() > 3
                ? parser.parseFrom(tokens, 4)      // explicit return type
                : new TypeValue(NoType.NO_TYPE);   // no return type

        return new FunctionDefinition(name, params, returnType);
    }

    @Override
    public List<Instruction> build(Clause base, List<Clause> optional) {
        FunctionDefinition header = (FunctionDefinition) base.HEADER;
        List<Instruction> body = base.BODY;

        if (!(body.get(body.size() - 1) instanceof ReturnInstruction)) {
            body.add(new ReturnInstruction());
        }

        return asList(body.size() + 2,
                new DefineFunctionInstruction(header.name, header.params, header.returnType),
                new JumpInstruction(body.size() + 1),
                body);
    }

    @Override
    public String getName() {
        return "def";
    }

}
