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
package org.swingexplorer.edt_monitor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.SwingUtilities;

import org.swingexplorer.instrument.Agent;
import org.swingexplorer.instrument.Problem;
import org.swingexplorer.instrument.ProblemListener;
import org.swingexplorer.util.ListenerSupport;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class MdlEDTMonitor {

    boolean monitorViolations;
    
    PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
    ProblemListenerRedispatcher problemListenerRedispatcher = new ProblemListenerRedispatcher();
    ListenerSupport<ProblemListener> listenerSupport = new ListenerSupport<ProblemListener>(ProblemListener.class);
    
    
    public MdlEDTMonitor() {
        EDTDebugQueue.setProblemListener(problemListenerRedispatcher);
    }
    
    public boolean isViolationMonitoringAvailable() {
        return Agent.isInstrumented();
    }
        
    public void setMonitorViolations(boolean _monitorViolations) {
        boolean old = monitorViolations;
        monitorViolations = _monitorViolations;
        if(monitorViolations) {
        	Agent.setMonitorEDTViolations(true);
            Agent.setProblemListener(problemListenerRedispatcher);
        } else {
            Agent.setProblemListener(null);
        }
        propChangeSupport.firePropertyChange("monitorViolations", old, monitorViolations);
    }

    public int getMinimalMonitoredHangTime() {
        return (int)EDTDebugQueue.getMinimalMonitoredHangTime();
    }

    public void setMinimalMonitoredHangTime(int _minimalMonitoredHangTime) {
        long old = EDTDebugQueue.getMinimalMonitoredHangTime();
        EDTDebugQueue.setMinimalMonitoredHangTime(_minimalMonitoredHangTime);
        propChangeSupport.firePropertyChange("minimalMonitoredHangTime", old, _minimalMonitoredHangTime);
    }
    
    public void setMonitorHangs(boolean _monitorHangs) {
        boolean old = EDTDebugQueue.monitorHangs;
        EDTDebugQueue.monitorHangs = _monitorHangs;
        propChangeSupport.firePropertyChange("monitorHangs", old, _monitorHangs);
    }
    
    public boolean isMonitorHangs() {
        return EDTDebugQueue.monitorHangs;
    }

    public boolean isMonitorViolations() {
        return monitorViolations;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propChangeSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propChangeSupport.removePropertyChangeListener(l);
    }
    
    public void addProblemListener(ProblemListener l) {
        listenerSupport.addEventListener(l);
    }
    
    public void removeProblemListener(ProblemListener l) {
        listenerSupport.removeEventListener(l);
    }
    
    
    class ProblemListenerRedispatcher implements ProblemListener {
        public void violationOccured(StackTraceElement[] trace, String threadName) {
            
        }

        public void problemOccured(final Problem problem) {
            // notify all listeners about the problem in the EDT
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    listenerSupport.getDispatcher().problemOccured(problem);
                }
            });
        }
    }

    public boolean isMonitorExceptions() {
        return EDTDebugQueue.monitorExceptions;
    }

    public void setMonitorExceptions(boolean _monitorExceptions) {
        boolean old = EDTDebugQueue.monitorExceptions;
        EDTDebugQueue.monitorExceptions = _monitorExceptions;
        propChangeSupport.firePropertyChange("monitorExceptions", old, _monitorExceptions);
    }
}
