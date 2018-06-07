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
package org.swingexplorer.additiontrace;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.swingexplorer.idesupport.IDESupport;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class ActOpenSourceCode implements HyperlinkListener {
	
	IDESupport ideSupport;
	private PNLAdditionTrace owner;
	
	ActOpenSourceCode(PNLAdditionTrace _owner) {
		owner = _owner;
	}
	
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			
			String[] params = e.getDescription().split(":");
			ideSupport.requestCheckedOpenSourceCode(params[0], Integer.parseInt(params[1]), owner);
		}
	}
}
