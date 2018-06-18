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
package org.swingexplorer.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class CustomSplitPaneUI extends BasicSplitPaneUI {

	    public static ComponentUI createUI(JComponent x) {
	    	return new CustomSplitPaneUI();
	    }

	    protected void installDefaults() {
	    	super.installDefaults();
	    	getDivider().setDividerSize(3);
	        splitPane.setBorder(null);
	    }
	    
	    /**
	      * Creates the default divider.
	      */
	    public BasicSplitPaneDivider createDefaultDivider() {
	    	return new CustomSplitPaneDivider(this);
	    }
}

