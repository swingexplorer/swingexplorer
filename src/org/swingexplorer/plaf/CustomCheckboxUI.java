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

import javax.swing.AbstractButton;
import javax.swing.plaf.metal.MetalCheckBoxUI;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class CustomCheckboxUI extends MetalCheckBoxUI  /*BasicButtonUI*/ {

	
//	@Override
//	protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
//		
//		g.setColor(Color.BLACK);
//		AbstractButton b = (AbstractButton)c;
//		if(!b.isSelected()) {
//			g.drawRect(iconRect.x, iconRect.y, iconRect.width, iconRect.height);
//		} else {
//			g.fillRect(iconRect.x, iconRect.y, iconRect.width, iconRect.height);
//		}
//	}
	
    @Override
    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setFont(PlafUtils.CUSTOM_FONT);
    }
    
//    @Override
//    public synchronized void paint(Graphics g, JComponent c) {
//    	g.setColor(Color.BLACK);
//    	
//    	AbstractButton b = (AbstractButton) c;
//    	if(b.isSelected()) {
//    		g.fillRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
//    	} else {
//    		g.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
//    	}
//    }
//    
//    @Override
//    public Dimension getPreferredSize(JComponent c) {
//    	
//    	AbstractButton b = (AbstractButton) c;
//    	String text = b.getText();
//        Font font = b.getFont();
//        
//        FontMetrics fm = b.getFontMetrics(font);
//        int width = fm.stringWidth(text);
//        int height = fm.getHeight();
//    	
//    	return new Dimension(width, height);
//    }
//    
//    public void installUI(JComponent c) {
//    	c.addMouseListener(l)
//    }
}
