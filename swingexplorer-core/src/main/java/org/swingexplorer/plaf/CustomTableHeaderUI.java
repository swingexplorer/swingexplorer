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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class CustomTableHeaderUI extends BasicTableHeaderUI {

	@Override
	protected void installDefaults() {
		super.installDefaults();
		JComponent c = (JComponent) header.getDefaultRenderer();
        
        header.setDefaultRenderer(new HeaderCellRenderer());
		c.setBorder(new MetalBorders.TableHeaderBorder());
	}
    
    
    static class HeaderCellRenderer extends DefaultTableCellRenderer {

        Border normalBorder;

        public HeaderCellRenderer() {
            super();
            
            Border bevel = BorderFactory.createEtchedBorder();// .createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(115, 114, 105),new Color(165, 163, 151));
            normalBorder = BorderFactory.createCompoundBorder(bevel, BorderFactory.createEmptyBorder(0, 3, 0, 0));
            
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (table != null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                }
            }

            setText((value == null) ? "" : value.toString());
            setHorizontalTextPosition(LEFT);
            setBorder(normalBorder);
            
            return this;
        }
    }
}
