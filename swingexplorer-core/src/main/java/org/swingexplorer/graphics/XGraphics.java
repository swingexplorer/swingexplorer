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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.swingexplorer.Log;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class XGraphics extends Graphics2D {

	
	Graphics2D graphics;
	Callback callback;
	ArrayList<Operation> operations;
	ArrayList<Graphics> openGraphics;
	
	
	public XGraphics(Graphics2D graphics) {
		this(graphics, null);
	}
	
	public XGraphics(Graphics2D graphics, Callback callbackP) {
		this.graphics = graphics;
		operations = new ArrayList<Operation>();
		openGraphics = new ArrayList<Graphics>();
		openGraphics.add(graphics);
		callback = callbackP;
	}
	
	private XGraphics(Graphics2D graphics, ArrayList<Operation> operations, ArrayList<Graphics> openGraphics, Callback callbackP) {
		this.graphics = graphics;
		this.operations = operations;
		this.openGraphics = openGraphics;		
		callback = callbackP;
	}

	/** 
	 * Detaches all operations collected by this graphics instance
	 * and resets internal operation list to empty.
	 * Detached operation list contains END operation at the end.
	 */
	public ArrayList<Operation> detachOperations() {
		ArrayList<Operation> detachOps = operations;
		
		//	add END marker operation to operation list, 
		// payer needs it for showing selection
		operations.add(Operation.createEndOperation(detachOps.size()));
		
		// Clearing operation list and graphics objects
		operations = new ArrayList<Operation>();
		
		// dispose all Graphics because UI components may forget
		// to call dispose and we have owerriden "finalize"
		for(Graphics g : openGraphics) {
			g.dispose();
		}
		
		openGraphics.clear();
		openGraphics.add(graphics);		
		
		return detachOps;
	}

	public void finalize() {
		callback = null;
		
		// we can not call this finalize because
		// this will call "dispose" inside and the operation
		// will be recorded but operations from finalizer 
		// thread must not be recorded  
//		super.finalize();
	}
	

	@Override
	public java.awt.Graphics create() {
		Graphics2D g = (Graphics2D) operation("create");
		openGraphics.add(g); // add to graphics array
		return new XGraphics(g, operations, openGraphics, callback);
	}
	@Override
	public void dispose() {
		operation("dispose");
		openGraphics.remove(graphics);	// remove from graphics array
	}
	
	public void dump(PrintStream out) {
		for(Operation op:operations) {
			out.println(op.method + " " + op.graphicsIndex);
		}
		out.println("OpenGraphics: " + openGraphics.size());
		out.println("OpCount: " + operations.size());
	}
	
	Object operation(String methodName, Object...args) {
		
		//	obtain arguments and types
		Class<?>[] types = new Class[args.length/2];
		Object[] arguments = new Object[args.length/2];
		for(int i = 0,j = 0; i < args.length; i+=2,j++) {
			types[j] = (Class<?>)args[i];
			arguments[j] = args[i + 1];
		}
		
		// obtain method
		Method method;
		try {
			method = Graphics2D.class.getMethod(methodName, types);
		} catch (Exception e) {				
			throw new RuntimeException("Error obtaining method (" + methodName + ") from graphics", e);
		}
		
		// obtain stack trace and 
		StackTraceElement[] fullTrace = new Throwable().getStackTrace();
		
		// finding stack trace's finish (trace used inside swingexplorer)
		int traceLength = fullTrace.length - 2;
		for(int i = 2; i < fullTrace.length; i++) {			
			if(fullTrace[i].getClassName().contains("org.swingexplorer")) {
				traceLength = i - 2;
				break;
			}
		}
		
		//remove last 2 elements from it taking 2 calls from XGraphics
		StackTraceElement[] stackTrace = new StackTraceElement[traceLength];
		System.arraycopy(fullTrace, 2, stackTrace, 0, stackTrace.length);
		
		// memorize operation and index of Graphics was used		
		Operation op = new Operation(openGraphics.indexOf(graphics), operations.size(), stackTrace, method, arguments);
		
		if(!methodName.startsWith("get")) { // do not memorize getters
			operations.add(op); 
		}

		Object res = op.run(graphics);
		if(callback != null) {
			callback.operationPerformed(op, graphics);
		}
		return res;
	}
	
	public int getOperationCount() {
		return operations.size();
	}
	
	public void interpret(Graphics g, int toStep, Callback callback) {
		
		
		ArrayList<Graphics> openGraphics = new ArrayList<Graphics>();
		openGraphics.add(g);
		
		Operation op = null;
		for(int i = 0; i < toStep; i ++) {
			op = operations.get(i);
			
			Graphics2D use_g = (Graphics2D)openGraphics.get(op.graphicsIndex);
			Log.general.debug(op.graphicsIndex + " " + op.toString());
			Object res = op.run(use_g);
			if(callback != null) {
				callback.operationPerformed(op, use_g);
			}
			if(op.isCreate()) {
				openGraphics.add((Graphics)res);
				Log.general.debug("Created: " + (openGraphics.size() - 1));
			} else if(op.isDispose()) {
				openGraphics.remove(op.graphicsIndex);
				Log.general.debug("Deleted: " + op.graphicsIndex);
			}			
		}
	}
	
	
	@Override
	public void transform(java.awt.geom.AffineTransform p0) {
		operation("transform", java.awt.geom.AffineTransform.class, p0);
	}

	@Override
	public void fill(java.awt.Shape p0) {
		operation("fill", java.awt.Shape.class, forceClone(p0));
	}

	@Override
	public void rotate(double p0, double p1, double p2) {
		operation("rotate", double.class, p0, double.class, p1, double.class,
				p2);
	}

	@Override
	public void rotate(double p0) {
		operation("rotate", double.class, p0);
	}

	@Override
	public void scale(double p0, double p1) {
		operation("scale", double.class, p0, double.class, p1);
	}

	@Override
	public void addRenderingHints(java.util.Map p0) {
		operation("addRenderingHints", java.util.Map.class, p0);
	}

	@Override
	public void clip(java.awt.Shape p0) {
		operation("clip", java.awt.Shape.class, p0);
	}

	@Override
	public void draw(java.awt.Shape p0) {
		operation("draw", java.awt.Shape.class, p0);
	}

	@Override
	public void drawGlyphVector(java.awt.font.GlyphVector p0, float p1, float p2) {
		operation("drawGlyphVector", java.awt.font.GlyphVector.class, p0,
				float.class, p1, float.class, p2);
	}

	@Override
	public boolean drawImage(java.awt.Image p0,
			java.awt.geom.AffineTransform p1, java.awt.image.ImageObserver p2) {
		return (boolean) (Boolean) operation("drawImage", java.awt.Image.class,
				p0, java.awt.geom.AffineTransform.class, p1,
				java.awt.image.ImageObserver.class, p2);
	}

	@Override
	public void drawImage(java.awt.image.BufferedImage p0,
			java.awt.image.BufferedImageOp p1, int p2, int p3) {
		operation("drawImage", java.awt.image.BufferedImage.class, p0,
				java.awt.image.BufferedImageOp.class, p1, int.class, p2,
				int.class, p3);
	}

	@Override
	public void drawRenderableImage(
			java.awt.image.renderable.RenderableImage p0,
			java.awt.geom.AffineTransform p1) {
		operation("drawRenderableImage",
				java.awt.image.renderable.RenderableImage.class, p0,
				java.awt.geom.AffineTransform.class, p1);
	}

	@Override
	public void drawRenderedImage(java.awt.image.RenderedImage p0,
			java.awt.geom.AffineTransform p1) {
		operation("drawRenderedImage", java.awt.image.RenderedImage.class, p0,
				java.awt.geom.AffineTransform.class, p1);
	}

	@Override
	public void drawString(java.lang.String p0, float p1, float p2) {
		operation("drawString", java.lang.String.class, p0, float.class, p1,
				float.class, p2);
	}

	@Override
	public void drawString(java.text.AttributedCharacterIterator p0, int p1,
			int p2) {
		operation("drawString", java.text.AttributedCharacterIterator.class,
				p0, int.class, p1, int.class, p2);
	}

	@Override
	public void drawString(java.text.AttributedCharacterIterator p0, float p1,
			float p2) {
		operation("drawString", java.text.AttributedCharacterIterator.class,
				p0, float.class, p1, float.class, p2);
	}

	@Override
	public void drawString(java.lang.String p0, int p1, int p2) {
		operation("drawString", java.lang.String.class, p0, int.class, p1,
				int.class, p2);
	}

	@Override
	public java.awt.Color getBackground() {
		return (Color) operation("getBackground");
	}

	@Override
	public java.awt.Composite getComposite() {
		return (Composite) operation("getComposite");
	}

	@Override
	public java.awt.GraphicsConfiguration getDeviceConfiguration() {
		return (GraphicsConfiguration) operation("getDeviceConfiguration");
	}

	@Override
	public java.awt.font.FontRenderContext getFontRenderContext() {
		return (FontRenderContext) operation("getFontRenderContext");
	}

	@Override
	public java.awt.Paint getPaint() {
		return (Paint) operation("getPaint");
	}

	@Override
	public java.lang.Object getRenderingHint(java.awt.RenderingHints.Key p0) {
		return operation("getRenderingHint", java.awt.RenderingHints.Key.class,
				p0);
	}

	@Override
	public java.awt.RenderingHints getRenderingHints() {
		return (RenderingHints) operation("getRenderingHints");
	}

	@Override
	public java.awt.Stroke getStroke() {
		return (Stroke) operation("getStroke");
	}

	@Override
	public java.awt.geom.AffineTransform getTransform() {
		return (AffineTransform) operation("getTransform");
	}

	@Override
	public boolean hit(java.awt.Rectangle p0, java.awt.Shape p1, boolean p2) {
		return (boolean) (Boolean) operation("hit", java.awt.Rectangle.class,
				p0, java.awt.Shape.class, p1, boolean.class, p2);
	}

	@Override
	public void setBackground(java.awt.Color p0) {
		operation("setBackground", java.awt.Color.class, p0);
	}

	@Override
	public void setComposite(java.awt.Composite p0) {
		operation("setComposite", java.awt.Composite.class, forceClone(p0));
	}

	@Override
	public void setPaint(java.awt.Paint p0) {
		operation("setPaint", java.awt.Paint.class, p0);
	}

	@Override
	public void setRenderingHint(java.awt.RenderingHints.Key p0,
			java.lang.Object p1) {
		operation("setRenderingHint", java.awt.RenderingHints.Key.class, p0,
				java.lang.Object.class, p1);
	}

	@Override
	public void setRenderingHints(java.util.Map p0) {
		operation("setRenderingHints", java.util.Map.class, p0);
	}

	@Override
	public void setStroke(java.awt.Stroke p0) {
		operation("setStroke", java.awt.Stroke.class, forceClone(p0));
	}

	@Override
	public void setTransform(java.awt.geom.AffineTransform p0) {
		operation("setTransform", java.awt.geom.AffineTransform.class, p0);
	}

	@Override
	public void shear(double p0, double p1) {
		operation("shear", double.class, p0, double.class, p1);
	}

	@Override
	public void translate(int p0, int p1) {
		operation("translate", int.class, p0, int.class, p1);
	}

	@Override
	public void translate(double p0, double p1) {
		operation("translate", double.class, p0, double.class, p1);
	}

	
	
	@Override
	public boolean drawImage(java.awt.Image p0, int p1, int p2, int p3, int p4,
			int p5, int p6, int p7, int p8, java.awt.image.ImageObserver p9) {
		return (boolean) (Boolean) operation("drawImage", java.awt.Image.class,
				p0, int.class, p1, int.class, p2, int.class, p3, int.class, p4,
				int.class, p5, int.class, p6, int.class, p7, int.class, p8,
				java.awt.image.ImageObserver.class, p9);
	}

	@Override
	public boolean drawImage(java.awt.Image p0, int p1, int p2, int p3, int p4,
			int p5, int p6, int p7, int p8, java.awt.Color p9,
			java.awt.image.ImageObserver p10) {
		return (boolean) (Boolean) operation("drawImage", java.awt.Image.class,
				p0, int.class, p1, int.class, p2, int.class, p3, int.class, p4,
				int.class, p5, int.class, p6, int.class, p7, int.class, p8,
				java.awt.Color.class, p9, java.awt.image.ImageObserver.class,
				p10);
	}

	@Override
	public boolean drawImage(java.awt.Image p0, int p1, int p2,
			java.awt.Color p3, java.awt.image.ImageObserver p4) {
		return (boolean) (Boolean) operation("drawImage", java.awt.Image.class,
				p0, int.class, p1, int.class, p2, java.awt.Color.class, p3,
				java.awt.image.ImageObserver.class, p4);
	}

	@Override
	public boolean drawImage(java.awt.Image p0, int p1, int p2, int p3, int p4,
			java.awt.image.ImageObserver p5) {
		return (boolean) (Boolean) operation("drawImage", java.awt.Image.class,
				p0, int.class, p1, int.class, p2, int.class, p3, int.class, p4,
				java.awt.image.ImageObserver.class, p5);
	}

	@Override
	public boolean drawImage(java.awt.Image p0, int p1, int p2,
			java.awt.image.ImageObserver p3) {
		return (boolean) (Boolean) operation("drawImage", java.awt.Image.class,
				p0, int.class, p1, int.class, p2,
				java.awt.image.ImageObserver.class, p3);
	}

	@Override
	public boolean drawImage(java.awt.Image p0, int p1, int p2, int p3, int p4,
			java.awt.Color p5, java.awt.image.ImageObserver p6) {
		return (boolean) (Boolean) operation("drawImage", java.awt.Image.class,
				p0, int.class, p1, int.class, p2, int.class, p3, int.class, p4,
				java.awt.Color.class, p5, java.awt.image.ImageObserver.class,
				p6);
	}

	@Override
	public void fillRect(int p0, int p1, int p2, int p3) {
		operation("fillRect", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3);
	}

	@Override
	public java.awt.Color getColor() {
		return (Color) operation("getColor");
	}

	@Override
	public void setColor(java.awt.Color p0) {
		operation("setColor", java.awt.Color.class, p0);
	}

	@Override
	public void clearRect(int p0, int p1, int p2, int p3) {
		operation("clearRect", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3);
	}

	@Override
	public void clipRect(int p0, int p1, int p2, int p3) {
		operation("clipRect", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3);
	}

	@Override
	public void copyArea(int p0, int p1, int p2, int p3, int p4, int p5) {
		operation("copyArea", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3, int.class, p4, int.class, p5);
	}

	

	@Override
	public void drawArc(int p0, int p1, int p2, int p3, int p4, int p5) {
		operation("drawArc", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3, int.class, p4, int.class, p5);
	}

	@Override
	public void drawLine(int p0, int p1, int p2, int p3) {
		operation("drawLine", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3);
	}

	@Override
	public void drawOval(int p0, int p1, int p2, int p3) {
		operation("drawOval", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3);
	}

	@Override
	public void drawPolygon(int[] p0, int[] p1, int p2) {
		operation("drawPolygon", int[].class, p0, int[].class, p1, int.class,
				p2);
	}

	@Override
	public void drawPolyline(int[] p0, int[] p1, int p2) {
		operation("drawPolyline", int[].class, p0, int[].class, p1, int.class,
				p2);
	}

	@Override
	public void drawRoundRect(int p0, int p1, int p2, int p3, int p4, int p5) {
		operation("drawRoundRect", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3, int.class, p4, int.class, p5);
	}

	@Override
	public void fillArc(int p0, int p1, int p2, int p3, int p4, int p5) {
		operation("fillArc", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3, int.class, p4, int.class, p5);
	}

	@Override
	public void fillOval(int p0, int p1, int p2, int p3) {
		operation("fillOval", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3);
	}

	@Override
	public void fillPolygon(int[] p0, int[] p1, int p2) {
		operation("fillPolygon", int[].class, p0, int[].class, p1, int.class,
				p2);
	}

	@Override
	public void fillRoundRect(int p0, int p1, int p2, int p3, int p4, int p5) {
		operation("fillRoundRect", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3, int.class, p4, int.class, p5);
	}

	@Override
	public java.awt.Shape getClip() {
		return (Shape) operation("getClip");
	}

	@Override
	public java.awt.Rectangle getClipBounds() {
		return (Rectangle) operation("getClipBounds");
	}

	@Override
	public java.awt.Font getFont() {
		return (Font) operation("getFont");
	}

	@Override
	public java.awt.FontMetrics getFontMetrics(java.awt.Font p0) {
		return (FontMetrics) operation("getFontMetrics", java.awt.Font.class,
				p0);
	}

	// method clones object through relection
	// in some cases memorized argument values
	// should be cloned to avoud their modification later
	Object forceClone(Object toClone) {
		if(toClone == null) {
			return null;
		}
		Object cloned = null;
		try {
			Method cloneMeth = toClone.getClass().getMethod("clone", new Class[0]);
			cloned = cloneMeth.invoke(toClone, new Object[0]);
		} catch(Exception ex) {
			// can not clone object, 
			// this potentially may rise problems when playback
			Log.general.debug("Can not clone: " + toClone.getClass());
			return toClone;
		}
		return cloned;
	}
	
	@Override
	public void setClip(java.awt.Shape p0) {		
		operation("setClip", java.awt.Shape.class, forceClone(p0));
	}

	@Override
	public void setClip(int p0, int p1, int p2, int p3) {
		operation("setClip", int.class, p0, int.class, p1, int.class, p2,
				int.class, p3);
	}

	@Override
	public void setFont(java.awt.Font p0) {
		operation("setFont", java.awt.Font.class, p0);
	}

	@Override
	public void setPaintMode() {
		operation("setPaintMode");
	}

	@Override
	public void setXORMode(java.awt.Color p0) {
		operation("setXORMode", java.awt.Color.class, p0);
	}

		
	
}

