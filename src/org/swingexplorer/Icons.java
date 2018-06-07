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

import static org.swingexplorer.GuiUtils.getImageIcon;

import java.awt.Image;

import javax.swing.Icon;
/**
 *
 * @author  Maxim Zakharenkov
 */
public abstract class Icons {

    public static final String BASE_PATH = "resources/swingexplorer/";
    
    public static Icon warning() {
        return getImageIcon(BASE_PATH + "warning.png");
    }
    
    public static Icon codeLineBall() {
        return getImageIcon(BASE_PATH + "code_line_ball.png");
    }
    
    public static Icon collapsedHandler() {
        return getImageIcon(BASE_PATH + "collapsed_handler.png");
    }
    
    public static Icon expandedHandler() {
        return getImageIcon(BASE_PATH + "expanded_handler.png");
    }
    
     public static Icon[] monitor() {
         Icon[] icons = new Icon[16];
         for (int i = 0; i < icons.length; i ++ ) {
             icons[i] = getImageIcon(BASE_PATH + "monitor" + (i + 1) + ".png");
         }
        return icons;
    }
     
     public static Image appSmallImage() {
    	 return GuiUtils.getImage(BASE_PATH + "swex18x18.png");
     }
}
