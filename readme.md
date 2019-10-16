# Legend
Legend is a WIP multi-purpose programming language that is intended to be intuitive and easy to learn. Features are being added and updated all the time, so some of the examples shown here may not represent the intended final look of the language.

Here's an example of a simple program:

```
name = read("What's your name? ")
if #name == 0
    >> "You must enter a name!"
else if name == "ValorCat"
    >> "I doubt that."
else
    >> "Hello, " & name
end
```

Checkout the wiki for a syntax reference: https://github.com/ValorCat/legend/wiki

## Using the Interpreter
The syntax for running the interpeter is:

```shell script
legend [-sw] <source-file> [args...]
```

#### Interpreter Flags
Interpreter flags are special directives to the interpreter.

| Flag | Meaning | Description |
|------|---------|-------------|
| `-s` | Strict Typing | Raises an error on encountering an untyped variable. |
| `-w` | Wait on Exit | Keeps the window open after the program terminates until the user presses any key. |

#### Source File
The path to the source file. The file must have the `.leg` extension, though it doesn't need to be specified. Both of the following are valid:

```
./legend input
./legend input.leg
```

#### Program Arguments
Arguments to pass to the program. These can be accessed within the program from the `args` variable:

```
for a in args
    >> a
end
```

## Interpreter Versions
There are two versions of the interpreter. Read below to determine which version is best for you.

### Standalone Version
This version is slightly larger (~25mb), but has no external dependencies. Simply go to the releases page and choose the latest version:

**Releases:** https://github.com/ValorCat/legend/releases

At the bottom under "Assets", download `legend-standalone.zip` and unzip it. The start script is at `./bin/legend`:

```shell script
./bin/legend <source-file> [args...]
```

### Java-Dependent Version
This version is much smaller (<1mb), but requires Java 11 installed. If you don't have Java 11 but want to use this version, you can download the Java 11 JDK from Oracle:

**JDK 11:** https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

Once Java is installed, go to the Legend releases page and choose the latest version:

**Releases:** https://github.com/ValorCat/legend/releases

At the bottom under "Assets", download `legend-java.zip` and unzip it. You can run the interpeter with the provided start script if Java is on the system path, or manually otherwise:

```shell script
./legend <source-file> [args...]
```

```shell script
path/to/java -jar legend.jar <source-file> [args...]
```