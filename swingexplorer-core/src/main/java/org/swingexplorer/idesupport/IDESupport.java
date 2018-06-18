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
package org.swingexplorer.idesupport;

import java.awt.Component;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.JOptionPane;

import org.swingexplorer.Log;
import org.swingexplorer.SysUtils;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class IDESupport extends NotificationBroadcasterSupport implements IDESupportMBean {

    int notificationNumber = 0;
    
    // if bound is false then IDESupport is switched off
    // and IDESupport is not bound to RMI registry at all
    boolean bound;
    
    // this flag determines if plugin connected the MBean
    boolean connected;
    
    
    private IDESupport() {
    }
    
    public static IDESupport registerMBean() {
        int port = SysUtils.getManagementPort();
        IDESupport mBean = new IDESupport();
        
        if(port == -1) {
        	mBean.bound = false;
        	Log.ideSupport.info("No management port is specified. IDE support is off");
        	return mBean;
        }
        
        // create and register Mbean
        try {
            LocateRegistry.createRegistry(port);
            
            String strName = "org.swingexplorer:name=IDESupport";
            ObjectName name = new ObjectName(strName);
            
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(mBean, name);
            
            JMXServiceURL url = 
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/server"); 
            JMXConnectorServer cs = 
                JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs); 
            cs.start();
            mBean.bound = true;
            
            Log.ideSupport.info("IDE support initialized successfully");
        } catch (Exception e) {
            e.printStackTrace();
            Log.ideSupport.error("Can not initialize JMX service of SwingExplorer. Integration with IDE will not work.");
            mBean.bound = false;
        }
        return mBean;
    }
    
    public void requestOpenSourceCode(String className, int lineNo) throws IDENotConnectedException {
        if(!bound) {
        	throw new IDENotConnectedException();
        }
        
        if(!connected) {
        	throw new IDENotConnectedException();
        }
        
        Notification notification = new Notification("requestOpenSourceCode", this, notificationNumber++, System.currentTimeMillis());
        
        HashMap<String,Object> userData = new HashMap<String,Object>();
        userData.put("className", className);
        userData.put("lineNumber", lineNo);
        
        notification.setUserData(userData);
        sendNotification(notification);
    }

    public void requestCheckedOpenSourceCode(String className, int lineNo, Component owner) {
    	try {
        	requestOpenSourceCode(className, lineNo);
    	} catch(IDENotConnectedException ex) {
    		JOptionPane.showMessageDialog(owner, 
    				"Can not open source code. IDE connection is not available.\n" +
    				"The connection is available only when application is launched\n" +
    				"from IDE using Swing Explorer plug-in.");
    	}
    }
    

	public void connect() {
		connected = true;
	}


	public void disconnect() {
		connected = false;
	}
}
