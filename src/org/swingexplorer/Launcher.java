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


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.swingexplorer.edt_monitor.EDTDebugQueue;
import org.swingexplorer.graphics.Player;
import org.swingexplorer.idesupport.IDESupport;
import org.swingexplorer.personal.FramePersonalizer;
import org.swingexplorer.personal.PersonalizerRegistry;
import org.swingexplorer.personal.SplitPanePersonalizer;
import org.swingexplorer.personal.TabbedPanePersonalizer;
import org.swingexplorer.personal.TablePersonalizer;
import org.swingexplorer.plaf.PlafUtils;


/**
 * 
 * @author Maxim Zakharenkov
 */
public class Launcher implements Runnable {
    
    public IDESupport ideSupport;
    
	public FRMSwingExplorer frmMain;
	public PNLPlayerControls pnlPlayerControls;
	public JDialog dlgPlayerControls;	
	public MdlSwingExplorer model = new MdlSwingExplorer();
    public Player player = new Player();
    
	private PersonalizerRegistry personalizerRegistry;
    
    
	public void run() {
		// register JMX bean for IDE support
        ideSupport = IDESupport.registerMBean();
        EDTDebugQueue.initMonitoring();
		
        // create frame
        frmMain = new FRMSwingExplorer();
        frmMain.setName("frmMain");
        frmMain.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent evt) {
        		exitApplication();
        	}
        });
        
		// we use own L&F for swing explorer to avoid conflict with application's L&F
        PlafUtils.applyCustomLookAndFeel(frmMain.getContentPane());
		
        // load options and set to interested parties
        Options options =  new Options();
        options.load();
        model.setOptions(options);
		player.setOptions(options);
        frmMain.setApplication(this);
        personalizerRegistry = new PersonalizerRegistry(frmMain, options);
        
        // add component personalizers
        personalizerRegistry.addPersonalizer("frmMain", new FramePersonalizer());
        personalizerRegistry.addPersonalizer("sppMain", new SplitPanePersonalizer("verticalDividerLocation"));
        personalizerRegistry.addPersonalizer("sppRight", new SplitPanePersonalizer("horizontalDividerLocation"));
        personalizerRegistry.addPersonalizer("sppMasterDetail", new SplitPanePersonalizer("eventTabDividerLocation"));
        personalizerRegistry.addPersonalizer("tbpTrees", new TabbedPanePersonalizer("selectedTreeTabIndex"));
        personalizerRegistry.addPersonalizer("tbpBottom", new TabbedPanePersonalizer("selectedToolTabIndex"));
        personalizerRegistry.addPersonalizer("tblEvents", new TablePersonalizer("eventTableColumnSizes"));
        personalizerRegistry.addPersonalizer("pnlEventProperties.tblProperties", new TablePersonalizer("eventDetailTableColumnSizes"));
        personalizerRegistry.addPersonalizer("pnlPropertySheet.tblProperties", new TablePersonalizer("propertyTableColumnSizes"));

		// open frame
		frmMain.setVisible(true);
	}
	
	public void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(frmMain, message);
	}

	private void exitApplication() {
		int res = JOptionPane.showOptionDialog(frmMain, "Do you want to finish application or close Swing Explorer window?", "Exit", JOptionPane.DEFAULT_OPTION, 
				JOptionPane.QUESTION_MESSAGE,null, new Object[]{"Exit Application", "Close Window", "Cancel"}, "Exit");
		
		// save personalization state onto options
		personalizerRegistry.saveState();
		
		// save options to file
		Options opts = model.getOptions();
		opts.save();
		
		if(res == 0) {
			System.exit(0);
		} else if(res == 1){
			frmMain.dispose();
		}
	}
	

    static final String HELP = 
            "Swing Explorer application can be executed in 2 modes:\n" +
            "Simple mode (faster):\n" +
            "  java -cp swexpl.jar[;<your_class_path>] org.swingexplorer.Launcher <your_main_class>\n" +
            "\n" +
            "Agent mode (slower, but has a bit more functionality):\n" +
            "  java -javaagent:swag.jar -Xbootclasspath/a:swag.jar -cp swexpl.jar;[<your_class_path>] org.swingexplorer.Launcher <your_main_class>\n";
    
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
		for (int i = 0; i < newArgs.length; i++) {
			newArgs[i] = args[i + 1];
		}
		
        try {
            mainClass = Class.forName(args[0]);
			mainMethod = mainClass.getMethod("main", new Class[]{String[].class});
        } catch(ClassNotFoundException ex) {
            String msg = MessageFormat.format("ERROR: can not find class {0} specified as argument. Please check classpath and class name.", args[0]);
            System.err.println(msg);
            return;
        } catch(NoSuchMethodException ex) {
            String msg = MessageFormat.format("ERROR: can not find main method in the class {0}.", args[0]);
            System.err.println(msg);
            return;
        }

        // launching Swing explorer
        final Launcher app = new Launcher();
        try {
			SwingUtilities.invokeAndWait(app);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        // launching application
        try {
            mainMethod.invoke(mainClass, new Object[]{newArgs});
        } catch(Exception ex) {
            String msg = MessageFormat.format("ERROR: cannot invoke main method in the class {0}.", args[0]);
            System.err.println(msg);
            ex.printStackTrace(System.err);
        }
	}

}

