package statement.structure;

import execute.Environment;
import expression.Expression;
import expression.value.Value;
import parse.Parser;
import parse.Token;
import parse.error.ErrorLog;

import java.util.List;

import static parse.error.ErrorDescription.BAD_WHILE_LOOP;

/**
 * @since 1/20/2019
 */
public class WhileLoop implements FlowController {

    private int startAddress, endAddress;
    private Expression condition;

    public WhileLoop(List<Token> tokens, Parser parser) {
        if (tokens.size() == 1 || !tokens.get(1).isValue()) {
            throw ErrorLog.raise(BAD_WHILE_LOOP, "Expected boolean condition after 'while'");
        }
        condition = parser.parseFrom(tokens, 1);
    }


    @Override
    public void execute(Environment env) {
        if (checkCondition(env)) {
            env.getControlStack().push(this);
            startAddress = env.getCounter();
        } else {
            env.setCounter(endAddress + 1);
        }
    }

    @Override
    public boolean isDone(Environment env) {
        if (checkCondition(env)) {
            env.setCounter(startAddress + 1);
            return false;
        }
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
    public String toString() {
        return "while(" + condition + ")";
    }

    private boolean checkCondition(Environment env) {
        Value result = condition.evaluate(env);
        if (!result.isType("Boolean")) {
            throw new RuntimeException("While loop expected boolean condition, got type '"
                    + result.type().getName() + "'");
        }
        return result.asBoolean();
    }

}
