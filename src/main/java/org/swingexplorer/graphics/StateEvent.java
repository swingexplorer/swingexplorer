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


import java.util.EventObject;

import org.swingexplorer.graphics.Player.PState;


/**
 * 
 * @author Maxim Zakharenkov
 */
public class StateEvent extends EventObject {

	private PState newState;
	private PState oldState;
	
	public StateEvent(Player source, PState oldStateP, PState newStateP) {
		super(source);
		newState = newStateP;
		oldState = oldStateP;
	}

	public Player getPlayer() {
		return (Player)source;
	}
	
	
	public PState getNewState() {
		return newState;
	}
	
	public PState getOldState() {
		return oldState;
	}
}

