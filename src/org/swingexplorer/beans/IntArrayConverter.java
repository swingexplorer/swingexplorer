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

import java.text.ParseException;
import java.util.StringTokenizer;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class IntArrayConverter implements Converter<int[]>{

	public int[] fromString(String strValue) throws ParseException {

		StringTokenizer tokens = new StringTokenizer(strValue, ",");
		int count = tokens.countTokens();
		
		int[] result = new int[count];
		int i = 0;;
		try {
			while(tokens.hasMoreElements()) {
				String strNumber = tokens.nextToken();
				
				result[i] = Integer.parseInt(strNumber);
				i ++;
			}
		
			return result;
		} catch(NumberFormatException ex) {
			throw new ParseException("Error parsing \"" + strValue + "\" to array or ints", 0);
		}
	}

	public String toString(int[] value) {
		if(value.length == 0) {
			return "";
		}
		
		StringBuilder buf = new StringBuilder();
		for(int v : value) {
			buf.append(v + ",");
		}
		
		// remove last ,
		buf.delete(buf.length() - 1, buf.length());
		
		return buf.toString();
	}

}


