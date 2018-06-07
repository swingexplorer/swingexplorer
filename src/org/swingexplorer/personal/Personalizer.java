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

import javax.swing.JComponent;

import org.swingexplorer.Options;

/**
 * Class responsible for saving/restoring state of a component
 * between sessions.
 * @author  Maxim Zakharenkov
 */
public interface Personalizer {

	/**
	 * Installs personalizer for a component.
	 * Personalizer may add some listeners to
	 * the component which perform necessary configuration
	 * on event because it is often only possible to
	 * set component's sizes after a specific event occures 
	 * (e.g. component becomes visible or resized first) 
	 * @param options
	 * @param component
	 */
	public void install(Options options, Component component);
	
	/**
	 * The method is called by application before it closes
	 * down. This method saves state of a personalized component into
	 * the Options object provided to personalizer in the {@link #install(Options, JComponent)}
	 * method.
	 */
	public void saveState();
}
