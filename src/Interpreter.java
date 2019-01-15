import dataformat.Expression;
import execute.Executor;
import parse.Parser;
import parse.Token;
import parse.Tokenizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * @since 1/15/2019
 */
public class Interpreter {

    public static void main(String[] args) {
        interpret(new File("src/input.txt"));
    }

    public static void interpret(File sourceFile) {
        if (Files.notExists(sourceFile.toPath())) {
            throw new RuntimeException("Couldn't locate source file: " + sourceFile.getAbsolutePath());
        }

        String input;
        try {
            input = new String(Files.readAllBytes(sourceFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read source file: " + e.getMessage());
        }

        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser();
        Executor executor = new Executor();

        List<List<Token>> tokens = tokenizer.tokenize(input);
        List<Expression> statements = parser.parse(tokens);
        executor.execute(statements);
    }

}
