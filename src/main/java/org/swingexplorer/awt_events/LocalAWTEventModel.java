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
package org.swingexplorer.awt_events;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

/**
 * 
 * @author Maxim Zakharenkov
 *
 */
public class LocalAWTEventModel implements AWTEventModel {

	ArrayList<AWTEventListener> listeners = new ArrayList<AWTEventListener>();
	Dispatcher dispatcher = new Dispatcher();
	Component owner;
	Window rootContainer;
	boolean monitoring;
	PropertyChangeSupport changeSupport;
	Filter filter;
	
	public LocalAWTEventModel(Component _owner) {
		owner = _owner;
		filter = new Filter();
	}
	
	public void addEventListener(AWTEventListener listener) {
		if(listener == null) {
			throw new NullPointerException("Listener can not be null");
		}
		listeners.add(listener);
	}

	public void removeEventListener(AWTEventListener listener) {
		listeners.remove(listener);
	}

	public void setMonitoring(boolean _monitoring) {
		if(_monitoring == monitoring) {
			return;
		}
		if(_monitoring) {
			rootContainer = SwingUtilities.getWindowAncestor(owner);
			Toolkit.getDefaultToolkit().addAWTEventListener(dispatcher, 0xFFFFFFFFFFFFFFFL);
		} else {
			Toolkit.getDefaultToolkit().removeAWTEventListener(dispatcher);	
		}
		monitoring = _monitoring;
		firePropertyChanged("monitoring", !monitoring, monitoring);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
        	changeSupport = new PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport != null) {
        	changeSupport.removePropertyChangeListener(listener);
        }
    }
	
	protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
		if(changeSupport != null) {
			changeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}
	
	
	public boolean isMonitoring() {
		return monitoring;
	}
	
	private boolean isMatchFilter(AWTEvent event) {
		if(event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent)event;
			return 
			filter.mouseClicked && MouseEvent.MOUSE_CLICKED == mouseEvent.getID()||
			filter.mouseDragged && MouseEvent.MOUSE_DRAGGED == mouseEvent.getID()||
			filter.mouseEntered && MouseEvent.MOUSE_ENTERED == mouseEvent.getID()||
			filter.mouseExited && MouseEvent.MOUSE_EXITED == mouseEvent.getID()||
			filter.mouseMoved && MouseEvent.MOUSE_MOVED == mouseEvent.getID()||
			filter.mousePressed && MouseEvent.MOUSE_PRESSED == mouseEvent.getID()||
			filter.mouseReleased && MouseEvent.MOUSE_RELEASED == mouseEvent.getID()||
			filter.mouseWeel && MouseEvent.MOUSE_WHEEL == mouseEvent.getID();
		}
		if(event instanceof KeyEvent) {
			return 
			filter.keyPressed && KeyEvent.KEY_PRESSED == event.getID() ||
			filter.keyReleased && KeyEvent.KEY_RELEASED == event.getID() ||
			filter.keyTyped && KeyEvent.KEY_TYPED == event.getID();
		}
		
		return false;
	}
	
	
	class Dispatcher implements AWTEventListener {

		public void eventDispatched(AWTEvent event) {
			if(!isMatchFilter(event)) {
				return;
			}
			// ignore events of Swing explorer
			if(SwingUtilities.isDescendingFrom((Component)event.getSource(), rootContainer)) {
				return;
			}
			
			for(AWTEventListener l : listeners) {
				l.eventDispatched(event);
			}
		}
	}

	public void setFilter(Filter _filter) {
		Filter old = filter;
		filter = _filter;	
		changeSupport.firePropertyChange("filter", old, filter);
	}
	
	public Filter getFilter() {
		return filter;
	}
}

