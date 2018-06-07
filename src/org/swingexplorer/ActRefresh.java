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


import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.accessibility.Accessible;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.swingexplorer.PNLComponentTree.TreeNodeObject;


/**
 * 
 * @author Maxim Zakharenkov
 */
public class ActRefresh extends RichAction {

	PNLComponentTree pnlComponentTree;
    
    // the fields used for detecting
    // changes in the component hierarchy
    Timer timer;
    HierarchyEvent lastEvent;
    Component lastAncestor;
    MdlSwingExplorer mdlSwingExplorer;
    
	public ActRefresh(PNLComponentTree _pnlComponentTree, MdlSwingExplorer _mdlSwingExplorer) {
		setName("Refresh");
		setTooltip("<html>Refresh tree and<br> displayed component</html>");
		setIcon("refresh.png");
        pnlComponentTree = _pnlComponentTree;
        mdlSwingExplorer = _mdlSwingExplorer;
        

        //timer for refresh (because sometimes we need to wait a bit when application is initialized)
        timer = new Timer(700, this);
        timer.setRepeats(false);
        
// Commented because it causes hangings when playing is performed
//
//          
//        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
//            public void eventDispatched(AWTEvent event) {
//                if(event instanceof HierarchyEvent) {
//                    
//                    // since there are too many very similar hierarchy events
//                    // and we can not do refresh on each of them we do some 
//                    // filtering of same events and perform refresh only 
//                    // after last event is arrived and no new events come in 
//                    // next 0.7 seconds 
//                    HierarchyEvent hEvent = (HierarchyEvent)event;
//                    if(lastEvent != null && lastEvent.getID() == event.getID()) {
//                        Component curAncestor = SwingUtilities.getWindowAncestor(hEvent.getChanged());
//                        if(lastAncestor == curAncestor || curAncestor == null) {
//                            // we consider this event as equivalet to previous
//                            return;
//                        }
//                        lastAncestor = curAncestor;
//                    }
//                    // start timer and wait if we should refresh
//                    lastEvent = hEvent;
//                    timer.restart();
//                }
//            }
//        }, 0xFFFFFFFFFFFFFFFL);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		Log.general.debug("Refresh component tree");
		
        refreshTreeModel();
        
        //  refresh displayed component
        mdlSwingExplorer.updateDisplayedComponentImage();
        
        
        // if component tree has no child components try to perform
        // refresh a bit later
        DefaultMutableTreeNode root = pnlComponentTree.getRoot();
        if(root.getChildCount() <= 0) {
        	timer.start();
        }
	}
    
    
    public void refreshTreeModel() {
        // reset last event and ancestor t mark
        // that refresh was performed
        lastEvent = null;
        lastAncestor = null;
        
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new TreeNodeObject(null, "root"));
        fillTreeModel(root);

        // when trees are equal - nothing to refresh
        if(!areTreesEqual(root, pnlComponentTree.getRoot())) {
            pnlComponentTree.setRoot(root);
        }
    }
	
    private static boolean areTreesEqual(DefaultMutableTreeNode root1, DefaultMutableTreeNode root2) {
        Enumeration<?> enum1 = root1.breadthFirstEnumeration();
        Enumeration<?> enum2 = root2.breadthFirstEnumeration();
        
        while(enum1.hasMoreElements()) {
            if(!enum2.hasMoreElements()) {
                return false;
            }
            DefaultMutableTreeNode node1 = (DefaultMutableTreeNode)enum1.nextElement();
            DefaultMutableTreeNode node2 = (DefaultMutableTreeNode)enum2.nextElement();
            if(!node1.getUserObject().equals(node2.getUserObject())) {
                return false;
            }
        }
        if(enum2.hasMoreElements()) {
            return false;
        }
        return true;
    }
    
    
	private void fillTreeModel(DefaultMutableTreeNode root) {
		root.removeAllChildren();
		
		// perform GC to remove closed disposed 
		// unreferenced windows/dialogs
		System.gc();
		
		Window[] allWindows;
		
		boolean forJDK15; 
		try {
			//	Try JRE 1.6 first through reflection
			Method meth = Window.class.getMethod("getWindows", new Class[0]);
			allWindows = (Window[])meth.invoke(Window.class, new Object[0]);
			forJDK15 = false;
		} catch (Exception e) {
			forJDK15 = true;
			// We got exception try for JRE 1.5
			Window[] frames = Frame.getFrames();
			
			// obtain list of ownerless dialogs
			Frame sharedOwner = getSharedOwnerFrame();
			Window[] ownerlessDialogs = sharedOwner == null ? new Window[0] : sharedOwner.getOwnedWindows();
			allWindows = new Window[frames.length + ownerlessDialogs.length];
			
			// merge dialogs and windows
			System.arraycopy(frames, 0, allWindows, 0, frames.length);
			System.arraycopy(ownerlessDialogs, 0, allWindows, frames.length, ownerlessDialogs.length);
			
			if(sharedOwner == null) {
				Log.general.warn("Dialog list is not in this Java configuration. Try to run it with JRE 1.6 or higher or with full security privilegies.");
			}
			// End JDK 1.5
		}
		
		// search for SwingExplorer's frame and exclude it
		// from resulting list. Also exclude windows to those
		// Swing Explorer is owner
		Window winExplorer = SwingUtilities.getWindowAncestor(pnlComponentTree);
		ArrayList<Window> list = new ArrayList<Window>();
		for(Window curWindow: allWindows) {
			if(!SysUtils.isShowExplorerWindow() && 
					(curWindow == winExplorer || curWindow.getOwner() == winExplorer)) {
				continue;
			}
			list.add(curWindow);
		}
		
		addChildren(root, list.toArray(new Window[list.size()]), forJDK15);		
	}
	
	String getWindowTitle(Window wnd) {
		String title = wnd.getClass().getSimpleName();
		if(wnd instanceof Frame) {
			title = title + "(" + ((Frame)wnd).getTitle() + ")";
		} else if(wnd instanceof Dialog) {
			title = title + "(" + ((Dialog)wnd).getTitle() + ")";
		}
		return title;
	}
	
	void addChildren(DefaultMutableTreeNode root, Window[] windows, boolean forJRE15) {
		for (int i = 0; i < windows.length; i++) {
			TreeNodeObject object = new TreeNodeObject(windows[i], getWindowTitle(windows[i]));
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(object);
			root.add(child);
			addChildren(child, windows[i]);
			
			if(forJRE15) {
				// for JRE 1.5 we need to get also owned dialogs since they are not
				// available from Frame.getFrames()
				Window[] ownedWnds = windows[i].getOwnedWindows();
				addChildren(root, ownedWnds, forJRE15);
			}
		}
		
	}
	
	void addChildren(DefaultMutableTreeNode node, Container cont) {
		for(int i = 0; i<cont.getComponentCount(); i++) {
			Component comp = cont.getComponent(i);
			
			// first try to get bean's name
			String name = comp.getName();
			if(name != null) {
				name = comp.getClass().getSimpleName() + "(" + name + ")";
			} else {
				// Next try to obtain description as "getText" if available
	            try {
	                Method meth = comp.getClass().getMethod("getText", new Class[0]);
	                String result = (String)meth.invoke(comp, new Object[0]);
	                name = comp.getClass().getSimpleName() + "(" + result + ")";
	            } catch (Exception e) {
	                name = comp.getClass().getSimpleName();
	            }
			}
			
			 // this is for the case of inner classes (they have empty simple name)
            if("".equals(name)) {
                name = comp.getClass().getName();
                
                int idx = name.lastIndexOf(".");
                if(idx != -1){
                    name = name.substring(idx + 1);
                }
            }
            
            // determine if it is glass pane
            if(cont instanceof JRootPane && ((JRootPane)cont).getGlassPane() == comp) {
                name = comp.getClass().getSimpleName() + "(glass pane)";
            }
            
            // determine if it is content pane
            if(cont instanceof JLayeredPane && 
               ((JLayeredPane)cont).getLayer(comp) == JLayeredPane.FRAME_CONTENT_LAYER ) {
                Object parent = cont.getParent();
                if(parent instanceof JRootPane && ((JRootPane)parent).getContentPane() == comp) {
                    name = comp.getClass().getSimpleName() + "(content pane)";
                }
            }
            
			TreeNodeObject object = new TreeNodeObject(comp, name);
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(object);
			node.add(child);
			
			if(comp instanceof Container) {
				addChildren(child, (Container)comp);
			}
            
            // obtain combo popup if it is available
            if(comp instanceof JComboBox) {
                Accessible accChild = ((JComboBox)comp).getAccessibleContext().getAccessibleChild(0);
                if(accChild != null) {
                    name = accChild.getClass().getSimpleName() + "(popup)";
                    TreeNodeObject comboObject = new TreeNodeObject((Component)accChild, name);
                    DefaultMutableTreeNode comboNode = new DefaultMutableTreeNode(comboObject);
                    child.add(comboNode);
                    
                    if(accChild instanceof Container) {
                        addChildren(comboNode, (Container)accChild);
                    }
                }
            }
            
            // adding tooltip of component
            if(comp instanceof JComponent) {
                JComponent jcomp = (JComponent)comp;
                String txt = jcomp.getToolTipText();
                if(txt != null) {
                    
                    JToolTip tooltip = (JToolTip)jcomp.getClientProperty("swex.tooltip");
                    if(tooltip == null) {
                        // create tooltip and  memorize in client property
                        tooltip = jcomp.createToolTip();
                        jcomp.putClientProperty("swex.tooltip", tooltip);
                    }
                    
                    tooltip.setTipText(txt);
                    TreeNodeObject tooltipObject = new TreeNodeObject(tooltip, "tooltip");
                    DefaultMutableTreeNode tooltipNode = new DefaultMutableTreeNode(tooltipObject);
                    child.add(tooltipNode);
                }
            }
		}
	}
    
	static Frame getSharedOwnerFrame() {
		try {
			Method meth = SwingUtilities.class.getDeclaredMethod("getSharedOwnerFrame", new Class[]{});
			meth.setAccessible(true);
			return (Frame)meth.invoke(null, new Object[]{});
		} catch (Exception e) {
			// null should be hadled upper
		}
		return null;
	}

}