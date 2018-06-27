Swing Explorer
======================

A GUI tool for inspecting Java Swing GUIs. It works in a similar way as developer plugins for HTML browsers
but for Java Swing toolkit. The tool is useful for debugging, testing, inspecting and fine tunning of 
Swing Applications. 

With Swing Explorer you can:
- Visually browse through application component hierarchy
- Monitor AWT/Swing events
- Debug 2D graphics
- Easily find places in the source code responsible for creation of certain peaces of UI. 
- Monitor threading rule violations 

Below is shown how basic Swing application is inspected by Swing Explorer.
More detailed information about Swing Explorer can be found in the User Manual (**TBD**).  
![Swing Explorer](docs/swing_explorer_hints.jpg)

## Running Swing Explorer with your application

### Using standalone mode

Use the `swexpl` command, found in `bin/`, to launch your application.

```
swexpl -cp <classpath> <mainclass>
```

Where `<classpath>` is your program's classpath, and `<mainclass>` is the fully-qualified name of your program's main class.

You can also manually pull in the Swing Explorer libraries and use `org.swingexplorer.Launcher.main()` to run your program with Swing Explorer active.

See the [User Guide](docs/User-Guide.md) for more details.

### Using IDE plug-in
**TBD**


##  Building from source

Using maven it is necessary to run `mvn clean package` command from the root folder of cloned project's repository.
To test whether the build worked, you can cd in to `swingexplorer-core` and run `dev-tools/launch_sample.sh` to run the sample application.
