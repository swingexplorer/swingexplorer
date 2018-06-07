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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class IconPanel extends JComponent {

	Icon icon;
	double scale = 1;
	Rectangle selection;
	
	public IconPanel(Icon ico) {
		icon = ico;
	}
	
	public IconPanel() {
	}
	
	public void setIcon(Icon icon) {
		this.icon = icon;
		revalidate();
		repaint();
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	public void setSelection(Rectangle rect) {
		selection = rect;
		repaint();
	}
	
	public Rectangle getSelection() {
		return selection;
	}
	
	
	
	public void setScale(double scaleP) {
		scale = scaleP;
		revalidate();
		repaint();
	}
	public double getScale() {
		return scale;
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		if(icon != null) {
			
			g2d.scale(getScale(), getScale());
			icon.paintIcon(this, g2d, 0, 0);
			
			if(selection != null) {
				g2d.setColor(Color.RED);
				g2d.drawRect(selection.x, selection.y, selection.width, selection.height);
			}
		} else {
			g2d.drawString("No icon set", 0, 20);
		}
	}

	public Dimension getPreferredSize() {
		if(icon == null) {
			return new Dimension(0, 0);
		}
		return new Dimension((int)Math.round(getScale()*icon.getIconWidth()), 
				(int)Math.round(getScale()*icon.getIconHeight()));
	}
}

