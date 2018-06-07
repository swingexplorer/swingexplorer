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

import java.io.File;
import java.net.URI;


/**
 * Helper class to deal with system properties
 * and logging
 * @author  Maxim Zakharenkov
 */
public abstract class SysUtils {
	
	public static final String KEY_SHOW_EXPLORER_WINDOW = "swex.showwin";
    public static final String KEY_LOG_OPTIONS = "swex.log";
    public static final String KEY_LOG_CONSOLE = "swex.log.console";
    public static final String KEY_MONITOR_EDT_VIOLATIONS = "swex.vmonitor";
    public static final String KEY_MONITOR_EDT_HANGS = "swex.hmonitor";
    public static final String KEY_MONITOR_EDT_EXCEPTIONS = "swex.emonitor";
    public static final String KEY_MANAGEMENT_PORT = "swex.mport";

	
	/**
	 * Show explorer window in the tree or not
	 * @return
	 */
	public static boolean isShowExplorerWindow() {
		return getBoolean(KEY_SHOW_EXPLORER_WINDOW);
	}
    
    /** 
     * Checks if EDT violations should be monitored immediately
     * after program is started and resets the flag fo false.
     */ 
    public static boolean isMonitorEDTViolationsAndReset() {
        boolean res = getBoolean(KEY_MONITOR_EDT_VIOLATIONS);
        System.getProperties().remove(KEY_MONITOR_EDT_VIOLATIONS);
        return res;
    }
    
    public static boolean isMonitorEDTViolations() {
        return getBoolean(KEY_MONITOR_EDT_VIOLATIONS);
    }

    /** 
     * Checks if EDT hangs should be monitored immediately
     * after program is started and resets the flag fo false.
     */
    public static boolean isMonitorEDTHangsAndReset() {
        boolean res = getBoolean(KEY_MONITOR_EDT_HANGS);
        System.getProperties().remove(KEY_MONITOR_EDT_HANGS);
        return res;
    }

    public static boolean isMonitorEDTExceptionsAndReset() {
        boolean res = getBoolean(KEY_MONITOR_EDT_EXCEPTIONS);
        System.getProperties().remove(KEY_MONITOR_EDT_EXCEPTIONS);
        return res;
    }
    
    public static String getApplicationVersion() {
        return SysUtils.class.getPackage().getImplementationVersion();
    }
    
    /**
     * JMX port Swing Explorer should listen to be
     * managed from outside.
     * @return port number or -1 if management should be 
     * disabled 
     */
    public static int getManagementPort() {
        return getInt(KEY_MANAGEMENT_PORT);
    }
    
	private static boolean getBoolean(String key) {
		try {
			return Boolean.getBoolean(key);
		} catch(SecurityException ex) {
			return false;
		}
	}
    
    private static int getInt(String key) {
        try {
            return Integer.getInteger(key);
        } catch(Exception ex) {
            return -1;
        }
    }
	
	public static String getJavaVersion() {
		return System.getProperty("java.specification.version");
	}
	
	public static boolean isJava6() {
		return getJavaVersion().startsWith("1.6");
	}

	/**
	 * Opens given URI in the browser
	 * @param strUri
	 * @return true if operation succeded and false if not
	 */
	public static boolean openBrowser(String strUri) {
		if(!SysUtils.isJava6()) {
			return false;
		}
        	
        if( !java.awt.Desktop.isDesktopSupported() ) {
            return false;
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
        	return false;
        }

		try {
			URI uri = new URI( strUri);
			desktop.browse( uri );
			return true;
		} catch (Exception e1) {
			return false;
		}
	}

    /**
	 * Returns version-specific directory where Swing Explorer
	 * stores own files.
	 * @param autoCreate - indecates if directory should be created if it does
     * not exist
	 * @return full path
	 */
	public static String getHomeDirectory(boolean autoCreate) {

		// the exactly same implementation of this method is
		// also presented in the both in the SysUtils class 
		// and in the NB plug-in's Installer class 
		// making any changes here please also copy them
		// into both places


		// obtain version, if version not found - we are in development mode
		String version = SysUtils.class.getPackage().getImplementationVersion();
        if(version == null) {
            version = "0.0.0";// development version
        }

        String strDir =  System.getProperty("user.home") + "/.swex/" + version;

        // auto-create if necessary
		if(autoCreate) {
			File dir = new File(strDir);
			if(!dir.exists()) {
				dir.mkdirs();
			}
		}
		return strDir;
	}
	
	public static String getOptionFilePath(boolean autoCreateDirectory) {
		String dir = getHomeDirectory(autoCreateDirectory);
		return dir + "/options.properties";
	}
	
	
	/**
	 * Determines if logging options (levels, categories).
	 * @return
	 */
	public static String getLogOptions() {
		return System.getProperty(KEY_LOG_OPTIONS);
	}
	
	/**
	 * Determines if logging to console should be done instead of file.
	 * @return
	 */
	public static boolean isLogToConsole() {
		return Boolean.getBoolean(KEY_LOG_CONSOLE);
	}
}

