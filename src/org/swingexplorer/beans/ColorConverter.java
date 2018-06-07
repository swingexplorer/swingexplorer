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
package org.swingexplorer.beans;

import java.awt.Color;
import java.text.ParseException;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class ColorConverter implements Converter<Color> {

	public Color fromString(String strValue) throws ParseException {
		if(strValue == null) {
			return null;
		}
		
		String[] strRgb = strValue.split(",");
		
		if(strRgb.length < 3 || strRgb.length > 4) {
			throw new ParseException("Wrong color string \"" + strRgb + "\" Number of color components must be either 3 of 4 (r,g,b,alpha)",0);
		}
		try {
			
			strRgb[0] = strRgb[0].trim();
			strRgb[1] = strRgb[1].trim();
			strRgb[2] = strRgb[2].trim();
			
			int r = Integer.parseInt(strRgb[0]);
			int g = Integer.parseInt(strRgb[1]);
			int b = Integer.parseInt(strRgb[2]);
			
			int aplha = 255;
			if(strRgb.length == 4) {
				aplha = Integer.parseInt(strRgb[3]);
			}
			return new Color(r, g, b, aplha);
		} catch(NumberFormatException ex) {
			throw new ParseException("Invalid string format to convert \"" + strValue + "\" to Color", 0);
		}
	}

	public String toString(Color value) {
		if(value == null) {
			return null;
		}
		return value.getRed() + "," + value.getGreen() + "," + value.getBlue() + "," + value.getAlpha();
	}

}
