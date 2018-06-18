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

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class ActMouseClickOnDisplay extends MouseAdapter {
	
	PNLGuiDisplay display;
	MdlSwingExplorer model;
	
	ActMouseClickOnDisplay(PNLGuiDisplay displayP, MdlSwingExplorer modelP) {
		display = displayP;
		model = modelP;
	}
	
    @Override
    public void mousePressed(MouseEvent e) {
        Component comp = display.getDisplayedComponentAt(e.getPoint());
        if(comp == null) {
            return;
        }

        handlePressed(e, comp);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Component comp = display.getDisplayedComponentAt(e.getPoint());
        if(comp == null) {
            return;
        }

        if(e.getClickCount() == 2) {
            doubleClick(e, comp);
        }
    }
    
	void handlePressed(MouseEvent e, Component comp) {
		if(e.getButton() == BUTTON1) {
			
			int onmask = CTRL_DOWN_MASK;
		 	if ((e.getModifiersEx() & onmask) == onmask) {
		 		// add selection if control is pressed
		 		if(model.isSelected(comp)) {
		 			model.removeSelection(comp);
		 		} else {
		 			model.addSelection(comp);
		 		}
		 	} else {
		 		// select only one component (remove other selections)
		 		if(model.isSelected(comp)) {
		 			model.setSelection(null);
		 		} else {
		 			model.setSelection(comp);
		 		}
		 	}
            model.setMeasurePoint1(null);
		} else if(e.getButton() == BUTTON3) {
			model.setMeasurePoint1(e.getPoint());
		}
		
		// we need focus for diplay to receive key events over it
		if(!display.hasFocus()) {
			display.requestFocusInWindow();
		}
	}
	
	void doubleClick(MouseEvent e, Component comp) {
		if(e.getButton() == BUTTON1) {
			try {
                model.setDisplayedComponent(comp);
            } catch (DisplayableException ex) {
                JOptionPane.showMessageDialog(display, ex.getMessage());
            }
		}
	}
}