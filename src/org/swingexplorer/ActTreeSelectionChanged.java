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

import java.awt.Component;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author Maxim Zakharenkov
 */
public class ActTreeSelectionChanged implements TreeSelectionListener {
    
    MdlSwingExplorer model;
    PNLComponentTree pnlComponentTree;
    
    /** Creates a new instance of ActTreeSelectionChanged */
    public ActTreeSelectionChanged(MdlSwingExplorer modelP, PNLComponentTree pnlComponentTreeP) {
        model = modelP;
        pnlComponentTree = pnlComponentTreeP;
    }

    public void valueChanged(TreeSelectionEvent e) {
	    TreePath[] paths = e.getPaths();
		for (TreePath curPath : paths) {

			Component component = pnlComponentTree.getComponent(curPath);

			if (e.isAddedPath(curPath)) {
				model.addSelection(component);
			} else {
				model.removeSelection(component);
			}
		}
    }    
}
