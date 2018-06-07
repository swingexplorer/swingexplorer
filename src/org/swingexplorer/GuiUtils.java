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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;



/**
 * Helper GUI utility methods
 * @author Maxim Zakharenkov
 */
final public class GuiUtils {
    
    /** Logger used by this class. */

    /**
     * Constant which may be used as background error color for any editor.
     * This constant define light red color.
     */
    public static final Color EDITOR_ERROR_COLOR = new Color(255, 150, 150);
    
    /**
     * Constant used for tool-tip text generation. Defines error
     * font color in HTML format. 
     */
    public static final String EDITOR_ERROR_COLOR_HTML = "#f30000";

    /**
     * Private constructor means class cannot be instantiated.
     */
    private GuiUtils() {
    }

    /**
     * Used to get instance of <i>Image</i> object using <i>ClassLoader</i> as
     * resource provider.
     * @param resourceName Name of the resource where image stored.
     * @return Instance of <i>Image</i> object or <code>null</code> if
     * resource is not found.
     */
    public static Image getImage(String resourceName) {
        return getImage(null, resourceName);
    }

    /**
     * Used to get instance of <i>Image</i> object. Specified class or
     * <i>ClassLoader</i> if class is null are used to get access to the
     * resource where image is stored.
     * @param _class Class used to get the image resource.
     * May be <code>null</code>.
     * @param resourceName Name of the resource where image is stored.
     * @return Instance of <i>Image</i> object or <code>null</code> if
     * resource is not found.
     */
    public static Image getImage(Class<?> _class, String resourceName) {
        URL resource;
        if (_class != null) {
            // Strange things happens here:
            // The following code must work in web start,
            // but is not working in local Java application.
            resource = _class.getClassLoader().getResource(resourceName);
            // that's why following code used:
            if (resource == null) {
                resource = _class.getResource(resourceName);
            }

        } else {
            resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        }
        if (resource != null) {
            return Toolkit.getDefaultToolkit().getImage(resource);
        } else {
            return null;
        }
    }

    /**
     * Used to get instance of <i>ImageIcon</i> object. Invoke
     * {@link #getImageIcon(Class, String)} method with <code>null</code> class.
     * @param resourceName Name of the resource where image stored.
     * @return Instance of <i>ImageIcon</i> object or <code>null</code> if
     * resource is not found.
     */
    public static ImageIcon getImageIcon(String resourceName) {
        return getImageIcon(null, resourceName);
    }

    /**
     * Used to get instance of <i>ImageIcon</i> object. Specified class or
     * <i>ClassLoader</i> if class is null are used to get access to the
     * resource where image is stored.
     * @param _class Class used to get the image resource.
     * May be <code>null</code>.
     * @param resourceName Name of the resource where image is stored.
     * @return Instance of <i>Image</i> object or <code>null</code> if
     * resource is not found.
     */
    public static ImageIcon getImageIcon(Class<?> _class, String resourceName) {
        Image image = getImage(_class, resourceName);
        if (image != null) {
            return new ImageIcon(image);
        } else {
            return null;
        }
    }

    /**
     * Used to get an icon with specified width and height. If the width or
     * height are less or equal with 0 icon will not be scaled.
     * @param resourceName Name of resource where icon is stored.
     * @param width Width for the returned icon.
     * @param height Height for the returned icon.
     * @return The created and scaled icon or <code>null</code> if resource
     * is not available.
     */
    public static ImageIcon getScaledIcon(String resourceName, int width, int height) {
        ImageIcon result = getImageIcon(resourceName);
        if (result != null) {
            if (width > 0 && height > 0) {
                Image artImage = result.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                result.setImage(artImage);
            }
        }
        return result;
    }

    /**
     * Used to get parent window for specified component.
     * @param component The component for which parent window should be found.
     * @return The window where specified component is placed or the component
     * itself if it is instance of <i>Window</i>.
     */
    public static Window getParentWindow(Component component) {
        Window result;
        if (component instanceof Window) {
            result = (Window) component;
        } else if (component != null) {
            result = SwingUtilities.getWindowAncestor(component);
        } else {
            throw new NullPointerException(
                MessageFormat.format("Cannot find parent window for component \"{0}\"", new Object[] { component }));
        }
        return result;
    }

    /**
     * Centers specified child component on the specified container.
     * @param parent Container used to center on, if <code>null</code>
     * then child is centered relatively to the whole screen.
     * @param child The component to be centered.
     */
    public static void center(Component parent, Component child) {
        if (parent == null) {
            center(child);
        } else {
            center(parent.getLocationOnScreen(), parent.getSize(), child, false);
        }
    }

    /**
     * Centers specified component on a screen.
     * @param component The component to be centered.
     */
    public static void center(Component component) {
        // size will be restricted against screen size anyway
        // that's why boolean argument is false.
        // anyway this argument should not be removed - it is simply reserved
        // for future if MDI restriction will be required.
        center(new Point(0, 0), null, component, true);

    }

    private static void center(Point parentLocation, Dimension parentSize, Component component, boolean restrictSize) {

        // this dimension is used to fit into screen after positioning performed
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (parentSize == null) {
            parentSize = screenSize;
        }

        Dimension childSize = component.getSize();

        // Disallows child GUI component have greater size than parent GUI
        // container have.
        // This method is reserved to restrict area when MDI component used.
        if (restrictSize) {
            if (childSize.height > parentSize.height) {
                childSize.height = parentSize.height;
            }
            if (childSize.width > parentSize.width) {
                childSize.width = parentSize.width;
            }
        }

        // check child size against screen size anyway:
        if (childSize.height > screenSize.height) {
            childSize.height = screenSize.height;
        }
        if (childSize.width > screenSize.width) {
            childSize.width = screenSize.width;
        }
        component.setSize(childSize.width, childSize.height);

        int childLocationX = (parentSize.width - childSize.width) / 2 + parentLocation.x;
        int childLocationY = (parentSize.height - childSize.height) / 2 + parentLocation.y;

        // check location and size to fit into screen:
        if (childSize.width + childLocationX > screenSize.width) {
            childLocationX = screenSize.width - childSize.width;
        }
        if (childSize.height + childLocationY > screenSize.height) {
            childLocationY = screenSize.height - childSize.height;
        }
        if (childLocationX < 0) {
            childLocationX = 0;
        }
        if (childLocationY < 0) {
            childLocationY = 0;
        }

        // Centers child GUI component on the parent GUI container.
        component.setLocation(childLocationX, childLocationY);
    }

    /**
     * Character <code>&amp;</code> used as prefix for a mnemonic symbol.
     */
    public static final char AMP = '&' /**/;

    private static final char NO_MNEMONIC = (char) 0;

    /**
     * Used to return string where single <code>&amp;</code> symbols are
     * extracted and double are replaced with single one.
     * @param text Text from where the symbols has to be extracted.
     * @return String where the symbols are extracted.
     */
    public static String getTextWithoutMnemonic(String text) {
        if (text == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        int idx = 0;
        while (idx < text.length()) {
            char c = text.charAt(idx++);
            if (c != AMP) {
                result.append(c);
            } else {
                if (idx < text.length()) {
                    result.append(text.charAt(idx++));
                } else {
                    result.append(AMP);
                }
            }
        }
        return result.toString();
    }

    /**
     * Used to get from a text a mnemonic defined by a single
     * <code>&amp;</code> symbol.
     * @param text Text where mnemonic may be defined.
     * @return Extracted mnemonic or character that is zero if
     * mnemonic is not specified.
     */
    public static char getMnemonicFromText(String text) {
        if (text == null) {
            return NO_MNEMONIC;

        } else if (text.length() < 1) {
            return NO_MNEMONIC;
        }

        int idx = text.indexOf(AMP);
        while (idx >= 0) {
            if (text.length() > (++idx)) {
                if (text.charAt(idx) != AMP) {
                    break;
                }
                idx = text.indexOf(AMP, idx + 1);
            } else {
                idx = -1;
            }
        }
        if (idx < 0) {
            return NO_MNEMONIC;
        } else {
            return text.charAt(idx);
        }
    }

    /**
     * Used to create popup menu with specified actions.
     * @param actions Array of actions used for the popup menu creation. If
     * array contains <i>null</i> element separator will be created in the
     * corresponding position.
     * @return New instance of popup menu.
     */
    public static JPopupMenu createPopupMenu(Action[] actions) {
        JPopupMenu result = new JPopupMenu();
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] != null) {

                JMenuItem item = new JMenuItem(actions[i]) {
                    public void setText(String text) {
                        super.setText(GuiUtils.getTextWithoutMnemonic(text));
                    }
                };
                result.add(item);

            } else {

                result.addSeparator();
            }
        }
        return result;
    }

    /**
     * Adds listener to window that ensures that
     * the window is not resized less then specified size.
     * In case if it is resized anyway it restores the
     * minimal size.
     *
     * @param wnd Window which size should be restricted.
     * @param minSize The minimum allowed size for the window.
     */
    public static void restrictWindowMinimumSize(final Window wnd, final Dimension minSize) {
        restrictMinimumSizeImpl(wnd, minSize);
    }

    /**
     * Adds listener to internal frame that ensures that
     * the frame is not resized less then specified size.
     * In case if it is resized anyway it restores the
     * minimal size.
     *
     * @param frame Frame which size should be restricted.
     * @param minSize The minimum allowed size for the window.
     */
    public static void restrictWindowMinimumSize(final JInternalFrame frame, final Dimension minSize) {
        restrictMinimumSizeImpl(frame, minSize);
    }

    private static void restrictMinimumSizeImpl(final Component component, final Dimension minSize) {
        // Ensure that window has normal size before resizing event
        Dimension curSize = component.getSize();
        if (curSize.width < minSize.width || curSize.height < minSize.height) {
            component.setSize(minSize);
        }
        // Adding listener. If added after size settings - will not be added if
        // one of arguments is NULL.
        component.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension curSize = component.getSize();
                if (curSize.width < minSize.width || curSize.height < minSize.height) {
                    Dimension newSize =
                        new Dimension(Math.max(minSize.width, curSize.width), Math.max(minSize.height, curSize.height));
                    component.setSize(newSize);
                }
            }
        });
    }
    
    
    /**
     * Ensures that specified window do not exceed screen size, including
     * possible task bar.
     * @param window The window to be placed exactly on screen.
     */
    public static void ensureWindowOnScreen(Window window) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        GraphicsConfiguration gc = window.getGraphicsConfiguration();
        Insets ins = kit.getScreenInsets( gc );
        Dimension totalSize = kit.getScreenSize();
        
        int availWidth = totalSize.width - ins.left - ins.right;
        int availHeight = totalSize.height - ins.top - ins.bottom;
        
        // modify location and size only if window is out of screen:
        Point loc = window.getLocation();
        Dimension size = window.getSize();
        if (loc.x < ins.left
            || loc.y < ins.top
            || loc.x + size.width > availWidth
            || loc.y + size.height > availHeight) {
            
            // modify size to be inside the screen:
            if (size.width > availWidth) {
                size.width = availWidth;
            }
            if (size.height > availHeight) {
                size.height = availHeight;
            }
            window.setSize( size );
            
            // modify locaiton to be on screen:
            if (loc.x < ins.left) {
                loc.x = ins.left;
            }
            if (loc.y < ins.top) {
                loc.y = ins.top;
            }
            
            if (loc.x + size.width > totalSize.width - ins.right) {
                loc.x = totalSize.width - ins.right - size.width;
            }
            if (loc.y + size.height > totalSize.height - ins.bottom) {
                loc.y = totalSize.height - ins.bottom - size.height;
            }
            window.setLocation( loc );
        }
    }


    /**
     * Method for demo purposes.
     * Shows specified frame with 1/1.5 size
     * of screen. When frame is closed the System.exit(0) is executed;
     * @param frame Frame to show
     */
    public static void showDemoFrame(JFrame frame) {
        // Making good size and center the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize((int) (screenSize.width / 1.5), (int) (screenSize.height / 1.5));
        center(frame);

        // Adding exit listener
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }

    /**
     * Method for demo purposes.
     * Shows specified panel in frame with 1/1.5 size
     * of screen. When frame is closed the System.exit(0) is executed
     * @param panel Panel to show
     */
    public static void showDemoPanel(Component panel) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        showDemoFrame(frame);
    }

    /**
     * Constructs instance of Dialog that has Frame or Dialog
     * parameter in constructor.
     *
     * @param dialogClass Class of dialog to construct.
     * @param parent Dialog owner component.
     * @return New dialog instance.
     * @throws IllegalArgumentException If invalid class specified.
     */
    public static Dialog createDialogInstance(Class<?> dialogClass, Component parent) throws IllegalArgumentException {
        // get parent window...
        Window owner;
        if (parent instanceof Window) {
            owner = (Window) parent;
        } else if (parent != null) {
            owner = SwingUtilities.getWindowAncestor(parent);
        } else {
            owner = null;
        }
        // create instance...
        Dialog result = null;
        Constructor<?> ctor = null;
        try {
            if (owner instanceof Dialog) {
                ctor = dialogClass.getConstructor(new Class[] { Dialog.class });
                result = (Dialog) ctor.newInstance(new Object[] { owner });

            } else if (owner instanceof Frame) {
                ctor = dialogClass.getConstructor(new Class[] { Frame.class });
                result = (Dialog) ctor.newInstance(new Object[] { owner });

            } else {
                ctor = dialogClass.getConstructor(new Class[] {
                });
                result = (Dialog) ctor.newInstance(new Object[] {
                });
            }

        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid dialog class. It must be public and have public constructor with Frame or Dialog parameter");
        }
        return result;
    }

    /**
     * Method to be used in conjunction with {@link #expandTreePaths(JTree, Enumeration)}
     * @param tree
     * @return
     */
    public static Enumeration<TreePath> getExpatnedTreePaths(JTree tree) {
        TreePath pathToRoot = new TreePath(tree.getModel().getRoot());
        Enumeration<TreePath> expandPaths = tree.getExpandedDescendants(pathToRoot);
        return expandPaths;
    }
    
    /**
     * Used to restore expansion state of the tree like:
     * <pre>
     *      Enumeration<TreePath> expandPaths = tree.getExpandedDescendants(pathToRoot);
     *       
     *       // do some changes in the tree model
     *       ...
     *       
     *       // fire change event
     *       DefaultTreeModel model = (DefaultTreeModel)treProblems.getModel();
     *       model.nodeStructureChanged(root);
     *       
     *       // restore  expanded state
     *       GuiUtils.expandTreePaths(tree, expandPaths);
     * </pre>
     * It is useful only when DefaultMutableTreeNode elements or their successors 
     * are used inside tree.
     * @param tree
     * @param expandPaths
     */
    public static void expandTreePaths(JTree tree, Enumeration<TreePath> expandPaths) {
        if(expandPaths != null) {
            
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
            
            // iterator through paths to expand
            while(expandPaths.hasMoreElements()) {
                    // obtaion last path user object
                    TreePath curPath = expandPaths.nextElement();
                    DefaultMutableTreeNode castedNode = (DefaultMutableTreeNode)curPath.getLastPathComponent();
                    Object obj =  castedNode.getUserObject();
                    
                    // go through all elements in the tree and search for elements to expand
                    // matching them by user object
                    Enumeration<?> enumAll = root.breadthFirstEnumeration();
                    while(enumAll.hasMoreElements()) {
                        DefaultMutableTreeNode curNode = (DefaultMutableTreeNode)enumAll.nextElement();
                        if(obj.equals(curNode.getUserObject())) {     
                            // expand
                            tree.expandPath(new  TreePath(curNode.getPath()));
                            break;
                        }
                    }
            }
        }
    }
    
    /**
     * Utility method to notify tree about changes without
     * loosing expansion state 
     * @param treProblems
     */
    public static void notifyTreeChanged(JTree treProblems) {
        Enumeration<TreePath> expandedState = GuiUtils.getExpatnedTreePaths(treProblems);
        DefaultTreeModel model = (DefaultTreeModel)treProblems.getModel();
        model.nodeStructureChanged((TreeNode)model.getRoot());
        GuiUtils.expandTreePaths(treProblems, expandedState);
    }
    
    /**
     * Returns index of a tab's component inside tabbed pane
     * @param tbp tabbed pane
     * @param component component to find
     * @return component's index
     * @throws IllegalArgumentException if component does not belong to
     * this tabbed pane
     */
    public static int getTabComponentIndex(JTabbedPane tbp, Component component) throws IllegalArgumentException {
        int count = tbp.getTabCount();
        for(int i = 0; i < count; i ++) {
            if(component == tbp.getComponentAt(i)) {
                return i;
            }
        }
        throw new IllegalArgumentException("No component found in the tabbed pane");
    }
    
    
    public static void paintGrid(Graphics g, int width, int height, Color darkColor, Color brightColor) {
        g.setColor(darkColor);
        g.fillRect(0, 0, width, height);
        
        g.setColor(brightColor);
        int inc = 5;
        for(int x = 0; x < width; x += inc*2) {
            for(int y = 0; y < height; y += inc*2) {
                g.fillRect(x, y, inc, inc);
                g.fillRect(x + inc, y + inc, inc, inc);
            }   
        }
    }
    
    /**
     * Formats stack tarce element into HTML with link
     * @param elem
     * @return
     */
    public static String formatElementToHTML(StackTraceElement elem) {
		// taking only class name before $ (in case of inner class)
		String className = elem.getClassName(); 
		className = className.split("\\$")[0];
		String href = className + ":" + elem.getLineNumber();
		
		return
		elem.getClassName() + "." + elem.getMethodName() +
        (elem.isNativeMethod() ? "(Native Method)" :
         (elem.getFileName() != null && elem.getLineNumber() >= 0 ?
          "(<a href=\"" + href + "\">" + elem.getFileName() + ":" + elem.getLineNumber() + "</a>)" :        	  
          (elem.getFileName() != null ?  "("+elem.getFileName()+")" : "(Unknown Source)")));
	}
    
    
    /**
     * Finds component with specified name in the specified hierarchy.
     * @param parent - hiererchy parent
     * @param beanName
     * @return null if component not found
     */
    public static Component findComponentByName(Container parent, String beanName) {
		
		if(beanName.equals(parent.getName())) {
			return parent;
		}
		
		// iterate over component hierarchy
		LinkedList<Container> parentList = new LinkedList<Container>();
		parentList.add(parent);
		do {
			Container curParent = parentList.removeFirst();
			int count = curParent.getComponentCount();
			for(int i = 0; i < count; i ++) {
				Component comp = curParent.getComponent(i);
				
				if(beanName.equals(comp.getName())) {
					// component found
					return comp; 
				}
				
				if(comp instanceof Container) {
					parentList.add((Container)comp);
				}
			}
		} while(!parentList.isEmpty());
		
		return null;
	}
}

