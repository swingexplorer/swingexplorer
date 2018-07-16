/*
 *   Swing Explorer. Tool for developers exploring Java/Swing-based application internals. 
 * 	 Copyright (C) 2012, Maxim Zakharenkov
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *   
 */
package org.swingexplorer;


import java.lang.reflect.Method;
import java.text.MessageFormat;

import javax.swing.SwingUtilities;

import org.swingexplorer.internal.*;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class Launcher {

    /**
     * Launch the Swing Explorer GUI.
     *
     * This launches the Swing Explorer GUI in the current process. It does not
     * do anything about calling the user program's main method. This method
     * is useful for programs that wish to use Swing Explorer as a library and
     * launch it on their own initiative after the application has started.
     *
     * If you are using Launcher's main() method, you do not need to call this.
     *
     * Does not throw exceptions. If an error occurs during launching, the stack
     * trace is printed to standard error.
     */
	public static void launch() {
        try {
            final Application app = new Application();
            SwingUtilities.invokeAndWait(app);
        } catch (Exception e) {
            System.err.println("An error occurred while starting Swing Explorer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Run a user-specified program with Swing Explorer running in it.
     *
     * This main method will set up Swing Explorer in the current Java process
     * and then pass control to the main() method of the specified class.
     *
     * If an error occurs (in locating or loading the user program class, running
     * Swing Explorer, or running main() in the user program class), an error
     * message is printed to System.err, and execution continues. This will typically
     * result in the program exiting, with a zero exit status.
     *
     * The first argument to main() is the fully-qualified name of the user program class.
     * The remaining arguments are passed as arguments to the user program class's
     * main() method.
     *
     * @param args the user program class, and any additional arguments to pass to
     *             its main() method
     */
	public static void main(String[] args) {
        
        Method mainMethod = null;
        Class<?> mainClass = null;
        String[] newArgs = null;
        
    	if(args.length == 0) {
            System.err.println("ERROR: Command line arguments have to be specified for Simple mode");
            System.err.println(HELP);
            return;
        }
    	newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);

        String userProgramClassName = args[0];
        customizeProgramAppearance(userProgramClassName);

        try {
            mainClass = Class.forName(userProgramClassName);
            mainMethod = mainClass.getMethod("main", String[].class);
        } catch(ClassNotFoundException ex) {
            String msg = MessageFormat.format("ERROR: can not find class {0} specified as argument. Please check classpath and class name.",
                userProgramClassName);
            System.err.println(msg);
            return;
        } catch(NoSuchMethodException ex) {
            String msg = MessageFormat.format("ERROR: can not find main method in the class {0}.", userProgramClassName);
            System.err.println(msg);
            return;
        }

        // launching Swing explorer
        launch();

        // launching application
        try {
            mainMethod.invoke(mainClass, new Object[]{newArgs});
        } catch(Exception ex) {
            String msg = MessageFormat.format("ERROR: cannot invoke main method in the class {0}.", userProgramClassName);
            System.err.println(msg);
            ex.printStackTrace(System.err);
        }
	}

    private static final String HELP =
        "Swing Explorer application can be executed in 2 modes:\n" +
            "Simple mode (faster):\n" +
            "  java -cp swingexplorer-core-<version>.jar[;<your_class_path>] org.swingexplorer.Launcher <your_main_class>\n" +
            "\n" +
            "Agent mode (slower, but has a bit more functionality):\n" +
            "  java -javaagent:swingexplorer-agent-<version>.jar -Xbootclasspath/a:swingexplorer-agent-<version>.jar -cp swingexplorer-core-<version>.jar;[<your_class_path>] org.swingexplorer.Launcher <your_main_class>\n";

    private static void customizeProgramAppearance(String userProgramClassName) {
        int ixLastDot = userProgramClassName.lastIndexOf('.');
        String userProgramClassSimpleName = (ixLastDot >= 0) ? userProgramClassName.substring(ixLastDot + 1)
            : userProgramClassName;
        String appDisplayName = "Swing Explorer - " + userProgramClassSimpleName;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            System.setProperty("apple.awt.application.name", appDisplayName);
        }
    }
}

