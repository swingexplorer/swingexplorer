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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.border.Border;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class RulerBorder implements Border{
	
	MdlSwingExplorer model;
	
	
	public Insets getBorderInsets(Component c) {
		return new Insets(20, 20, 0, 0);
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(c.getBackground());
		g.fillRect(0, 0, 20, 20);
		g.setColor(c.getBackground().darker());
		g.fillRect(0, 20, 20, width);
		g.fillRect(20, 0, width, 20);
		
		g.setColor(Color.BLACK);
		for(int i = 20; i < width; i += 10) {
			g.drawLine(i, 0, i, 10);
		}
		for(int i = 20; i < height; i += 10) {
			g.drawLine(0, i, 10, i);
		}
		
		Point p =  model.getMouseLocation();
		if(p != null) {
			g.drawLine(p.x, 0, p.x, 20);
			g.drawLine(0, p.y, 20, p.y);
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public MdlSwingExplorer getModel() {
		return model;
	}

	public void setModel(MdlSwingExplorer model) {
		this.model = model;
	}
}
