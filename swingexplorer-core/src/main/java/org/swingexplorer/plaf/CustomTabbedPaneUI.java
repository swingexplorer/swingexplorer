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
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.plaf.metal.MetalTabbedPaneUI;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class CustomTabbedPaneUI extends MetalTabbedPaneUI {

	boolean tabsOverlapBorder = false;
	private boolean contentOpaque = true;
	private Color selectedColor = new Color(200, 221, 242);
	private Color unselectedColor = new Color(0xdadada);

	public CustomTabbedPaneUI() {
	}

	@Override
	protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
			int selectedIndex, int x, int y, int w, int h) {
	}

	protected void paintContentBorder(Graphics g, int tabPlacement,
			int selectedIndex) {
		int width = tabPane.getWidth();
		int height = tabPane.getHeight();
		Insets insets = tabPane.getInsets();
		Insets tabAreaInsets = getTabAreaInsets(tabPlacement);

		int x = insets.left;
		int y = insets.top;
		int w = width - insets.right - insets.left;
		int h = height - insets.top - insets.bottom;

		y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
		if (tabsOverlapBorder) {
			y -= tabAreaInsets.bottom;
		}
		h -= (y - insets.top);

		if (tabPane.getTabCount() > 0 && (contentOpaque || tabPane.isOpaque())) {
			// Fill region behind content area
			Color color = unselectedColor;
			if (color != null) {
				g.setColor(color);
			} else if (selectedColor == null || selectedIndex == -1) {
				g.setColor(tabPane.getBackground());
			} else {
				g.setColor(selectedColor);
			}
		}
		paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
	}

	protected void paintTabBackground(Graphics g, int tabPlacement,
			int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		int slantWidth = h / 2;
		if (isSelected) {
			g.setColor(selectedColor);
		} else {
			g.setColor(unselectedColor);
		}

		if (tabPane.getComponentOrientation().isLeftToRight()) {
			switch (tabPlacement) {
			case LEFT:
				g.fillRect(x + 5, y + 1, w - 5, h - 1);
				g.fillRect(x + 2, y + 4, 3, h - 4);
				break;
			case BOTTOM:
				g.fillRect(x + 2, y, w - 2, h - 4);
				g.fillRect(x + 5, y + (h - 1) - 3, w - 5, 3);
				break;
			case RIGHT:
				g.fillRect(x, y + 2, w - 4, h - 2);
				g.fillRect(x + (w - 1) - 3, y + 5, 3, h - 5);
				break;
			case TOP:
			default:
				g.fillRect(x + 4, y + 2, (w - 1) - 3, (h - 1) - 1);
				g.fillRect(x + 2, y + 5, 2, h - 5);
			}
		} else {
			switch (tabPlacement) {
			case LEFT:
				g.fillRect(x + 5, y + 1, w - 5, h - 1);
				g.fillRect(x + 2, y + 4, 3, h - 4);
				break;
			case BOTTOM:
				g.fillRect(x, y, w - 5, h - 1);
				g.fillRect(x + (w - 1) - 4, y, 4, h - 5);
				g.fillRect(x + (w - 1) - 4, y + (h - 1) - 4, 2, 2);
				break;
			case RIGHT:
				g.fillRect(x + 1, y + 1, w - 5, h - 1);
				g.fillRect(x + (w - 1) - 3, y + 5, 3, h - 5);
				break;
			case TOP:
			default:
				g.fillRect(x, y + 2, (w - 1) - 3, (h - 1) - 1);
				g.fillRect(x + (w - 1) - 3, y + 5, 3, h - 3);
			}
		}
	}

	@Override
	protected Insets getContentBorderInsets(int tabPlacement) {
		return new Insets(3, 1, 0, 0);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		tabPane.setFont(PlafUtils.CUSTOM_FONT);
	}
}

