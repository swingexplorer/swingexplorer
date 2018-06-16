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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.swingexplorer.awt_events.PNLAwtEvents;



/**
 * 
 * @author Maxim Zakharenkov
 */
public class ActShowEventSource extends RichAction {
	
	MdlSwingExplorer mdlSwingExplorer;
	PNLAwtEvents pnlAwtEvents;

	public ActShowEventSource(MdlSwingExplorer _mdlSwingExplorer, PNLAwtEvents _pnlAwtEvents) {
		mdlSwingExplorer = _mdlSwingExplorer;
		pnlAwtEvents = _pnlAwtEvents;
		setName("Show event source");
		setTooltip("Select component the event comes from");
	}

	public void actionPerformed(ActionEvent e) {
		AWTEvent evt = pnlAwtEvents.getSelectedEvent();
		if(evt == null) {
			JOptionPane.showMessageDialog(pnlAwtEvents, "There is no event selected");
		} else {
			// update displayed component to source's ancestor
			Component evtSource = (Component)evt.getSource();
			
			// find topmost source's container and use it as displayed component
			boolean visible = evtSource.isVisible();
			Component sourceContainer = evtSource;
			while(sourceContainer.getParent() != null) {
				sourceContainer = sourceContainer.getParent();
				visible = visible && sourceContainer.isVisible();
			}
			
			
			if(!visible) {
				JOptionPane.showMessageDialog(pnlAwtEvents, "Source component of one of it's ancestors is invisible. It may cause some wrong appearance.");
			}
			
			try {
                mdlSwingExplorer.setDisplayedComponent(sourceContainer);
            } catch (DisplayableException ex) {
                JOptionPane.showMessageDialog(pnlAwtEvents, ex.getMessage());
            }
			
			// select event source component
			mdlSwingExplorer.setSelection(evtSource);
		}
	}
}
