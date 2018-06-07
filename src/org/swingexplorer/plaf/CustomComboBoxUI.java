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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.metal.MetalComboBoxUI;

import sun.swing.DefaultLookup;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class CustomComboBoxUI extends MetalComboBoxUI {

    
	EtchedBorder border = new EtchedBorder();
	
    @Override
    protected void installDefaults() {
        super.installDefaults();
        comboBox.setFont(PlafUtils.CUSTOM_FONT);
        
    }
    
    public void paint( Graphics g, JComponent c ) {
        hasFocus = comboBox.hasFocus();
        if ( !comboBox.isEditable() ) {
        	
        	// calculate rectangle excluding border
        	Insets ins = border.getBorderInsets(c);
            Rectangle r = super.rectangleForCurrentValue();
            r = new Rectangle(r.x + ins.left, r.y + ins.top, r.width - ins.left - ins.right, r.height - ins.top - ins.bottom);
            
            paintCurrentValueBackground(g,r,hasFocus);
            border.paintBorder(c, g, 0, 0, c.getWidth(), c.getHeight());
            paintCurrentValue(g,r,hasFocus);
        }
    }
    
    public void paintCurrentValue(Graphics g,Rectangle bounds,boolean hasFocus) {
        ListCellRenderer renderer = comboBox.getRenderer();
        Component c;

        if ( hasFocus && !isPopupVisible(comboBox) ) {
            c = renderer.getListCellRendererComponent( listBox,
                                                       comboBox.getSelectedItem(),
                                                       -1,
                                                       true,
                                                       false );
        }
        else {
            c = renderer.getListCellRendererComponent( listBox,
                                                       comboBox.getSelectedItem(),
                                                       -1,
                                                       false,
                                                       false );
            c.setBackground(UIManager.getColor("ComboBox.background"));
        }
        c.setFont(comboBox.getFont());
        if ( hasFocus && !isPopupVisible(comboBox) ) {
            c.setForeground(listBox.getSelectionForeground());
            c.setBackground(listBox.getSelectionBackground());
        }
        else {
            if ( comboBox.isEnabled() ) {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            }
            else {
                c.setForeground(DefaultLookup.getColor(
                         comboBox, this, "ComboBox.disabledForeground", null));
                c.setBackground(DefaultLookup.getColor(
                         comboBox, this, "ComboBox.disabledBackground", null));
            }
        }

        // Fix for 4238829: should lay out the JPanel.
        boolean shouldValidate = false;
        if (c instanceof JPanel)  {
            shouldValidate = true;
        }

        currentValuePane.paintComponent(g,c,comboBox,bounds.x,bounds.y,
                                        bounds.width,bounds.height, shouldValidate);
    }

}
