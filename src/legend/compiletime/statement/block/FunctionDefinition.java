package legend.compiletime.statement.block;

import legend.compiletime.Parser;
import legend.compiletime.Token.TokenType;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.group.ParameterList;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.block.clause.Clause;
import legend.runtime.instruction.DefineFunctionInstruction;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.JumpInstruction;
import legend.runtime.instruction.ReturnInstruction;

import java.util.ArrayList;
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
        ParameterList params = parseParameters(name, tokens.get(2).CHILDREN, parser);

        Expression returnType = null;
        if (tokens.size() > 3) {
            returnType = parser.parseFrom(tokens, 4);
        }

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

    private static ParameterList parseParameters(String name, TokenLine tokens, Parser parser) {
        List<String> paramNames = new ArrayList<>(2);
        List<Expression> paramTypes = new ArrayList<>(2);

        int param = 1, current = 0, nextComma;
        // refactor this hacky monstrosity
        while ((nextComma = tokens.indexOf(",")) >= 0 || (nextComma = tokens.size()) > 0 && current < tokens.size()) {
            String paramName;
            Expression paramType;
            switch (nextComma - current) {
                case 0:  // no parameter name
                    throw ErrorLog.get("Missing parameter #%d to function '%s'", param, name);
                case 1:  // untyped parameter
                    paramName = tokens.get(current).asExpression().getIdentifier();
                    paramNames.add(paramName);
                    paramTypes.add(null);
                    break;
                default: // typed parameter
                    paramName = tokens.get(nextComma - 1).asExpression().getIdentifier();
                    paramType = parser.parseBetween(tokens, current, nextComma - 2);
                    if (!paramType.isCompact()) {
                        throw ErrorLog.get("Type expression for paramter #%d must be wrapped in parentheses", param);
                    }
                    paramNames.add(paramName);
                    paramTypes.add(paramType);
            }
            current = nextComma + 1;
            param++;
        }
        return new ParameterList(name, paramNames, paramTypes);
    }

}
