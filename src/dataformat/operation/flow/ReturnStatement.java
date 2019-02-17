package dataformat.operation.flow;

import dataformat.Expression;
import dataformat.operation.Operation;
import dataformat.value.LNull;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;
import java.util.Stack;

/**
 * @since 2/16/2019
 */
public class ReturnStatement extends Operation {

    private Expression value;

    public ReturnStatement(int position, List<Token> tokens, Stack<FlowController> controlStack) {
        super(position, tokens);
        if (controlStack.isEmpty() || !controlStack.peek().getKeyword().equals("def")) {
            throw new RuntimeException("Cannot return while outside a function");
        }
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'return'");
        } else if (tokens.size() > 1 && !tokens.get(1).isValue()) {
            throw new RuntimeException("Expected expression after 'return'");
        } else if (tokens.size() > 2) {
            throw new RuntimeException("Unexpected symbol '" + tokens.get(2).VALUE + "'");
        }
        if (tokens.size() == 2) {
            // return a value
            value = tokens.get(1).asExpression();
            Token.consolidate(tokens, new Token("return", this), 0, 2);
        } else {
            // return without a value
            value = LNull.NULL;
            Token.consolidate(tokens, new Token("return", this), 0, 1);
        }
    }

    @Override
    public Value evaluate(Environment env) {
        env.setReturnValue(value.evaluate(env));
        env.setCounter(Integer.MAX_VALUE);
        return LNull.NULL;
    }

}
