Swing Explorer User's Guide
===========================

#  Running Swing Explorer

##  Using the **swexpl** command line tool

Swing Explorer comes with a tool called **swexpl** that makes it easy to launch Swing Explorer from the command line. You point it at your program's JARs and main class, and it takes care of pulling in Swing Explorer and calling the launcher.

The **swexpl** tool is found in the `bin/` subdirectory of the Swing Explorer distribution.

```
swexpl -cp <classpath> <mainclass>
swexpl --agent -cp <classpath> <mainclass>
```

The `<classpath>` argument is a colon-separated list of paths to the JARs or classes in your program.

The `<mainclass>` argument is the fully-qualified name of your program's main class.

The `--agent` argument tells Swing Explorer to load its instrumentation code as a Java agent, enabling additional EDT-monitoring functionality.

Examples:

```
swexpl -cp path/to/MyProgram.jar com.example.myprogram.MainClass
swexpl --agent -cp path/to/MyProgram.jar com.example.myprogram.MainClass
```


##  Using Swing Explorer as a library

You can also use Swing Explorer as a library in your program. To do so, get the Swing Explorer JARs (`swingexplorer-agent-<version>.jar` and `swingexplorer-core-<version>.jar`) and their dependencies (found in the `lib/` directory of the distribution) on your Java classpath, and use `org.swingexplorer.Launcher` as the main class of your program, supplying the name of your own program's main class as a command line argument.

For details on how this can be done, see the source code for `swexpl`, found in `bin/` in the Swing Explorer distribution, or in `src/dist-files/bin` in the `swingexplorer` repository.