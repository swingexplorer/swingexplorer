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

import java.awt.Graphics2D;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * Object holding a basic operation on Graphics object.
 * It points to method to call and parameter values to 
 * use during call. To perform operation the {@link #run(Graphics2D)} 
 * method has to be called. It executes the method through reflection.
 * @author Maxim Zakharenkov
 */
public class Operation {
	
	
	Method method;
	Object[] arguments;
	int graphicsIndex;
	int opIndex;
	boolean endOperation;
	StackTraceElement[] stackTrace;
	
	// constructor for a special purpose of creating END operation
	private Operation(int index) {
		this(-1, index, null, null, (Object[])null);
		endOperation = true;		
	}
	
	public Operation(int graphicsIndexP, int opIndexP, StackTraceElement[] stackTraceP, Method methP, Object...argsP) {
		method = methP;
		arguments = argsP;
		graphicsIndex = graphicsIndexP;
		opIndex = opIndexP;
		stackTrace = stackTraceP;
	}
	
	public boolean isEndOperation() {
		return endOperation;
	}
	
	/**
	 * Performs operation on given graphics instance.
	 * Just calls metod through reflection.
	 * @param graphics
	 * @return operation execurion result
	 */
	public Object run(Graphics2D graphics) {
		if(isEndOperation()) {
			throw new RuntimeException("END operation must not be executed, it serves only as marker");
		}
		
		//	invoke method
		try {
			return method.invoke(graphics, arguments);
		} catch (Exception e) {
			throw new RuntimeException("Error invoking method (" + method + ") from graphics", e);
		}
	}
	
	boolean isCreate() {
		return "create".equals(method.getName());
	}
	
	boolean isDispose() {
		return "dispose".equals(method.getName());
	}
	
	public String toString() {
		if(isEndOperation()) {
			return "END.";
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(method.getName() + "(");
		Class<?>[] params = method.getParameterTypes();
		for (int j = 0; j < params.length; j++) {
			sb.append(getTypeName(params[j]));
			sb.append(":");
			sb.append(arguments[j]);
			if (j < (params.length - 1))
				sb.append(",");
		}
		sb.append(")");
		return MessageFormat.format("{0,number,0000} : {1,number,00} {2}",
				opIndex, graphicsIndex, sb.toString());
	}

	// helper for toString
	String getTypeName(Class<?> type) {
		if (type.isArray()) {
			try {
				Class<?> cl = type;
				int dimensions = 0;
				while (cl.isArray()) {
					dimensions++;
					cl = cl.getComponentType();
				}
				StringBuffer sb = new StringBuffer();
				sb.append(cl.getName());
				for (int i = 0; i < dimensions; i++) {
					sb.append("[]");
				}
				return sb.toString();
			} catch (Throwable e) { /* FALLTHRU */
			}
		}
		return type.getName();
	}

	public int getIndex() {
		return opIndex;
	}

	public static Operation createEndOperation(int index) {
		return new Operation(index);
	}

	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}
}
