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


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.RepaintManager;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import org.swingexplorer.GuiUtils;
import org.swingexplorer.Options;



/**
 *
 * @author Maxim Zakharenkov
 */
public class Player {
    
	Operation[] operations;
	int stepTime = 100;
	EventListenerList listenerList = new EventListenerList();
	
	PState state;
	Timer playTimer;
	
	
	BufferedImage workerImage;
	Graphics g;
	ArrayList<Graphics> openGraphics = new ArrayList<Graphics>();
	int currentOperationIndex = -1;
	
	Options options;
	
	
	public enum PState {
		NEW("New"), 		// created without operations
		IDLE("Idle"), 		// waiting for action
		PLAYING("Playing"); // playing
		
		PState(String nameP) {
			name = nameP;
		}
		String name;
		
		public String toString() {
			return name;
		}
	}
	
    /** Creates a new instance of Player */
    public Player() {
    	playTimer = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doStep();
				if(getCurrentOperation().isEndOperation()) {
					playTimer.stop();
				}
			}    		
    	});
    	playTimer.setRepeats(true);
    	state = PState.NEW;
    }

    public Operation getCurrentOperation() {
		if(currentOperationIndex == -1 || operations == null) {
			return null;
		}
		return operations[currentOperationIndex];
	}
    public boolean hasMoreOperations() {
		return operations.length > currentOperationIndex + 1;
	}
    public int getCurrentOperationIndex() {
		return currentOperationIndex;
	}
	private Operation nextOperation() {
		Operation op = getCurrentOperation();
		setCurrentOperationIndex(currentOperationIndex + 1);
		return op;
	}
	private void setCurrentOperationIndex(int index) {
		if(index == currentOperationIndex) {
			return;
		}
		Operation oldOp = getCurrentOperation();
		currentOperationIndex = index;
		fireCurrentOperationChangedEvent(getCurrentOperation(), oldOp);
	}
	
    private void setCurrentState(PState stateP) {
    	if(state == stateP) {
    		return;
    	}
    	PState oldState = state;
    	state = stateP;
    	fireStateChangeEvent(state, oldState);
    }
	
	public PState getCurrentState() {
		return state;
	}
    
    public void setStepTime(int stepTimeP) {
    	playTimer.setDelay(stepTimeP);
	}
	public int getStepTime() {
		return playTimer.getDelay();
	}
	
	public int getOperationCount() {
		return operations.length;
	}

	public void setOperations(Component component) {
		BufferedImage currentImage = new BufferedImage(component.getWidth(),
				component.getHeight(), BufferedImage.TYPE_INT_RGB);
		XGraphics currentGraphics = new XGraphics((Graphics2D) currentImage
				.getGraphics());

		// switching off double buffering to be able to capture all
		// rendering operations on the component
		RepaintManager.currentManager(component).setDoubleBufferingEnabled(
				false);
		
		// painting component to XGraphics and
		// recording graphical operations made during painting		
		component.paint(currentGraphics);
		
		// restore double buffering
		RepaintManager.currentManager(component)
				.setDoubleBufferingEnabled(true);
		
		// detach recorded operations
		ArrayList<Operation> newOperations = currentGraphics.detachOperations();
	
		currentGraphics.dispose();
		
		// set operations to player
		setOperations(newOperations.toArray(new Operation[newOperations.size()]), component.getSize());
		
		// current operation is END now
		setCurrentOperationIndex(this.operations.length - 1);
	}
	
	public void setOperations(Operation[] operationsP, Dimension imageSize) {				
		resetImage(imageSize);
		operations = operationsP;		
		setCurrentState(PState.IDLE);
		fireOperationsResetEvent(operationsP, imageSize);
	}
	
	public Operation[] getOperations() {
		return operations;
	}
	
	
	private void resetImage(Dimension size) {
		workerImage = new BufferedImage(size.width, 
				size.height, 
		BufferedImage.TYPE_INT_RGB);
		g = workerImage.getGraphics();
		setCurrentOperationIndex(0);

        openGraphics.clear();
		openGraphics.add(g);
	}
	
	private void resetImage() {
		resetImage(new Dimension(workerImage.getWidth(), 
				workerImage.getHeight()));
	}
	
    public void play() {
    	if(state == PState.IDLE) {
    		setCurrentState(PState.PLAYING);
    		if(getCurrentOperation().isEndOperation()) {
    			setCurrentOperationIndex(0);
    			resetImage();
    		}
    		playTimer.start();
    	}
    }
    
    public void pause() {
    	if(state == PState.PLAYING) {
    		playTimer.stop();
    		setCurrentState(PState.IDLE);    		
    	}
    }
    
    public void seek(int seekPosition) {    	
    	pause();		

    	
    	if(state != PState.IDLE) {
    		throw new IllegalStateException("Idle state must be here to perform seek operation");
    	}
    	
    	if(seekPosition < 0 || seekPosition >= operations.length) {
    		throw new IllegalArgumentException("Invalid seek position " + seekPosition + " must be in bounds [" + 0 + "-" + operations.length + ")");
    	}
    	
    	// if current operation index is before seek position
    	// then we can just perform additional operation till seekable
    	// position otherwise we need to reset image and repaint it from scratch    	
		int curOpIndex = 0;
		if (getCurrentOperationIndex() > seekPosition) {
			// reset current image without
			// resetting current operation's index
			// to not fire unneeded events
			workerImage = new BufferedImage(workerImage.getWidth(), workerImage
					.getHeight(), BufferedImage.TYPE_INT_RGB);
			g = workerImage.getGraphics();

			
			GuiUtils.paintGrid(g, workerImage.getWidth(), workerImage.getHeight(), options.getGridDarkColor(), options.getGridBrightColor());
            
			openGraphics.clear();
			openGraphics.add(g);
			
			// modifying current index directly to not fire events
			// current index is set to 0 to perform all operations before seek position
//			currentOperationIndex = 0;
		} else {
			curOpIndex = getCurrentOperationIndex();
		}
		
		// performing necessary operations till seekable position 
		Operation op = null;
		
		while (true) {
			op = operations[curOpIndex++];
			if (op.getIndex() == seekPosition || op.isEndOperation()) {				
				break;
			}
			doOperation(op);
		}
		
		// set current position and fire event about position change
		setCurrentOperationIndex(seekPosition);
		
		if (op != null) {
			fireImageRenderedEvent(workerImage, op);
		}
    }
    
    private void doOperation(Operation op) {
		// perform 1 operation
		Graphics2D use_g = (Graphics2D)openGraphics.get(op.graphicsIndex);
		log(op.graphicsIndex + " " + op.toString());
		Object res = op.run(use_g);
		if(op.isCreate()) {
			openGraphics.add((Graphics)res);
			log("Created: " + (openGraphics.size() - 1));
		} else if(op.isDispose()) {
			openGraphics.remove(op.graphicsIndex);
			log("Deleted: " + op.graphicsIndex);
		}
	}
    
	void log(String text) {
//		System.err.println(text);
	}

    
    public void playStep() {    	
		pause();
		if(hasMoreOperations()) {
			if (state != PState.IDLE) {
				throw new IllegalStateException(
						"Idle state must be here to perform playStep operation");
			}
			doStep();
		}
	}
    
    public void playStepBack() {
	    int seekPos = getCurrentOperationIndex() - 1;
	    if(seekPos >= 0) {
	    	seek(seekPos);
	    }
    }
    
    private void doStep() {    	
		Operation op = null;
		if (hasMoreOperations()) {
			op = nextOperation();
			doOperation(op);
			fireImageRenderedEvent(workerImage, op);
		}
		
		if(getCurrentOperation().isEndOperation()) {
			setCurrentState(PState.IDLE);		
		}
    }


    
    protected void fireCurrentOperationChangedEvent(
		final Operation curOperationP, final Operation oldOperationP) {
    	
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlayerListener.class) {
				CurrentOperationChangeEvent evt = new CurrentOperationChangeEvent(
						this, curOperationP, oldOperationP);
				((PlayerListener) listeners[i + 1])
						.currentOperationChanged(evt);
			}
		}
		log("Current operation changed: " + curOperationP);
	}
    
    protected void fireImageRenderedEvent(final BufferedImage image, final Operation op) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlayerListener.class) {
				ImageEvent imageEvent = new ImageEvent(this, image, op);
				((PlayerListener) listeners[i + 1]).imageRendered(imageEvent);
			}
		}
	}
    
    protected void fireOperationsResetEvent(final Operation[] operationsP, final Dimension imageSizeP) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlayerListener.class) {
				OperationResetEvent evt = new OperationResetEvent(Player.this,
						operationsP, imageSizeP);
				((PlayerListener) listeners[i + 1]).operationsReset(evt);
			}
		}
	 }
    
    protected void fireStateChangeEvent(final PState newState,
			final PState oldState) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlayerListener.class) {
				StateEvent stateEvent = new StateEvent(Player.this, oldState,
						newState);
				((PlayerListener) listeners[i + 1]).stateChanged(stateEvent);
			}
		}
	}
    
    public void addPlayerListener(PlayerListener l) {
	     listenerList.add(PlayerListener.class, l);
	 }

	 public void removePlayerListener(PlayerListener l) {
	     listenerList.remove(PlayerListener.class, l);
	 }

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}
}

