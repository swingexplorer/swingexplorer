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

import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class FRMSwingExplorer extends JFrame {

	PNLSwingExplorer pnlSwingExplorer;
	JMenuBar menuBar;
	JMenu mnuHelp;
	
    ActHelpAbout actHelpAbout;
    ActHelp actHelp;
	
	Launcher application;
	
		
	public FRMSwingExplorer() {
		initComponents();
		initActions();
	}
	
	private void initComponents() {
		// initialize frame
        setTitle("Swing Explorer");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // initialize main panel
        pnlSwingExplorer = new PNLSwingExplorer();
		this.add(pnlSwingExplorer);
		
        Image appImage = Icons.appSmallImage();
        this.setIconImage(appImage);

        // initialize menu
        menuBar = new JMenuBar(); 
        mnuHelp = new JMenu("Help");        
        menuBar.add(mnuHelp);
        this.setJMenuBar(menuBar);
        
        // doing the following operation through reflection
        // this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        // this is done to support JRE 1.5 as well
        try {
			Class<?> exclusionType = Class.forName("java.awt.Dialog$ModalExclusionType");
			Field field = exclusionType.getField("APPLICATION_EXCLUDE");
			Object value = field.get(exclusionType);
			
			Method meth = JFrame.class.getMethod("setModalExclusionType", new Class[]{exclusionType});
			meth.invoke(this, value);
		} catch (Exception e) {
			Log.general.warn("Application exclusive modality is not available on this Java platform. It is recommended to use java 1.6 or later.");
		}
	}
	
	void initActions() {
		actHelpAbout = new ActHelpAbout(this);
        actHelp = new ActHelp();
               
        mnuHelp.add(actHelp);
        mnuHelp.add(actHelpAbout);
        
        addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
 			    // invoke refresh
		        SwingUtilities.invokeLater(new Runnable() {
		        	public void run() {  
		        		pnlSwingExplorer.actRefresh.actionPerformed(null);
		        	}
		        });
			}
		});
	}

	public Launcher getApplication() {
		return application;
	}

	public void setApplication(Launcher _application) {
		this.application = _application;
		pnlSwingExplorer.setApplication(_application);
	}
}

