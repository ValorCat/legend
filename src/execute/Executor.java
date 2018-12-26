package execute;

import dataformat.Expression;
import parse.Parser;
import tokenize.Token;
import tokenize.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @since 12/24/2018
 */
public class Executor {

    public static void main(String[] args) throws IOException {
        String input = new String(Files.readAllBytes(Paths.get("src/input.txt")));

        Tokenizer t = new Tokenizer();
        List<List<Token>> tokens = t.tokenize(input);

        Parser p = new Parser();
        List<Expression> trees = p.parse(tokens);

        for (Expression tree : trees) {
            System.out.println(tree);
        }
        System.out.println("\n-------------------------------------------------------------\n");

        Executor e = new Executor();
        e.execute(trees);
    }

    public void execute(List<Expression> statements) {
        Environment env = new Environment(Environment.GLOBAL);
        for (Expression statement : statements) {
            statement.evaluate(env);
        }
    }

}
