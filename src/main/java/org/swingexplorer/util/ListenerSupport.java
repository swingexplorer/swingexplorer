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
package org.swingexplorer.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class ListenerSupport<L> {

    ArrayList<L> listeners;
    Class<?> listenerInterface;
    L proxy;
    
    public ListenerSupport(Class<?> _listenerInterface) {
        listenerInterface = _listenerInterface;
    }
    
    public void addEventListener(L l) {
        if(listeners == null) {
            listeners = new ArrayList<L>();
        }
        synchronized(listeners) {
            listeners.add(l);
        }
    }
    
    public void removeEventListener(L l) {
        if(listeners == null) {
            return;
        }
        synchronized(listeners) {
            listeners.remove(l);
        }
    }
    
    @SuppressWarnings("unchecked")
    public L getDispatcher() {
        if(proxy == null) {
            proxy = (L)Proxy.newProxyInstance(this.getClass().getClassLoader(), 
                            new Class[]{listenerInterface}, 
                            new InvocationHandlerImpl());
        }
        return proxy;
    }
    
    class InvocationHandlerImpl implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            
            Object[] arrListeners;
            synchronized(listeners) {
                arrListeners = listeners.toArray();
            }
            for(Object listener :  arrListeners) {
                method.invoke(listener, args);
            }
            return null;
        }
    }
}
