package statement.structure;

import execute.Program;
import execute.Scope;
import expression.group.ArgumentList;
import expression.group.ParameterList;
import expression.group.Parentheses;
import expression.value.Value;
import expression.value.function.UserDefinedFunction;
import parse.Parser;
import parse.Token;
import parse.Token.TokenType;
import parse.error.ErrorLog;

import java.util.List;
import java.util.StringJoiner;

import static parse.error.ErrorDescription.BAD_FUNC_DEF;

/**
 * @since 2/16/2019
 */
public class FunctionDefinition implements FlowController {

    private String name;
    private ParameterList params;
    private Scope parentScope;
    private int startAddress, endAddress;

    public FunctionDefinition(List<Token> tokens, Parser parser) {
        if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.raise(BAD_FUNC_DEF, "Expected function name after 'def'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("()")) {
            throw ErrorLog.raise(BAD_FUNC_DEF, "Expected function parameters after '%s'", tokens.get(1));
        }
        name = tokens.get(1).VALUE;
        params = new ParameterList(name, ((Parentheses) parser.parseFrom(tokens, 2)).getContents());
    }

    /*
    This is essentially a dummy method. When the flow of control reaches
    this function definition, we skip the function body. If the function
    is executed later, the call method is run instead.
     */
    @Override
    public void execute(Scope scope) {
        parentScope = scope;
        startAddress = Program.PROGRAM.getCounter();
        Program.PROGRAM.getControlStack().push(this);
        scope.setLocalVariable(name, new UserDefinedFunction(name, this));
        Program.PROGRAM.setCounter(endAddress + 1);
    }

    public Value call(ArgumentList args) {
        Scope newScope = new Scope(parentScope, Program.PROGRAM.getCounter());
        params.accept(args, parentScope, newScope);
        return Program.PROGRAM.executeSubroutine(startAddress + 1, endAddress, newScope);
    }

    @Override
    public boolean isDone(Scope scope) {
        Program.PROGRAM.setCounter(scope.getReturnAddress());
        return true;
    }

    @Override
    public void setJumpPoint(List<Token> tokens, Parser parser) {
        if (tokens.get(0).matches("end")) {
            this.endAddress = parser.getAddress();
        } else {
            FlowController.invalidJumpPoint(tokens.get(0));
        }
    }

    @Override
    public String getKeyword() {
        return "def";
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        for (String param : params.getParamNames()) {
            joiner.add(param);
        }
        return "def(" + name + " " + joiner + ")";
    }

}
