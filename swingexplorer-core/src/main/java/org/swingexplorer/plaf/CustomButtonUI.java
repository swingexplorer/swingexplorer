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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class CustomButtonUI extends BasicButtonUI {

    private Color selectedColor = new Color(200, 221, 242);
    
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
      g.setColor(selectedColor);
      g.fillRect(0, 0, b.getWidth(), b.getHeight());
    }
    
    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setFont(PlafUtils.CUSTOM_FONT);
        b.setBorder(new ButtonBorder());
    }
    
    static class ButtonBorder extends EtchedBorder {
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = 4;
            return insets;
        }
    }
}
