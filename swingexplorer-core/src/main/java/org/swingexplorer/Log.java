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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class Log {
	
	private String category;
	
    public static final int LOG_NOTHING = 0;
    public static final int LOG_ERROR = 1;
    public static final int LOG_WARNING = 2;
    public static final int LOG_INFO = 3;
    public static final int LOG_DEBUG = 4;
    
    private static PrintStream logStream;
    
    private String lastParsedLogDef = null;
	private int currentLevel = LOG_ERROR;
	
	
	public static Log ideSupport = new Log("ideSupport");
	public static Log general = new Log("general");
	public static Log instrumentation = new Log("instrumentation");
	
	
	
	private Log(String _category) {
		category = _category;
	}
    
    public void info(String info) {
    	writeLog(LOG_INFO, info, null);
    }
    
	public void warn(String warning) {
		writeLog(LOG_WARNING, warning, null);
	}
	
	public void error(String error) {
		writeLog(LOG_ERROR, error, null);
	}
	
	public void error(Throwable ex) {
		writeLog(LOG_ERROR, null, ex);
	}
	
	public void error(String error, Throwable ex) {
		writeLog(LOG_ERROR, error, ex);
	}
	
	public void debug(String message) {
		writeLog(LOG_DEBUG, message, null);
	}
	
	public void debug(String message, Throwable ex) {
		writeLog(LOG_DEBUG, message, ex);
	}
	
    private boolean isDoLog(int level) {
        String strLog = SysUtils.getLogOptions();
        if(strLog != null) {

        	// if this string was parsed once we consider that log level is not changed
        	if(lastParsedLogDef != strLog) { 
        		
        		// parsing log string. E.g. debug,category1=info,category2=error
        		int defaultLevel = LOG_ERROR; 
        		currentLevel = -1;
        		
		        // check 
		        StringTokenizer tokens = new StringTokenizer(strLog, ",");
		        while(tokens.hasMoreElements()) {
		            String token = tokens.nextToken();
		            
		            int newLevel = getLevelByName(token);
		            if(newLevel != -1 && newLevel > defaultLevel) {
		            	defaultLevel = newLevel;
		            }
		            
		            if(token.startsWith(category)) {
		            	if(token.charAt(category.length()) == '=') { 
			            	String levelName = token.substring(category.length() + 1);
			            	newLevel = getLevelByName(levelName);
			            	if(newLevel != -1 && newLevel > currentLevel) {
			            		currentLevel = newLevel;
			            	}
		            	}
		            }
		        }
		        if(currentLevel == -1) {
		        	currentLevel = defaultLevel;
		        }
		        lastParsedLogDef = strLog;
		        
		        // output info about logging initialization
		        logStream = getLogStream(true);
		        logStream.println("[log init] " + "Logging " + category + " set to " + getLevelName(currentLevel));
        	}
        }
        return currentLevel >= level && level != -1;
    }
    
    private static int getLevelByName(String levelName) {
    	int logLevel = -1;
    	for(int i = LOG_NOTHING; i <= LOG_DEBUG; i++) {
    		if(levelName.equalsIgnoreCase(getLevelName(i))) {
    			return i;
    		}
    	}
    	return logLevel;
    }
    
    private static String getLevelName(int level) {
    	switch(level) {
    		case LOG_NOTHING: return "nothing";
    		case LOG_ERROR: return "error";
    		case LOG_WARNING: return "warning";
    		case LOG_INFO: return "info";
    		case LOG_DEBUG: return "debug";
    	}
    	return null;
    }
	
	private void writeLog(int level, String record, Throwable t) {
		try {
			if(isDoLog(level)) {
				PrintStream logStream = getLogStream(true);
				if(record != null) {
					logStream.println("[" + getLevelName(level) + "][" + category + "] " + record);
				} else {
					logStream.print("[" + getLevelName(level) + "][" + category + "] ");
				}
				if(t != null) {
					t.printStackTrace(logStream);
				}
			}
		} catch(Throwable ex) {
			// catch all exceptions in case if there is
			// a mistake in the logging code. logs are not written
			// in that case
		}
	}
	
	public PrintStream getLogStream(boolean autoCreateDirectory) {
		if(logStream == null) {
			
			if(SysUtils.isLogToConsole()) {
				logStream = System.out;
				return logStream;
			}
			
			String dir = SysUtils.getHomeDirectory(autoCreateDirectory);
			try {
				logStream = new PrintStream(new FileOutputStream(dir + "/swex.log"));
				return logStream;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logStream;
	}
}

