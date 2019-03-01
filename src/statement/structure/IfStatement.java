package statement.structure;

import expression.Expression;
import expression.value.LBoolean;
import execute.Environment;
import parse.Parser;
import parse.Token;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

/**
 * @since 1/27/2019
 */
public class IfStatement implements FlowController {

    private int startAddress, endAddress;
    private Map<Expression, Integer> branches;

    public IfStatement(List<Token> tokens, Parser parser) {
        startAddress = parser.getAddress();
        branches = new LinkedHashMap<>();
        parseIf(tokens, parser);
    }

    @Override
    public void execute(Environment env) {
        if (env.getCounter() != startAddress) {
            // we're at an elsif or else branch, which means we finished the
            // branch that executed, so we can jump straight to the end
            env.setCounter(endAddress);
        } else {
            // if we don't find a branch to jump to, we should go to the end
            env.setCounter(endAddress);
            env.getControlStack().push(this);
            for (Entry<Expression, Integer> branch : branches.entrySet()) {
                if (branch.getKey().evaluate(env).asBoolean()) {
                    // a branch was found, so we'll jump there instead
                    env.setCounter(branch.getValue() + 1);
                    break;
                }
            }
        }
    }

    @Override
    public boolean isDone(Environment env) {
        return true;
    }

    @Override
    public void setJumpPoint(List<Token> tokens, Parser parser) {
        switch (tokens.get(0).VALUE) {
            case "end":
                endAddress = parser.getAddress();
                break;
            case "elsif":
                parseElsif(tokens, parser);
                break;
            case "else":
                parseElse(tokens, parser);
                break;
            default:
                throw new RuntimeException("Unexpected symbol '" + tokens.get(0).VALUE + "'");
        }
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        for (Entry<Expression, Integer> branch : branches.entrySet()) {
            joiner.add(String.format("(%s -> %h)", branch.getKey(), branch.getValue()));
        }
        return "if(" + joiner + ")";
    }

    private void parseIf(List<Token> tokens, Parser parser) {
        if (tokens.size() == 1 || !tokens.get(1).isValue()) {
            throw new RuntimeException("Expected boolean expression after 'if'");
        }
        Expression control = parser.parseFrom(tokens, 1);
        branches.put(control, startAddress);
    }

    private void parseElsif(List<Token> tokens, Parser parser) {
        if (tokens.size() == 1 || !tokens.get(1).isValue()) {
            throw new RuntimeException("Expected boolean expression after 'elsif'");
        }
        Expression control = parser.parseFrom(tokens, 1);
        branches.putIfAbsent(control, parser.getAddress());
    }

    private void parseElse(List<Token> tokens, Parser parser) {
        if (tokens.size() > 1) {
            throw new RuntimeException("Unxpected symbol '" + tokens.get(1).VALUE + "' (did you mean 'elsif'?)");
        }
        branches.putIfAbsent(LBoolean.TRUE, parser.getAddress());
    }

}