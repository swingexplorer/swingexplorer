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

import org.swingexplorer.graphics.Operation;
import org.swingexplorer.idesupport.IDENotConnectedException;
import org.swingexplorer.idesupport.IDESupport;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class ActOpenSourceCode extends RichAction {

    IDESupport ideSupport;
    PNLPlayerControls owner;
    
    public ActOpenSourceCode(PNLPlayerControls _owner) {
        setName("src");
        setTooltip("Open sourcecode in IDE");
        owner = _owner;
    }
    
    public void actionPerformed(ActionEvent e) {
        
        Operation op = owner.player.getCurrentOperation();
        if(op == null) {
        	return;
        }
        StackTraceElement[] elems = op.getStackTrace();
        
        if(elems == null) {
        	JOptionPane.showMessageDialog(owner, "Current operation has no source code");
        	return;
        }
        
        try {
        	ideSupport.requestOpenSourceCode(elems[0].getClassName(), elems[0].getLineNumber());
    	} catch(IDENotConnectedException ex) {
    		JOptionPane.showMessageDialog(owner, 
    				"Can not open source code. IDE connection is not available.\n" +
    				"The connection is available only when application is launched\n" +
    				"from IDE using Swing Explorer plug-in.");
    		return;
    	}
    }
}