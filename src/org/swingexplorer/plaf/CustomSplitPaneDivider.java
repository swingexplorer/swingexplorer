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
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class CustomSplitPaneDivider extends BasicSplitPaneDivider {

	public CustomSplitPaneDivider(BasicSplitPaneUI ui) {
		super(ui);		
	}
	
	public void setDividerSize(int newSize) {
        dividerSize = newSize;
    }
	
	@Override
	public void paint(Graphics g) {
		
		Dimension size = getSize();
		if(JSplitPane.HORIZONTAL_SPLIT == splitPane.getOrientation()) {
			int starty = size.height / 2 - 10;
			g.setColor(Color.DARK_GRAY);
			
			for(int y = starty; y < starty + 20; y +=3) {
				g.drawLine(0, y, dividerSize, y + dividerSize);
			}
			g.setColor(super.getBackground());
			g.drawLine(dividerSize/2, starty, dividerSize/2, starty + 20);
			
		} else {
			int startx = size.width / 2 - 10;
			g.setColor(Color.DARK_GRAY);
			
			for(int x = startx; x < startx + 20; x +=3) {
				g.drawLine(x, 0, x + dividerSize, dividerSize);
			}
			g.setColor(super.getBackground());
			g.drawLine(startx, dividerSize/2, startx + 20, dividerSize/2);
		}
	}
}

