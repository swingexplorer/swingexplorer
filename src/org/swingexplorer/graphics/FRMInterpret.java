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


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 
 * @author Maxim Zakharenkov
 */
public class FRMInterpret extends Frame {

	Button btnStep = new Button("Step");
	Button btnRun = new Button("Run");
	Component drawing = new Component() {};
	XGraphics xgraphics;
	
	ExecutorService execService;
	
	private FRMInterpret(XGraphics xg) {
		setLayout(new BorderLayout());
		Panel pnlButtons = new Panel(new GridLayout(2, 1));
		Panel pnlEast = new Panel(new BorderLayout());
		pnlEast.add(pnlButtons, BorderLayout.NORTH);
		pnlButtons.add(btnRun);
		pnlButtons.add(btnStep);	
		
		
		add(drawing, BorderLayout.CENTER);
		add(pnlEast, BorderLayout.EAST);
		
		xgraphics = xg;		
		
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callback.run();
			}
		});
		btnStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doStep();
			}			
		});
		
		execService = Executors.newSingleThreadExecutor();
		
		addWindowListener(new WindowAdapter(){


			public void windowClosing(WindowEvent e) {
				dispose();
				execService.shutdown();
			}
		});
		
	}
	
	
	StepCallbackImpl callback = new StepCallbackImpl();
	

	
	private void doStart() {
		Runnable runnable = new Runnable() {
			public void run() {	
				try {
					xgraphics.interpret(drawing.getGraphics(), xgraphics.getOperationCount(), callback);
				} catch(Throwable ex) {
					ex.printStackTrace();
				}
			}			
		};
		execService.submit(runnable);
	}
	
	void doStep() {
		synchronized(callback) {
			callback.notify();
		}
	}
	
	class StepCallbackImpl implements Callback {

		boolean isRunning = false;
		
		synchronized  void run() {
			isRunning = true;
			notify();
		}
		public synchronized void operationPerformed(Operation op, Graphics g) {
			Graphics drg = drawing.getGraphics().create();
			drg.setColor(g.getColor());
			drg.translate(0, 300);
			drg.fillRect(0, 0, 20, 20);
			if(op.method.getName().startsWith("setClip")) {
				System.out.println("Clip:" + op.arguments[0]);
			}
			if(op.method.getName().startsWith("drawLine")) {
				int x1 = (int)(Integer)op.arguments[0];
				int y1 = (int)(Integer)op.arguments[1];
				int x2 = (int)(Integer)op.arguments[2];
				int y2 = (int)(Integer)op.arguments[3];
				drg.drawLine(x1,y1,x2,y2);
				
				drg.setColor(new Color(100, 0, 0, 50));
				Rectangle clipRect = g.getClipBounds();
				drg.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
				System.out.println("x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2);
			}
			drg.dispose();
			if(isRunning) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	
	
	public static void open(XGraphics xg) {
		FRMInterpret frm = new FRMInterpret(xg);
		frm.setBounds(400, 400, 500, 500);
		frm.setVisible(true);
		frm.doStart();
	}
	
	public static void main(String[] args) {
		open(null);
	}
	
}

