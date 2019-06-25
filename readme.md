# Legend
Legend is a WIP multi-purpose programming language that is intended to be intuitive and easy to learn. Features are being added and updated all the time, so some of the examples shown here may not represent the intended final look of the language.

Here's an example of a simple program:

```
"What's your name?"
name = String read
if #name == 0
    "You must enter a name!"
else if name == 'ValorCat'
    "I doubt that."
else
    ('Hello, ' :: name) show
end
```

Checkout the wiki for a syntax reference: https://github.com/ValorCat/legend/wiki

## Running the Interpreter
You can find the latest versions of the interpreter here:

- **Releases:** https://github.com/ValorCat/legend/releases

### On Windows
There are no external dependencies. On the releases page, download the EXE file and run it on the command line:

```
.\legend path\to\input.leg
```

### On Other Platforms
The Java 11 JDK is required to run the interpreter on other platforms. You can download it here if you don't already have it installed:

**JDK 11:** https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

On the Legend releases page, download the JAR file and run it in the terminal:

```
java -jar legend.jar path/to/input.leg
```