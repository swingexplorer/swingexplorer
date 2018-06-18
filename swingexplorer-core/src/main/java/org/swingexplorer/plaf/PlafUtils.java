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
import java.awt.Container;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

/**
 *
 * @author  Maxim Zakharenkov
 */
public abstract class PlafUtils {

    public static final Font CUSTOM_FONT = new Font("Dialog", Font.PLAIN, 12);
    
    
    public static final void applyCustomLookAndFeel(Container parent) {
        
        for(int i =0; i < parent.getComponentCount(); i ++) {
            Component child = parent.getComponent(i);
            if(child instanceof JComponent) {
                applyCustomLookAndFeel((JComponent)child);
            }
        }
        if(parent instanceof JComponent) {
            setCustomUI(((JComponent)parent));
        }
    }
    
    private static void setCustomUI(JComponent component) {
        if(component instanceof JButton) {
            ((JButton)component).setUI(sharedButtonUI);
        } else if(component instanceof JTabbedPane) {
            ((JTabbedPane)component).setUI(new CustomTabbedPaneUI());
        } else if(component instanceof JTree) {
            ((JTree)component).setUI(new CustomTreeUI());
        } else if(component instanceof JSplitPane) {
            ((JSplitPane)component).setUI(new CustomSplitPaneUI());
        } else if(component instanceof JLabel) {
            ((JLabel)component).setUI(new CustomLabelUI());
        } else if(component instanceof JCheckBox) {
            ((JCheckBox)component).setUI(new CustomCheckboxUI());
        } else if(component instanceof JSpinner) {
            ((JSpinner)component).setUI(new CustomSpinnerUI());
        } else if(component instanceof JComboBox) {
            ((JComboBox)component).setUI(new CustomComboBoxUI());
        } else if(component instanceof JList) {
            ((JList)component).setUI(new CustomListUI());
        }
    }
    
    static CustomButtonUI sharedButtonUI = new CustomButtonUI();
}
