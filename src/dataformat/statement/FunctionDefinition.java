package dataformat.statement;

import dataformat.ArgumentList;
import dataformat.ParameterList;
import dataformat.group.Parentheses;
import dataformat.value.UserDefinedFunction;
import dataformat.value.Value;
import execute.Environment;
import execute.Executor;
import parse.Parser;
import parse.Token;
import parse.Token.TokenType;

import java.util.List;
import java.util.StringJoiner;

/**
 * @since 2/16/2019
 */
public class FunctionDefinition implements FlowController {

    private String name;
    private ParameterList params;
    private int startAddress, endAddress;

    public FunctionDefinition(List<Token> tokens, Parser parser) {
        if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected function name after 'def'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("()")) {
            throw new RuntimeException("Expected function parameters after '" + tokens.get(1).VALUE + "'");
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
    public void execute(Environment env) {
        startAddress = env.getCounter();
        env.assign(name, new UserDefinedFunction(name, this));
        env.setCounter(endAddress + 1);
    }

    public Value call(ArgumentList args, Environment env) {
        List<Statement> body = env.getSubroutine(startAddress + 1, endAddress);
        Environment newScope = new Environment(body, startAddress + 1);
        params.accept(args, newScope);
        Executor.execute(newScope);
        return newScope.getReturnValue();
    }

    /* This method should never be called. */
    @Override
    public boolean isDone(Environment env) {
        throw new IllegalStateException("Cannot call isDone() on user-defined function");
    }

    @Override
    public void setJumpPoint(List<Token> tokens, Parser parser) {
        if (tokens.get(0).matches("end")) {
            this.endAddress = parser.getAddress();
        } else {
            throw new RuntimeException("Unexpected symbol '" + tokens.get(0).VALUE + "'");
        }
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
