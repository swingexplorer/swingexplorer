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

import javax.swing.JOptionPane;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class ActHelp extends RichAction {
	
	public ActHelp() {
		setName("User documentation");
		setTooltip("User documentation");
	}

	public void actionPerformed(ActionEvent e) {
		if(!SysUtils.openBrowser("http://www.swingexplorer.com/?page=documentation")) {
			JOptionPane.showMessageDialog(null, "Can not open browser!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}