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
import java.awt.Container;
import java.util.LinkedList;

import org.swingexplorer.GuiUtils;
import org.swingexplorer.Log;
import org.swingexplorer.Options;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class PersonalizerRegistry {

	Container parentContainer;
	Options options;
	
	LinkedList<Personalizer> personalizers = new LinkedList<Personalizer>(); 
	
	public PersonalizerRegistry(Container _parentContainer, Options _options) {
		parentContainer = _parentContainer;
		options = _options;
	}
	
	public void addPersonalizer(String beanName, Personalizer personalizer) {
		Component component = GuiUtils.findComponentByName(parentContainer, beanName);
		if(component == null) {
			Log.general.error("Can not find bean with name \""+ beanName + "\" to install personalizer");
			return;
		}
		personalizer.install(options, component);
		personalizers.add(personalizer);
		Log.general.debug("Personalizer installed for bean \"" + beanName + "\"");
	}
	
	public void saveState() {
		for(Personalizer curPersonalizer: personalizers) {
			curPersonalizer.saveState();
		}
	}
}


