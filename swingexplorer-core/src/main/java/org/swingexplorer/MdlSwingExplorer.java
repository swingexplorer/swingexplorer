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


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicComboPopup;

import org.swingexplorer.edt_monitor.EDTDebugQueue;
import org.swingexplorer.graphics.XGraphics;




/**
 * 
 * @author Maxim Zakharenkov
 */
public class MdlSwingExplorer {

	private Options options;
	
	private double displayScale = 1;
	
	private Component displayedComponent;
	
	private ImageIcon displayedComponentImage;
	
	private Component currentComponent;

	
	private ArrayList<Component> selectedComponents = new ArrayList<Component>();

	private Point measurePoint1;

	private Point measurePoint2;

	private EventListenerList listenerList = new EventListenerList();

	private String statustext;
        
    private Point mouseLocation;
	    
    private XGraphics xgraphics;
	
    
	private void log(String msg, String...params) {
//		System.err.printf("[MdlSwingExplorer]" + msg, params);
	}
	

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listenerList.add(PropertyChangeListener.class, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listenerList.remove(PropertyChangeListener.class, l);
	}


	/**
	 * Fires only when old and new value are not equal
	 * @param prop
	 * @param oldVal
	 * @param newVal
	 */
	protected void fireCheckedPropertyChange(String prop, Object oldVal, Object newVal) {
		if(oldVal != null && newVal != null && oldVal.equals(newVal)) {
			return;
		}
		if(oldVal == newVal) {
			return; // this is both null case
		}
		
		firePropertyChange(prop, oldVal, newVal);
	}
	protected void firePropertyChange(String prop, Object oldVal, Object newVal) {
		log("Property %1$s changed\n", prop);
		
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				// Lazily create the event:
				
				PropertyChangeEvent	propertyChangeEvent = new PropertyChangeEvent(this, prop,
							oldVal, newVal);
				((PropertyChangeListener) listeners[i + 1])
						.propertyChange(propertyChangeEvent);
			}
		}
	}

	public Component getCurrentComponent() {
		return currentComponent;
	}

	public void setCurrentComponent(Component currentComponent) {
		Object old = this.currentComponent;
		this.currentComponent = currentComponent;
		fireCheckedPropertyChange("currentComponent", old, currentComponent);
	}

	public Point getMeasurePoint1() {
		return measurePoint1;
	}

	public void setMeasurePoint1(Point _measurePoint1) {
		Object old = this.measurePoint1;
		if(_measurePoint1 != null) {
			this.measurePoint1 = (Point)_measurePoint1.clone();
		} else {
			this.measurePoint1 = null;
		}
		fireCheckedPropertyChange("measurePoint1", old, _measurePoint1);
	}

	public Point getMeasurePoint2() {
		return measurePoint2;
	}

	public void setMeasurePoint2(Point _measurePoint2) {		
		Object old = this.measurePoint2;
		if(_measurePoint2 != null) {
			this.measurePoint2 = (Point)_measurePoint2.clone();
		} else {
			this.measurePoint2 = null;
		}
		fireCheckedPropertyChange("measurePoint2", old, _measurePoint2);
	}
	
	public void addMeasurePoint(Point _measurePoint) {
		if(measurePoint1 == null) {
			setMeasurePoint1(_measurePoint);
			return;
		} 
		if(measurePoint2 != null) {
			setMeasurePoint1(null);
			setMeasurePoint2(null);
			return;
		} 
		setMeasurePoint2(_measurePoint);
	}

	public Component[] getSelectedComponents() {
		return selectedComponents.toArray(new Component[selectedComponents.size()]);
	}

	public void addSelection(Component selection) {
		if(selectedComponents.contains(selection)) {
			return;
		}
		
		Object old = this.selectedComponents.clone();
		this.selectedComponents.add(0, selection);
		firePropertyChange("selectedComponents", old, this.selectedComponents);
	}
	
	/**
	 * Clears current selection and sets one component
	 * selection. Equivalent to clearSelection and addSelection 
	 * but fires only one change event.
	 * @param comp component to select
	 */
	public void setSelection(Component comp) {

		if(selectedComponents.size() == 1 && selectedComponents.get(0) == comp) {
			// this one component is already selected
			return;
		}
		
		Object old = this.selectedComponents.clone();
		this.selectedComponents.clear();
		if(comp != null) {
			this.selectedComponents.add(comp);
		}
		firePropertyChange("selectedComponents", old, this.selectedComponents);
	}
	
	public void removeSelection(Component selection) {
		Object old = this.selectedComponents.clone();
		this.selectedComponents.remove(selection);
		firePropertyChange("selectedComponents", old, this.selectedComponents);
	}
	
	public void clearSelection() {
		Object old = this.selectedComponents.clone();
		this.selectedComponents.clear();
		firePropertyChange("selectedComponents", old, this.selectedComponents);
	}

	public Component getDisplayedComponent() {
		return displayedComponent;
	}

	
	public void setDisplayedComponent(Component displayedComponent) throws DisplayableException {

        // switching off EDT hanging events because  
	    // component's rendering operations takes quite long time in the EDT
        // Unfortunately this operation must be in the EDT
        // the flag will be set to false back after dispatching of the current
        // event will be finished
        EDTDebugQueue.disableHangEvents = true;
        
        
        // BasicComboPopup need to be shown/hidden in updateDisplayedComponentImage to update size
        if(!(displayedComponent instanceof BasicComboPopup) &&
           !(displayedComponent instanceof JToolTip) &&
                        (displayedComponent.getWidth() <= 0 || displayedComponent.getHeight() <= 0)) {
            throw new DisplayableException("Component with zero width or height can not be displayed");
        }
		Object oldVal = this.displayedComponent;
		this.displayedComponent = displayedComponent;
		fireCheckedPropertyChange("displayedComponent", oldVal, displayedComponent);
		
		if(oldVal != null && displayedComponent != null && oldVal.equals(displayedComponent)) {
			return;
		}
		if(oldVal == displayedComponent) {
			return; // this is both null case
		} 
        
        
		updateDisplayedComponentImage();
		clearSelection();
	}

	/**
	 * Makes sues that component image is updated even if
	 * same component is passed as parameter
	 * @param newDisplayedComponent
	 */
	public void setDisplayedComponentAndUpdateImage(Component newDisplayedComponent) throws DisplayableException {
		if(displayedComponent == newDisplayedComponent) {
			updateDisplayedComponentImage();
		} else {
			setDisplayedComponent(newDisplayedComponent);
		}
	}

	public double getDisplayScale() {
		return displayScale;
	}

	public void setDisplayScale(double displayScale) {
		double old = this.displayScale;
		this.displayScale = displayScale;
		fireCheckedPropertyChange("displayScale", old, displayScale);
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {		
		Options old = this.options;
		this.options = options;
		fireCheckedPropertyChange("options", old, options);
	}
	
	public ImageIcon getDisplayedComponentImage() {
		return displayedComponentImage;
	}
	
	public void setDisplayedComponentImage(ImageIcon img) {
		ImageIcon oldvalue = displayedComponentImage;
		displayedComponentImage = img;
		fireCheckedPropertyChange("displayedComponentImage", oldvalue, displayedComponentImage);
	}
	
	public void updateDisplayedComponentImage() {

		
		if(displayedComponent != null) {
			
            // basic popup and tooltip  must be shown before painting
            if(displayedComponent instanceof BasicComboPopup || displayedComponent instanceof JToolTip) {
                ((JComponent)displayedComponent).show();
            }
            if(displayedComponent instanceof JToolTip) {
                displayedComponent.setSize(displayedComponent.getPreferredSize());
            }
            
			BufferedImage img = new BufferedImage(displayedComponent.getWidth(), 
												  displayedComponent.getHeight(), 
											  BufferedImage.TYPE_INT_RGB);
			Graphics g = img.getGraphics();
            
			Color darkColor = options.getGridDarkColor();
			Color brightColor = options.getGridBrightColor();
            GuiUtils.paintGrid(g, img.getWidth(), img.getHeight(), darkColor, brightColor);
            
            
            RepaintManager.currentManager(displayedComponent).setDoubleBufferingEnabled(false);
			displayedComponent.paint(g);
            RepaintManager.currentManager(displayedComponent).setDoubleBufferingEnabled(true);
            
			g.dispose();				
			setDisplayedComponentImage(new ImageIcon(img));
            
            
			//  basic popup and tooltip must be shown before painting
            if(displayedComponent instanceof BasicComboPopup || displayedComponent instanceof JToolTip) {
                ((JComponent)displayedComponent).hide();
            }
		}
	}

	public String getComponentPath(Component displayedComponent, boolean showIndex) {
		
		StringBuilder buf = new StringBuilder();
		Component comp = displayedComponent;
		int index = -1;
		
		// constriucting path to top component
		while(comp != null) {
			buf.insert(0, "/");
			
			if(showIndex) {
				if(index != -1 && showIndex) {
					buf.insert(0, ']');
					buf.insert(0, index);
					buf.insert(0, '[');
				}
				//	memorize next index
				index = getComponentIndex(comp);
			}
			buf.insert(0, comp.getClass().getSimpleName());
			
			comp = comp.getParent();
		}
		if(buf.length() > 0) {
			buf.deleteCharAt(buf.length() - 1); // delete last slash
		}
		return buf.toString();
	}
	
	/**
	 * Returns component's index inside it's parent
	 * @param comp
	 * @param parent
	 * @return
	 */
	private int getComponentIndex(Component comp) {
		Container parent = (Container)comp.getParent();
		if(parent == null || comp == null) {
			return -1;
		}
		for(int i = 0; i< parent.getComponentCount(); i++) {
			if(parent.getComponent(i) == comp) {
				return i;
			}
		}
		return -1;
	}

	public String getStatustext() {
		return statustext;
	}

	public void setStatustext(String statustext) {
		String old = this.statustext;
		this.statustext = statustext;
		fireCheckedPropertyChange("statusText", old, this.statustext);
	}

	public boolean isSelected(Component comp) {
		return selectedComponents.contains(comp);
	}
        
    public void setMouseLocation(Point p) {
		Point old = this.mouseLocation;
		if(p != null) {
			this.mouseLocation = (Point)p.clone();
		} else {
			this.mouseLocation = null;
		}
		fireCheckedPropertyChange("mouseLocation", old, this.statustext);
	}
  
    public Point getMouseLocation() {
    	if(mouseLocation != null) {
    		return (Point)mouseLocation.clone();
    	} else {
    		return null;
    	}
    }
    
    public Point getMouseLocationAbsolute() {
    	Point loc = getMouseLocation();
    	if(loc == null) {
    		return null;
    	}
    	SwingUtilities.convertPointToScreen(loc, getDisplayedComponent());
    	return loc;
    }
        
}

