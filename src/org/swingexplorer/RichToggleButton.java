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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JToggleButton;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class RichToggleButton extends JToggleButton {

	SelectionStateListener selectionStateListener;
	
	
	public RichToggleButton() {
		selectionStateListener = new SelectionStateListener();
	}
	
	public RichToggleButton(Action a) {
		this();
		setAction(a);
	}
	
	
	@Override
	public void setAction(Action a) {
		if(a instanceof RichToggleAction) {
			setToggleAction((RichToggleAction)a);
		} else {
			super.setAction(a);
		}
	}
	
	public void setToggleAction(RichToggleAction newAction) {
		if(newAction == getAction()) {
			return;
		}
		
		if(newAction == null) {
			Action curAction = getAction();
			if(curAction != null) {
				curAction.removePropertyChangeListener(selectionStateListener);
			}
		}
		newAction.addPropertyChangeListener(selectionStateListener);		
		super.setAction(newAction);
	}
	
	class SelectionStateListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals("selected")) {
				setSelected((Boolean)evt.getNewValue());
			}
		}
	}
}

