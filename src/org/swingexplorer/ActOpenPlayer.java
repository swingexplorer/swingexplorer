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


import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class ActOpenPlayer extends RichToggleAction {

	MdlSwingExplorer model;
	
	ActOpenPlayer(MdlSwingExplorer modelP) {
		setName("Open Player");
		setTooltip("Open Player");
		setIcon(DefaultIcon.INSTANCE);
		model = modelP;
	}
	
	public void actionPerformed(ActionEvent e) {
		setSelected(!isSelected());
		
		if (application.dlgPlayerControls == null) {
			application.dlgPlayerControls = new JDialog(application.frmMain,
					"Player");
			PNLPlayerControls pnlPlayerControls = new PNLPlayerControls();
			application.dlgPlayerControls.add(pnlPlayerControls);
			pnlPlayerControls.setPlayer(application.player);
			application.dlgPlayerControls.setBounds(380, 450, 590, 280);
			
			application.dlgPlayerControls.addWindowListener(new WindowAdapter(){
				@Override
				public void windowClosing(WindowEvent e) {
					ActOpenPlayer.this.setSelected(false);
				}
			});
		}
		application.dlgPlayerControls.setVisible(isSelected());		
	}

}