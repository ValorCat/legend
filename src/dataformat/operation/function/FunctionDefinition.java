package dataformat.operation.function;

import dataformat.ArgumentList;
import dataformat.Expression;
import dataformat.ParameterList;
import dataformat.group.Parentheses;
import dataformat.operation.Operation;
import dataformat.operation.flow.FlowController;
import dataformat.value.LNull;
import dataformat.value.UserDefinedFunction;
import dataformat.value.Value;
import execute.Environment;
import execute.Executor;
import parse.Token;
import parse.Token.TokenType;

import java.util.List;
import java.util.Stack;
import java.util.StringJoiner;

/**
 * @since 2/16/2019
 */
public class FunctionDefinition extends Operation implements FlowController {

    private String name;
    private ParameterList params;
    private int startAddress, endAddress;

    public FunctionDefinition(int position, List<Token> tokens, Stack<FlowController> controlStack) {
        super(position, tokens);
        controlStack.push(this);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'def'");
        } else if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected function name after 'def'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("()")) {
            throw new RuntimeException("Expected function parameters after '" + tokens.get(1).VALUE + "'");
        } else if (tokens.size() > 3) {
            throw new RuntimeException("Unexpected symbol '" + tokens.get(2).VALUE + "' after function parameters");
        }
        List<Expression> paramData = ((Parentheses) tokens.get(2).asExpression()).getContents();
        name = tokens.get(1).VALUE;
        params = new ParameterList(name, paramData);
        Token.consolidate(tokens, Token.newStatement("def", this), 0, 3);
    }

    /*
    This is essentially a dummy method. When the flow of control reaches
    this function definition, we skip the function body. If the function
    is executed later, the call method is run instead.
     */
    @Override
    public Value evaluate(Environment env) {
        startAddress = env.getCounter();
        env.assign(name, new UserDefinedFunction(name, this));
        env.setCounter(endAddress + 1);
        return LNull.NULL;
    }

    public Value call(ArgumentList args, Environment env) {
        List<Expression> body = env.getProgram().subList(startAddress + 1, endAddress);
        Environment newScope = new Environment(body, env);
        params.accept(args, env);
        Executor.execute(newScope);
        return newScope.getReturnValue();
    }

    /* This method should never be called. */
    @Override
    public boolean isDone(Environment env) {
        throw new IllegalStateException("Cannot call isDone() on user-defined function");
    }

    @Override
    public void setJumpPoint(int address, int tokenPos, List<Token> statement) {
        if (statement.get(tokenPos).matches("end")) {
            this.endAddress = address;
        } else {
            throw new RuntimeException("Unexpected symbol '" + statement.get(0).VALUE + "'");
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
