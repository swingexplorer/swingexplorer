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
package org.swingexplorer.instrument;

import java.awt.Component;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Vector;
import java.util.WeakHashMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;


/**
 *
 * @author  Maxim Zakharenkov
 */
public class Agent {

    static ClassPool pool;
    
    // marks already visited methods in one thread. Used for thread violation checking
    // to not report same violation multiple times when other methods are called from
    // violating method
    static java.util.Hashtable<Thread, Integer> threadMarks = new Hashtable<Thread, Integer>();

    static ProblemListener problemListener;
    static boolean instrumented = false;
    
    // problems are stored in this vector before listener is added to the agent
    static Vector<Problem> futureProblems = new Vector<Problem>();

    // keeps all stack traces o
    static WeakHashMap<Component, StackTraceElement[]> componentAddImplStackTraces = new WeakHashMap<Component, StackTraceElement[]>();
    
    // 
    static boolean monitorEDTViolations;

    public static boolean isMonitorEDTViolations() {
		return monitorEDTViolations;
	}

	public static void setMonitorEDTViolations(boolean monitorEDTViolations) {
		Agent.monitorEDTViolations = monitorEDTViolations;
	}
    
    public static void premain(String agentArguments, Instrumentation instrumentation) {
        instrumentation.addTransformer(new Transformer());
        pool = ClassPool.getDefault();
        pool.importPackage("org.swingexplorer.instrument");
       // Log.instrumentation.info("Instrumentation agent of Swing Explorer activated");
        instrumented = true;
    }

    public static boolean isInstrumented() {
        return instrumented;
    }
    
    public static void setProblemListener(ProblemListener _violationHandler) {
        problemListener = _violationHandler;
        if(problemListener == null) {
            return;
        }
        
        // notifying listener about all previousely accured problems
        for(Problem problem : futureProblems) {
            problemListener.problemOccured(problem);
        }
        futureProblems.clear();
    }
    
    private static void notifyProblemListener() {
        // obtain stack information
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        final StackTraceElement[] newTrace = new StackTraceElement[trace.length - 3];
        System.arraycopy(trace, 3, newTrace,  0, newTrace.length);
        final String threadName = Thread.currentThread().getName();
        
        // notify
        String description = MessageFormat.format("The {1}.{2} method called from {0} thread", threadName, newTrace[0].getClassName(), newTrace[0].getMethodName());

        Problem problem = new Problem(description, newTrace);
        if(problemListener != null) {        
            problemListener.problemOccured(problem);
        } else {
            if(monitorEDTViolations) {
                futureProblems.add(problem);
            }
        }
    }
    
    /** performs checking if we are in the EDT */
    public static void checkEDT() {
        if(isNotEventDispatchThread()) {
            // if we are not in EDT then memorise this thread
            // so that eliminate subsequent call checkings  
            Integer entries = (Integer)threadMarks.get(Thread.currentThread());
               if(entries == null) {
                   entries = new Integer(1);
                   notifyProblemListener();
               }
             threadMarks.put(Thread.currentThread(), new Integer(entries.intValue() + 1));
        }
    }
    
    private static boolean isNotEventDispatchThread() {
        // just simple !javax.swing.SwingUtilities.isEventDispatchThread()
        // is ot enough because in case we change event queue
        // the event thread is changed and the single remaining sign
        // is the thread name started by AWT-EventQueue
        
        String name = Thread.currentThread().getName();
        if(name != null && name.startsWith("AWT-EventQueue")) {
            return false;
        } else {
            return true;
        }
    }
    
    public static void threadSafeCheckEDT() {
        if(!javax.swing.SwingUtilities.isEventDispatchThread()) {
            // if we are not in EDT then memorise this thread
            // so that eliminate subsequent call checkings  
            Integer entries = (Integer)threadMarks.get(Thread.currentThread());
               if(entries == null) {
                   entries = new Integer(1);
                   // in difference with checkEDT() we don't
                   // notify listener because method is considered thread safe
                   // and all internal calls to non thread safe methods are legal
               }
             threadMarks.put(Thread.currentThread(), new Integer(entries.intValue() + 1));
        }
    }
    
    
    /** Removes thread mark for this call */
    public static void finalizeCheckEDT() {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            Integer entries = (Integer) threadMarks.get(Thread.currentThread());
            if (entries != null) {
                entries = new Integer(entries.intValue() - 1);
                if (entries.intValue() == 1) {
                    threadMarks.remove(Thread.currentThread());
                } else {
                    threadMarks.put(Thread.currentThread(), entries);
                }
            }
        }
    }
    
    public static void processContainer_addImpl(Component component) {
    	StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    	
    	// remove 2 last elements from stack trace
    	StackTraceElement[] newStackTrace = new StackTraceElement[stackTrace.length - 2];
    	System.arraycopy(stackTrace, 2, newStackTrace, 0, newStackTrace.length); 
    	
    	componentAddImplStackTraces.put(component, newStackTrace);
    }
    
    public static StackTraceElement[] getAddImplStackTrace(Component component) {
    	return componentAddImplStackTraces.get(component);
    }
    
    static class Transformer implements ClassFileTransformer {
        
        boolean first = true;
     
        public byte[] transform(ClassLoader loader, 
                        String className, 
                        Class<?> redefiningClass, 
                        ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
            
            className = className.replace("/", ".");
            
            // instrumenting java.awt.Container.addImpl
            try {
            	if(className.equals("java.awt.Container")) {
            		CtClass ctClass = pool.get(className);
            		CtMethod m = ctClass.getDeclaredMethod("addImpl");
            		m.insertBefore("{org.swingexplorer.instrument.Agent.processContainer_addImpl($1);}");
            		return ctClass.toBytecode();
            	}
            } catch(Exception ex) {
            	error("Error instrumenting class: " + className);
                if(first) {
                	error(ex);
                    first = false;
                }
            }

            
            
            // exclude javassist classes non-swing classes
            if(!className.startsWith("javax.swing") ||className.startsWith("javassist")) {
                return bytes;
            }
            
            try {
                CtClass ctClass = pool.get(className);
                
                if(extendsJComponent(ctClass)) {
                    
                    // insert EDT check into constructor
                    CtConstructor[] ctors = ctClass.getDeclaredConstructors();
                    for(CtConstructor constr : ctors) {
                        try {
                            constr.insertBefore("{org.swingexplorer.instrument.Agent.checkEDT();}");
                        } catch(Exception ex) {
                            if(first) {
                            	error(ex);
                                first = false;
                            }
                            error("Error instrumenting constructor: " + ctClass.getName() + " " + constr.getName() + constr.getSignature());
                        }
                    }
                    
                    // insert EDT check into non-thread safe methods
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    for(CtMethod m : methods) {
                        try {
                            if(isThreadSafeMethod(m)) {
                                m.insertBefore("{org.swingexplorer.instrument.Agent.threadSafeCheckEDT();}");
                            } else {
                                m.insertBefore("{org.swingexplorer.instrument.Agent.checkEDT();}");
                            }
                            m.insertAfter("{org.swingexplorer.instrument.Agent.finalizeCheckEDT();}", true);
                        } catch(Exception ex) {
                        	error("Error instrumenting method: " + ctClass.getName() + " " + m.getName() + m.getSignature());
                            if(first) {
                            	error(ex);
                                first = false;
                            }
                        }
                    }
                    debug("Instrumented: " + ctClass.getName());
                    return ctClass.toBytecode();
                } else {
                	debug("NOT instrumented: " + ctClass.getName());
                }
            } catch (Exception e) {
            	error("Error instrumenting class: " + className);
                if(first) {
                	error(e);
                    first = false;
                }
            }
            return bytes;
        }
    }

    // determines if class is JComponent or derived
    static boolean extendsJComponent(CtClass ctClass) throws CannotTransformException {
        if(ctClass == null) {
            return false;
        }
        if("javax.swing.JComponent".equals(ctClass.getName())) {
            return true;
        }
        try {
            return extendsJComponent(ctClass.getSuperclass());
        } catch (NotFoundException e) {
            throw new CannotTransformException(e);
        }
    }
    
    // determines if it is Swing's thread-safe method
    static boolean isThreadSafeMethod(CtMethod m) {
        String strMethod = m.getName() + m.getSignature();
        String name = m.getName();
        
        return
            strMethod.equals("repaint()V") ||
            strMethod.equals("repaint(JIIII)V") ||
            strMethod.equals("repaint(Ljava/awt/Rectangle;)V") ||
            strMethod.equals("repaint(IIII)V") ||
            strMethod.equals("revalidate()V") ||
            //strMethod.equals("invalidate()V") || /* removed by Alex's request */
            strMethod.equals("imageUpdate(Ljava/awt/Image;IIIII)Z") ||
            strMethod.equals("getListeners(Ljava/lang/Class;)[Ljava/util/EventListener;") ||
            name.startsWith("add") && name.endsWith("Listener") ||
            name.startsWith("remove") && name.endsWith("Listener");
    }
    
	static void debug(String string) {
//		System.out.println("[instrumentation][debug] " + string);
	}

	static void error(String string) {
//		System.out.println("[instrumentation][error] " + string);
	}

	static void error(Exception ex) {
//		System.out.print("[instrumentation][error] ");
//		ex.printStackTrace(System.out);
	}
}

