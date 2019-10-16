package legend;

import legend.compiletime.Compiler;
import legend.compiletime.Lexer;
import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.error.InterpreterException;
import legend.compiletime.expression.value.NullValue;
import legend.compiletime.expression.value.Value;
import legend.compiletime.statement.Statement;
import legend.runtime.Program;
import legend.runtime.instruction.Instruction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This class is the public entry point of the interpreter.
 *
 * Interpretation is a 4-step process:
 *
 * 1. The source code is divided into tokens by the {@link Lexer}.
 * 2. The tokens are parsed into statements and syntax trees by the {@link Parser}.
 * 3. The statements are compiled into instructions by the {@link Compiler}.
 * 4. The instructions are executed by the {@link Program}.
 *
 * @see legend.compiletime.Token
 * @see Statement
 * @see legend.runtime.instruction.Instruction
 * @since 1/15/2019
 */
public class Interpreter {

    private static final String USAGE =
            "Usage: legend [flags...] <source-file> [args...]\n" +
            "  Flags: -s (strict typing), -w (wait on exit)\n" +
            "  Source File: path to .leg source file (may omit file extension)\n" +
            "  Args: user-defined program arguments";

    public static boolean strictTyping = false;    // -s flag
    private static boolean waitOnExit = false;      // -w flag

    private static String[] programArgs;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(USAGE);
            return;
        }

        Path sourcePath = parseArguments(args);
        if (sourcePath != null) {
            Path absolutePath = sourcePath.toAbsolutePath();
            if (Files.isRegularFile(sourcePath)) {
                try {
                    interpret(Files.readString(sourcePath));
                } catch (IOException e) {
                    System.err.printf("Cannot read source file at %s: %s\n", absolutePath, e.getMessage());
                }
            } else {
                System.err.println("Cannot find source file: " + absolutePath);
            }
        }

        if (waitOnExit) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Press enter to exit...");
            scanner.nextLine();
            scanner.close();
        }
    }

    /**
     * Parse the interpreter arguments.
     * @param args the interpreter arguments
     * @return the path to the source file, or null if an error occurred
     */
    private static Path parseArguments(String[] args) {
        boolean success = true;
        Path path = null;
        int i;
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                success &= parseFlag(arg);
            } else {
                if (!arg.endsWith(".leg")) {
                    arg += ".leg";
                }
                path = Paths.get(arg);
                break;
            }
        }
        programArgs = Arrays.copyOfRange(args, i + 1, args.length);
        return success ? path : null;
    }

    private static boolean parseFlag(String flag) {
        for (int i = 1; i < flag.length(); i++) {
            switch (flag.charAt(i)) {
                case 's': strictTyping = true; break;
                case 'w': waitOnExit = true; break;
                default:
                    System.err.printf("Unrecognized interpreter flag '%s'\n", flag.charAt(i));
                    return false;
            }
        }
        return true;
    }

    private static void interpret(String source) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        Compiler compiler = new Compiler();

        List<TokenLine> tokens = lexer.tokenize(source);
        List<Statement> statements = parser.parse(tokens);
        List<Instruction> instructions = compiler.compile(statements);

        if (ErrorLog.foundErrors()) {
            List<InterpreterException> errors = ErrorLog.getErrors();
            System.err.printf("The interpeter encountered %d error(s) during parsing.\n\n", errors.size());
            for (InterpreterException e : errors) {
                System.err.printf("(line %d) %s\n", e.getLineNumber(), e.getMessage());
            }
            System.err.println("\nInterpretation aborted.\n");
            return;
        }

        Program.PROGRAM = new Program(instructions);
        Value returnValue = Program.PROGRAM.execute();

        if (returnValue != NullValue.NULL) {
            System.out.println("Program returned value: " + returnValue.asString());
        }
    }

    public static String[] getProgramArgs() {
        return programArgs;
    }

}
