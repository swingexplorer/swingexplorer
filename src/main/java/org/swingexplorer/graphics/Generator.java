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
package org.swingexplorer.graphics;

import static java.lang.System.out;

import java.awt.Graphics2D;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * 
 * @author Maxim Zakharenkov
 */
public class Generator {

	public static void main(String[] args) {
		
		
		for(Method meth : Graphics2D.class.getMethods()) {
			
			if((Modifier.ABSTRACT & meth.getModifiers()) == Modifier.ABSTRACT) {
				dump(meth);
			}
		}		
	}

	static void dump(Method meth) {
		String template =
				"@Override\n" +
				"public %1$s %2$s(%3$s) { \n " +
				"operation(\"%2$s\" %4$s); \n " +
				"}";
	 
		String returnType = meth.getReturnType().getName();
		String methodName = meth.getName();
		
		String paramDecl = "";
		String args = "";
		int i = 0;
		for(Class<?> type: meth.getParameterTypes()) {
			String comma = i > 0 ? ", " : ""; 
			paramDecl = paramDecl + comma + type.getName() + " p" + i;
			args = args + ", " + type.getName() + ".class, p" + i;
			i ++;
		}
		
		
		out.printf(template, returnType, methodName, paramDecl, args);
	}
	
//	@Override
//	public void drawGlyphVector(GlyphVector g, float x, float y) {
//		operation("drawGlyphVector", GlyphVector.class, g, float.class, x, float.class, y);
//	}
}

