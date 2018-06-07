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
package org.swingexplorer.awt_events;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.swingexplorer.RichAction;
import org.swingexplorer.awt_events.filter.FilterChangeListener;
import org.swingexplorer.awt_events.filter.PNLEventFilter;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class ActShowFilter extends RichAction {

	
	PNLAwtEvents owner;
	JPopupMenu popup;
	boolean selected = false;
    JDialog dlg;

	public ActShowFilter(PNLAwtEvents _owner) {
		owner =_owner;
		setName("Filter");

        
	}
	
	
	public void actionPerformed(ActionEvent e) {
        if(dlg == null) {
            //  create undecorated dialog
            Window wndAncestor = SwingUtilities.getWindowAncestor(owner);
            dlg = new JDialog((Frame)wndAncestor);
            dlg.setTitle("Event filter");
            dlg.setUndecorated(true);
            PNLEventFilter pnlFilter = new PNLEventFilter();
            pnlFilter.setBorder(UIManager.getBorder("PopupMenu.border"));// new LineBorder(Color.BLACK));
            dlg.add(pnlFilter);
            
            
            // add listener to hide dialog when it is deactivated
            dlg.addWindowListener(new WindowAdapter() {
                @Override
                public void windowDeactivated(WindowEvent e) {
                    // hide only when mouse is not over button
                    if(owner.btnShowFilter.getMousePosition() == null) {
                        dlg.setVisible(false);
                    }
                }
            });
            
            dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"/**/), "esc_action"/**/
            );
            dlg.getRootPane().getActionMap().put("esc_action"/**/, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    dlg.setVisible(false);
                }
            });
            pnlFilter.setFilter(owner.getEventModel().getFilter());
            
            pnlFilter.addFilterChangeListener(new FilterChangeListener() {
                public void filterChanged(Filter newFilter) {
                    owner.getEventModel().setFilter(newFilter);
                }
            });
            
            dlg.pack();
        }
        
		// set dialog's location near btnShowFilter button
		Point location = owner.btnShowFilter.getLocationOnScreen();
		location.x += owner.btnShowFilter.getWidth();
		dlg.setLocation(location);
		dlg.setVisible(!dlg.isVisible());
	}
}
