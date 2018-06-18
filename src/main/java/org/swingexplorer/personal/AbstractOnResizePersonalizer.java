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
package org.swingexplorer.personal;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.swingexplorer.Options;

/**
 * Helper class to make personalizer implementation easier.
 * It calls applyState method when component is resized first time.
 * Subclasses must implement this method.
 * @author  Maxim Zakharenkov
 */
public abstract class AbstractOnResizePersonalizer<T extends Component> implements Personalizer {

	protected Options options;
	protected T component;
	
	public void install(Options _options, Component _component) {
		component = (T)_component;
		options = _options;
		component.addComponentListener(new ResizeListener());
	}
	
	class ResizeListener extends ComponentAdapter{
    	int count = 0;
    	public void componentResized(ComponentEvent e) {
    		applyState();
    		component.removeComponentListener(this);
		}
    }
	
	/**
	 * Applies state from positions to component.
	 * The method is called when component is first resized
	 */
	public abstract void applyState();
	
	/**
	 * {@inheritDoc}
	 */
	public abstract void saveState();
}


