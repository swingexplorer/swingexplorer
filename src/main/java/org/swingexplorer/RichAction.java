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

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * 
 * @author Maxim Zakharenkov
 */
@SuppressWarnings("serial")
public abstract class RichAction extends AbstractAction {

	protected Launcher application;
	
	public void setName(String name) {
		putValue(NAME, name);
	}
	public void setTooltip(String tooltip) {
		putValue(SHORT_DESCRIPTION, tooltip);
	}
	public void setIcon(Icon ico) {
		putValue(SMALL_ICON, ico);
	}
	public void setIcon(String iconResource) {
        setIcon(GuiUtils.getImageIcon(Icons.BASE_PATH + iconResource));
	}
	
	public void setApplication(Launcher app) {
		application = app;
	}
	
	public Launcher getApplication() {
		return application;
	}
}

