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

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalTreeUI;

import org.swingexplorer.Icons;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class CustomTreeUI extends MetalTreeUI {

    @Override
    public Icon getExpandedIcon() {
        return Icons.expandedHandler();
    }
    
    @Override
    public Icon getCollapsedIcon() {
        return Icons.collapsedHandler();
    }
    
    @Override
    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
    }
    
    @Override
    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
    }
    
}
