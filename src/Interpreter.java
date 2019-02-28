import dataformat.Statement;
import dataformat.value.LNull;
import dataformat.value.Value;
import execute.Environment;
import execute.Executor;
import parse.Lexer;
import parse.Parser;
import parse.Token;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * This class is the public interface of the interpreter. You can execute a Legend file
 * by calling {@link #interpret(File)} and passing in the file object.
 * <p>
 * Interpretation is a three-step process:
 * <ol><li>The source code is divided into tokens and statements by the {@link Lexer} class.
 * <li>The statements are parsed into syntax trees by the {@link parse.Parser} class.
 * <li>The syntax trees are traversed and executed by the {@link execute.Executor} class.</ol>
 * @since 1/15/2019
 */
public class Interpreter {

    /*
    This main method executes the provided source file in {@code input.leg}. If you don't want
    to make a new source file, you can simply edit the provided file and run this method.
     */
    public static void main(String[] args) {
        interpret(new File("input.leg"));
    }

    /**
     * Execute a Legend source file.
     * @param sourceFile the file to execute
     */
    public static void interpret(File sourceFile) {
        if (Files.notExists(sourceFile.toPath())) {
            throw new RuntimeException("Couldn't locate source file: " + sourceFile.getAbsolutePath());
        }

        if (!sourceFile.getName().endsWith(".leg")) {
            throw new RuntimeException("Couldn't read source file: does not end in .leg");
        }

        String input;
        try {
            input = new String(Files.readAllBytes(sourceFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read source file: " + e.getMessage());
        }

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        List<List<Token>> tokens = lexer.tokenize(input);
        List<Statement> statements = parser.parse(tokens);

        Environment.GLOBAL.setProgram(statements);
        Executor.execute(Environment.GLOBAL);

        Value returnValue = Environment.GLOBAL.getReturnValue();
        if (returnValue != LNull.NULL) {
            System.out.println("Program returned value: " + returnValue.asString());
        }
    }

}
