# Legend
Legend is a WIP multi-purpose programming language that is intended to be intuitive and easy to learn. Features are being added and updated all the time, so some of the examples shown here may not represent the intended final look of the language.

Here's an example of a simple program:

```
"What's your name?"
name = String.read()
if #name == 0
    "You must enter a name!"
else if name == 'ValorCat'
    "I doubt that."
else
    ('Hello, ' & name).show()
end
```

Checkout the wiki for a syntax reference: https://github.com/ValorCat/legend/wiki

## Running the Interpreter
There are two versions of the interpreter. Read below to determine which version is best for you.

### Standalone Version
This version is slightly larger (~25 mb unzipped), but has no external dependencies. Simply go to the releases page and choose the latest version:

**Releases:** https://github.com/ValorCat/legend/releases

At the bottom under "Assets", download `legend-standalone.zip`. Unzip it and navigate to the `bin` subdirectory. You can run the interpreter by double-clicking `legend.bat` (on Windows) or `legend` (on Mac/Linux).

You can also run the interpreter from the terminal with:

```
./bin/legend <source-file>
```

### Java-Dependent Version
This version is much smaller (~200 kb), but requires Java 11 installed. If you don't have Java 11 but want to use this version, you can download the Java 11 JDK from Oracle:

**JDK 11:** https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

Once Java is installed, go to the Legend releases page and choose the latest version:

**Releases:** https://github.com/ValorCat/legend/releases

At the bottom under "Assets", download `legend.jar`. On some systems, you can double-click the JAR file to run it. Otherwise, you can run the interpreter from the terminal with:

```
java -jar legend.jar <source-file>
```