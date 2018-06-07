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

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.swingexplorer.instrument.Problem;
import org.swingexplorer.instrument.ProblemListener;

/**
 *
 * @author Elliott Hughes <enh@jessies.org>
 * 
 * Advice, bug fixes, and test cases from
 * Alexander Potochkin and Oleg Sukhodolsky.
 * 
 * https://swinghelper.dev.java.net/
 * 
 * Adoption for Swing Explorer by Maxim Zakharenkov 
 * https://swingexplorer.dev.java.net/
 */
public class EDTDebugQueue extends EventQueue {
        
    
        // Time to wait between checks that the event dispatch thread isn't hung.
        private static final long CHECK_INTERVAL_MS = 100;
    
        // Help distinguish multiple hangs in the log, and match start and end too.
        // Only access this via getNewHangNumber.
        private static int hangCount = 0;
    
        // Prevents us complaining about hangs during start-up, which are probably
        // the JVM vendor's fault.
        private boolean haveShownSomeComponent = false;
    
        // The currently outstanding event dispatches. The implementation of
        // modal dialogs is a common cause for multiple outstanding dispatches.
        private final LinkedList<DispatchInfo> dispatches = new LinkedList<DispatchInfo>();
  
        
        static long minimalMonitoredHangTime = 1000;
        static EDTDebugQueue instance = new EDTDebugQueue();
        static ProblemListener problemListener;
        
        // this flag is used for temporary switching off
        // events about hangs. It is useful fow SwingExplorer
        // when it performs image capturing that can be quite long
        // and occures in the EDT
        // The flag is in force only on a single event and it always
        // reset to "false" after event is dispatched
        public static boolean disableHangEvents = false;
        
        
        public static boolean monitorHangs = false;
        public static boolean monitorExceptions = false;
        
        public static void setProblemListener(ProblemListener _problemListener) {
            problemListener = _problemListener;
        }
        
        
        public static EDTDebugQueue getInstance() {
            return instance;
        }

        public static void initMonitoring() {
            Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
        }

        public static long getMinimalMonitoredHangTime() {
            return minimalMonitoredHangTime;
        }

        public static void setMinimalMonitoredHangTime(long _minimalMonitoredHangTime) {
            minimalMonitoredHangTime = _minimalMonitoredHangTime;
        }
        
        
        private static class DispatchInfo {
            // The last-dumped hung stack trace for this dispatch.
            private StackTraceElement[] lastReportedStack;
            // If so; what was the identifying hang number?
            private int hangNumber;
    
            // The EDT for this dispatch (for the purpose of getting stack traces).
            // I don't know of any API for getting the event dispatch thread,
            // but we can assume that it's the current thread if we're in the
            // middle of dispatching an AWT event...
            // We can't cache this because the EDT can die and be replaced by a
            // new EDT if there's an uncaught exception.
            private final Thread eventDispatchThread = Thread.currentThread();
    
            // The last time in milliseconds at which we saw a dispatch on the above thread.
            private long lastDispatchTimeMillis = System.currentTimeMillis();
    
            public DispatchInfo() {
                // All initialization is done by the field initializers.
            }
    
            public void checkForHang() {
                if (timeSoFar() > getMinimalMonitoredHangTime()) {
                    examineHang();
                }
            }
    
            // We can't use StackTraceElement.equals because that insists on checking the filename and line number.
            // That would be version-specific.
            private static boolean stackTraceElementIs(StackTraceElement e, String className, String methodName, boolean isNative) {
                return e.getClassName().equals(className) && e.getMethodName().equals(methodName) && e.isNativeMethod() == isNative;
            }
    
            // Checks whether the given stack looks like it's waiting for another event.
            // This relies on JDK implementation details.
            private boolean isWaitingForNextEvent(StackTraceElement[] currentStack) {
                return stackTraceElementIs(currentStack[0], "java.lang.Object", "wait", true) && stackTraceElementIs(currentStack[1], "java.lang.Object", "wait", false) && stackTraceElementIs(currentStack[2], "java.awt.EventQueue", "getNextEvent", false);
            }
    
            private void examineHang() {
                StackTraceElement[] currentStack = eventDispatchThread.getStackTrace();
    
                if (isWaitingForNextEvent(currentStack)) {
                    // Don't be fooled by a modal dialog if it's waiting for its next event.
                    // As long as the modal dialog's event pump doesn't get stuck, it's okay for the outer pump to be suspended.
                    return;
                }
    
                if (stacksEqual(lastReportedStack, currentStack)) {
                    // Don't keep reporting the same hang every time the timer goes off.
                    return;
                }
    
                hangNumber = getNewHangNumber();
                lastReportedStack = currentStack;
                
                String description = MessageFormat.format("EDT hang for {0}ms", timeSoFar());
                if(monitorHangs) {
                    fireProblem(new Problem(description, currentStack));
                }
            }
    
            private static boolean stacksEqual(StackTraceElement[] a, StackTraceElement[] b) {
                if (a == null) {
                    return false;
                }
                if (a.length != b.length) {
                    return false;
                }
                for (int i = 0; i < a.length; ++i) {
                    if (a[i].equals(b[i]) == false) {
                        return false;
                    }
                }
                return true;
            }
    
            /**
             * Returns how long this dispatch has been going on (in milliseconds).
             */
            private long timeSoFar() {
                return (System.currentTimeMillis() - lastDispatchTimeMillis);
            }
    
            public void dispose() {
                
                // reset 
                if(disableHangEvents) {
                    disableHangEvents = false;
                    return;
                }
                
                if (lastReportedStack != null) {
                    String description = MessageFormat.format("EDT hang end after {0}ms", timeSoFar());
                    if(monitorHangs) {
                        fireProblem(new Problem(description, lastReportedStack));
                    }
                }
            }
            
        }

        private static void fireProblem(Problem problem) {
            if(problemListener != null && !disableHangEvents) {
                problemListener.problemOccured(problem);
            }
        }
    
        private EDTDebugQueue() {
            initTimer();
        }
    
        /**
         * Sets up a timer to check for hangs frequently.
         */
        private void initTimer() {
            final long initialDelayMs = 0;
            final boolean isDaemon = true;
            Timer timer = new Timer("EventDispatchThreadHangMonitor", isDaemon);
            timer.schedule(new HangChecker(), initialDelayMs, CHECK_INTERVAL_MS);
        }
    
        private class HangChecker extends TimerTask {
            @Override
            public void run() {
                synchronized (dispatches) {
                    if (dispatches.isEmpty() || !haveShownSomeComponent) {
                        // Nothing to do.
                        // We don't destroy the timer when there's nothing happening
                        // because it would mean a lot more work on every single AWT
                        // event that gets dispatched.
                        return;
                    }
                    // Only the most recent dispatch can be hung; nested dispatches
                    // by their nature cause the outer dispatch pump to be suspended.
                    dispatches.getLast().checkForHang();
                }
            }
        }
    
    
        /**
         * Overrides EventQueue.dispatchEvent to call our pre and post hooks either
         * side of the system's event dispatch code.
         */
        @Override
        protected void dispatchEvent(AWTEvent event) {
            try {
                preDispatchEvent();
                super.dispatchEvent(event);
            } catch(RuntimeException ex) {
                if(monitorExceptions) {
                    fireProblem(new Problem("Exception caught in the Event Dispatch thread: " + ex.getMessage(), ex.getStackTrace()));
                } else {
                    throw ex;
                }
            } finally {
                postDispatchEvent();
                if (!haveShownSomeComponent && 
                        event instanceof WindowEvent && event.getID() == WindowEvent.WINDOW_OPENED) {
                    haveShownSomeComponent = true;
                }
            }
        }
    
        @SuppressWarnings("unused")
		private void debug(String which) {
            if (false) {
                for (int i = dispatches.size(); i >= 0; --i) {
                    System.out.print(' ');
                }
                System.out.println(which);
            }
        }
    
        /**
         * Starts tracking a dispatch.
         */
        private synchronized void preDispatchEvent() {
            debug("pre");
            synchronized (dispatches) {
                dispatches.addLast(new DispatchInfo());
            }
        }
    
        /**
         * Stops tracking a dispatch.
         */
        private synchronized void postDispatchEvent() {
            synchronized (dispatches) {
                // We've finished the most nested dispatch, and don't need it any longer.
                DispatchInfo justFinishedDispatch = dispatches.removeLast();
                justFinishedDispatch.dispose();
    
                // The other dispatches, which have been waiting, need to be credited extra time.
                // We do this rather simplistically by pretending they've just been redispatched.
                Thread currentEventDispatchThread = Thread.currentThread();
                for (DispatchInfo dispatchInfo : dispatches) {
                    if (dispatchInfo.eventDispatchThread == currentEventDispatchThread) {
                        dispatchInfo.lastDispatchTimeMillis = System.currentTimeMillis();
                    }
                }
            }
            debug("post");
        }
    
    
        private static String stackTraceToString(StackTraceElement[] stackTrace) {
            StringBuilder result = new StringBuilder();
            // We used to avoid showing any code above where this class gets
            // involved in event dispatch, but that hides potentially useful
            // information when dealing with modal dialogs. Maybe we should
            // reinstate that, but search from the other end of the stack?
            for (StackTraceElement stackTraceElement : stackTrace) {
                String indentation = "    ";
                result.append("\n" + indentation + stackTraceElement);
            }
            return result.toString();
        }
    
        private synchronized static int getNewHangNumber() {
            return ++hangCount;
        }
}
