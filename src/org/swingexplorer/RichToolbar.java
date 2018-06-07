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

import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.swingexplorer.plaf.CustomButtonUI;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class RichToolbar extends JToolBar {

	public AbstractButton addActionEx(Action a) {
		String text = a != null ? (String) a.getValue(Action.NAME) : null;
		Icon icon = a != null ? (Icon) a.getValue(Action.SMALL_ICON) : null;
		boolean enabled = a != null ? a.isEnabled() : true;
		String tooltip = a != null ? (String) a
				.getValue(Action.SHORT_DESCRIPTION) : null;

				
		AbstractButton b;
		
		if(a instanceof RichToggleAction) {
			b = new RichToggleButton((RichToggleAction)a);
			b.setText("");
		} else {
			b = new JButton(text, icon) {
				protected PropertyChangeListener createActionPropertyChangeListener(
						Action a) {
					PropertyChangeListener pcl = createActionChangeListener(this);
					if (pcl == null) {
						pcl = super.createActionPropertyChangeListener(a);
					}
					return pcl;
				}
			};
			b.setUI(new CustomButtonUI());
		}
		
		if (icon != null) {
			b.putClientProperty("hideActionText", Boolean.TRUE);
		}
		b.setHorizontalTextPosition(JButton.CENTER);
		b.setVerticalTextPosition(JButton.BOTTOM);
		b.setEnabled(enabled);
		b.setToolTipText(tooltip);
		
		b.setAction(a);
	    add(b);
	    return null;
	 }	
}

