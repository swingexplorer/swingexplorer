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
import java.awt.event.ActionEvent;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class ActDisplayParent extends RichAction {

    Launcher application;
    
    ActDisplayParent(Launcher _launcher) {
        setName("Display parent container");
        setTooltip("<html>Display parent container<br>of the displayed component</html>");
        application = _launcher;
        setIcon("display_parent.png");
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            Component comp = application.model.getDisplayedComponent();
            if(comp == null) {
               throw new DisplayableException("There is no component displayed"); 
            } 
            
            Component parent = comp.getParent();
            if(parent == null) {
                throw new DisplayableException("Displayed component has no parent");
            }
            
            application.model.setDisplayedComponentAndUpdateImage(parent);
            application.model.setSelection(comp);
        } catch (DisplayableException ex) {
        	application.showMessageDialog(ex.getMessage());
        }
    }

}
