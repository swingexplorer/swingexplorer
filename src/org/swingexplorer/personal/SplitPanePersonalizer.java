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
package org.swingexplorer.personal;

import javax.swing.JSplitPane;

import org.swingexplorer.Log;

/**
 * Saves/loads divider location for SplitPane
 * @author  Maxim Zakharenkov
 */
public class SplitPanePersonalizer extends AbstractOnResizePersonalizer<JSplitPane> {

	private String propertyName;
	
	/**
	 * 
	 * @param _propertyName property name to read/store divider location value
	 */
	public SplitPanePersonalizer(String _propertyName) {
		propertyName = _propertyName;
	}
	
	@Override
	public void applyState() {
		try {
			Object value = options.getValue(propertyName);
			component.setDividerLocation((Integer)value);
		} catch(Exception ex) {
			Log.general.error("Error when applying state for \"" + propertyName + "\"", ex);
		}
	}

	@Override
	public void saveState() {
		try {
			options.setValue(propertyName, component.getDividerLocation());
		} catch(Exception ex) {
			Log.general.error("Error when saving state for \"" + propertyName + "\"", ex);
		}
	}
}


