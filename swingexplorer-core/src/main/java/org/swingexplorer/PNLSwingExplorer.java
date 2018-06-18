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


import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.swingexplorer.awt_events.AWTEventModel;
import org.swingexplorer.edt_monitor.MdlEDTMonitor;
import org.swingexplorer.graphics.CurrentOperationChangeEvent;
import org.swingexplorer.graphics.ImageEvent;
import org.swingexplorer.graphics.OperationResetEvent;
import org.swingexplorer.graphics.PlayerListener;
import org.swingexplorer.graphics.StateEvent;



/**
 *
 * @author  Maxim Zakharenkov
 */
public class PNLSwingExplorer extends javax.swing.JPanel {
    
    /** Creates new form PNLSwingExplorer */
    public PNLSwingExplorer() {
    	initComponents();
        sppMain.setName("sppMain");
        sppRight.setName("sppRight");
        tbpBottom.setName("tbpBottom");
        pnlProperties.setName("pnlProperties");
        
        // animated icon for event monitor
        icoEventMonitoring = new AnimatedIcon(tbpBottom);
        icoEventMonitoring.setIcons(Icons.monitor());
        AWTEventModel awtEventModel = pnlAWTEvents.getEventModel();
        awtEventModel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if("monitoring".equals(evt.getPropertyName())) {
                    Boolean value = (Boolean)evt.getNewValue();
                    icoEventMonitoring.setInProgress(value);
                    
                    int idx = GuiUtils.getTabComponentIndex(tbpBottom, pnlAWTEvents);
                    if(value) {
                        tbpBottom.setIconAt(idx, icoEventMonitoring);
                        tbpBottom.setToolTipTextAt(idx, "Monitoring AWT events");
                    } else {
                        tbpBottom.setIconAt(idx, null);
                        tbpBottom.setToolTipTextAt(idx, null);
                    }
                }
            }
        });
        
        // animated icon for EDT monitor
        icoEDTMonitoring = new AnimatedIcon(tbpBottom);
        icoEDTMonitoring.setIcons(Icons.monitor());
        final MdlEDTMonitor mdlEDTMonitor = pnlEDTMonitor.getModel();
        mdlEDTMonitor.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if("monitorViolations".equals(evt.getPropertyName()) ||  
                    "monitorHangs".equals(evt.getPropertyName()) ||
                    "monitorExceptions".equals(evt.getPropertyName())) {
                    
                    boolean value = mdlEDTMonitor.isMonitorHangs() || mdlEDTMonitor.isMonitorViolations() || mdlEDTMonitor.isMonitorExceptions();
                    int idx = GuiUtils.getTabComponentIndex(tbpBottom, pnlEDTMonitor);
                    if(value) {
                        tbpBottom.setIconAt(idx, icoEDTMonitoring);
                        tbpBottom.setToolTipTextAt(idx, "Monitoring Event Dispatch Thread");
                    } else {
                        tbpBottom.setIconAt(idx, null);
                        tbpBottom.setToolTipTextAt(idx, null);
                    }
                    icoEDTMonitoring.setInProgress(value);
                }
            }
        });
        
        // set parameters from command line
        mdlEDTMonitor.setMonitorViolations(SysUtils.isMonitorEDTViolationsAndReset());
        mdlEDTMonitor.setMonitorHangs(SysUtils.isMonitorEDTHangsAndReset());
        mdlEDTMonitor.setMonitorExceptions(SysUtils.isMonitorEDTExceptionsAndReset());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sppMain = new javax.swing.JSplitPane();
        pnlComponentTree = new org.swingexplorer.PNLComponentTree();
        pnlWorkArea = new javax.swing.JPanel();
        txtPath = new javax.swing.JTextField();
        lblDisplayedPath = new javax.swing.JLabel();
        sppRight = new javax.swing.JSplitPane();
        scpDisplay = new javax.swing.JScrollPane();
        pnlGuiDisplay = new org.swingexplorer.PNLGuiDisplay();
        tbpBottom = new javax.swing.JTabbedPane();
        pnlProperties = new org.swingexplorer.PNLSelectionProperties();
        pnlAdditionTrace = new org.swingexplorer.additiontrace.PNLAdditionTrace();
        pnlPlayerControls = new org.swingexplorer.PNLPlayerControls();
        pnlAWTEvents = new org.swingexplorer.awt_events.PNLAwtEvents();
        pnlEDTMonitor = new org.swingexplorer.edt_monitor.PNLEDTMonitor();
        pnlOptions = new org.swingexplorer.PNLOptions();
        pnlStatusBar = new org.swingexplorer.PNLStatusBar();
        tlbMain = new org.swingexplorer.RichToolbar();

        sppMain.setBorder(null);
        sppMain.setDividerLocation(200);
        sppMain.setMinimumSize(new java.awt.Dimension(0, 0));

        pnlComponentTree.setMinimumSize(new java.awt.Dimension(0, 0));
        sppMain.setLeftComponent(pnlComponentTree);

        pnlWorkArea.setPreferredSize(new java.awt.Dimension(0, 0));

        txtPath.setEditable(false);
        txtPath.setToolTipText("<html>Path to displayed component<br>\nin the containment hierarchy\n</html>");

        lblDisplayedPath.setText("Displayed:");

        sppRight.setBorder(null);
        sppRight.setDividerLocation(314);
        sppRight.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sppRight.setResizeWeight(1.0);

        org.jdesktop.layout.GroupLayout pnlGuiDisplayLayout = new org.jdesktop.layout.GroupLayout(pnlGuiDisplay);
        pnlGuiDisplay.setLayout(pnlGuiDisplayLayout);
        pnlGuiDisplayLayout.setHorizontalGroup(
            pnlGuiDisplayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 704, Short.MAX_VALUE)
        );
        pnlGuiDisplayLayout.setVerticalGroup(
            pnlGuiDisplayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 416, Short.MAX_VALUE)
        );

        scpDisplay.setViewportView(pnlGuiDisplay);

        sppRight.setTopComponent(scpDisplay);

        tbpBottom.setMinimumSize(new java.awt.Dimension(0, 0));
        tbpBottom.addTab("Properties", null, pnlProperties, "Proberties of the selected component");
        tbpBottom.addTab("Addition trace", null, pnlAdditionTrace, "<html>Stack trace of the instruction<br>\nwhere the selected component<br>\nwas added into a container</html>");

        pnlPlayerControls.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tbpBottom.addTab("Player", null, pnlPlayerControls, "Graphics2D player");
        tbpBottom.addTab("AWT Events", null, pnlAWTEvents, "AWT event monitor");
        tbpBottom.addTab("EDT monitor", null, pnlEDTMonitor, "<html>Event Dispatch thread<br>\nproblem monitor\n</html>");
        tbpBottom.addTab("Options", pnlOptions);

        sppRight.setRightComponent(tbpBottom);

        org.jdesktop.layout.GroupLayout pnlWorkAreaLayout = new org.jdesktop.layout.GroupLayout(pnlWorkArea);
        pnlWorkArea.setLayout(pnlWorkAreaLayout);
        pnlWorkAreaLayout.setHorizontalGroup(
            pnlWorkAreaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlWorkAreaLayout.createSequentialGroup()
                .add(lblDisplayedPath)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
            .add(sppRight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
        );
        pnlWorkAreaLayout.setVerticalGroup(
            pnlWorkAreaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlWorkAreaLayout.createSequentialGroup()
                .add(pnlWorkAreaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDisplayedPath)
                    .add(txtPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sppRight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE))
        );

        sppMain.setRightComponent(pnlWorkArea);

        tlbMain.setFloatable(false);
        tlbMain.setRollover(true);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStatusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
            .add(tlbMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, sppMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(tlbMain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sppMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStatusBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDisplayedPath;
    private org.swingexplorer.awt_events.PNLAwtEvents pnlAWTEvents;
    private org.swingexplorer.additiontrace.PNLAdditionTrace pnlAdditionTrace;
    private org.swingexplorer.PNLComponentTree pnlComponentTree;
    private org.swingexplorer.edt_monitor.PNLEDTMonitor pnlEDTMonitor;
    private org.swingexplorer.PNLGuiDisplay pnlGuiDisplay;
    private org.swingexplorer.PNLOptions pnlOptions;
    private org.swingexplorer.PNLPlayerControls pnlPlayerControls;
    private org.swingexplorer.PNLSelectionProperties pnlProperties;
    private org.swingexplorer.PNLStatusBar pnlStatusBar;
    private javax.swing.JPanel pnlWorkArea;
    private javax.swing.JScrollPane scpDisplay;
    private javax.swing.JSplitPane sppMain;
    private javax.swing.JSplitPane sppRight;
    private javax.swing.JTabbedPane tbpBottom;
    private org.swingexplorer.RichToolbar tlbMain;
    private javax.swing.JTextField txtPath;
    // End of variables declaration//GEN-END:variables
    
    
    AnimatedIcon icoEventMonitoring;
    AnimatedIcon icoEDTMonitoring;
    
    Launcher application;
    
    ModelListener listener = new ModelListener();
    PlayerListener playerListener = new PlayerListenerImpl();
    
    JComboBox  cmbScale = new JComboBox();
    
    ActRefresh actRefresh;
    ActDisplayComponent actDisplayComponent;
    ActDumpAdditionTrace actTraceComponentAddition;
    ActTreeSelectionChanged actTreeSelectionChanged;
    ActZoomIn actZoomIn;
    ActZoomOut actZoomOut;
    ActMoveOverDisplay actMoveOverDisplay;
    ActMouseClickOnDisplay actClickOnDisplay;
    ActKeyOnDisplay actKeyOnDisplay;
    ActDisplayTopContainer actDisplayTopContainer;
    ActDisplayParent actDisplayParent;
    
    
    void initActions() {
    	actRefresh = new ActRefresh(pnlComponentTree, application.model);
        pnlComponentTree.addAction(actRefresh);
        
        actDisplayComponent = new ActDisplayComponent(application.model, pnlComponentTree);
        pnlComponentTree.addAction(actDisplayComponent);
        pnlComponentTree.setDefaultTreeAction(actDisplayComponent);
        
        actTraceComponentAddition = new ActDumpAdditionTrace(application.model, pnlComponentTree);
        pnlComponentTree.addAction(actTraceComponentAddition);
        
        actTreeSelectionChanged = new ActTreeSelectionChanged(application.model, pnlComponentTree);
        pnlComponentTree.setTreeSelectionAction(actTreeSelectionChanged);
        
        pnlGuiDisplay.setModel(application.model);
        pnlComponentTree.setModel(application.model);
        pnlProperties.setModel(application.model);
        pnlStatusBar.setModel(application.model);
        pnlAdditionTrace.setModel(application.model);
        pnlAdditionTrace.setIDESupport(application.ideSupport);
        pnlEDTMonitor.setIDESupport(application.ideSupport);
        
        application.model.addPropertyChangeListener(listener);
        
        application.player.addPlayerListener(playerListener);
    
        addAction(actRefresh);
        
        actZoomOut = new ActZoomOut(application.model);
        addAction(actZoomOut);
        
        cmbScale.setEditable(true);
        //cmbScale.setPreferredSize(new Dimension(20,16));
        cmbScale.setMaximumSize(new Dimension(60, 20));
        cmbScale.setModel(new DefaultComboBoxModel(new String[]{"25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%"}));
        tlbMain.add(cmbScale);
        cmbScale.setSelectedItem("" + (int)(application.model.getDisplayScale()*100) + "%");
        
        ComboScaleListener scaleListener = new ComboScaleListener();
        cmbScale.addItemListener(scaleListener);
        cmbScale.addPopupMenuListener(scaleListener);
        
        actZoomIn = new ActZoomIn(application.model);
        addAction(actZoomIn);
        
        actMoveOverDisplay = new ActMoveOverDisplay(pnlGuiDisplay, application.model);
        actKeyOnDisplay = new ActKeyOnDisplay(pnlGuiDisplay, application.model);
        pnlGuiDisplay.addMouseMotionListener(actMoveOverDisplay);
        pnlGuiDisplay.addMouseListener(actMoveOverDisplay);
        pnlGuiDisplay.addKeyListener(actKeyOnDisplay);
        pnlGuiDisplay.setFocusable(true);
        
        
        actClickOnDisplay = new ActMouseClickOnDisplay(pnlGuiDisplay, application.model);
        pnlGuiDisplay.addMouseListener(actClickOnDisplay);
        
        addAction(actDisplayComponent);
        
        actDisplayParent = new ActDisplayParent(application);
        addAction(actDisplayParent);
        
        actDisplayTopContainer = new ActDisplayTopContainer(application);
        addAction(actDisplayTopContainer);
        
        pnlPlayerControls.setPlayer(application.player);
        pnlPlayerControls.setIDESupport(application.ideSupport);
        pnlAWTEvents.setShowEventSourceAction(new ActShowEventSource(application.model, pnlAWTEvents));
        
        // set options into option panel
        pnlOptions.setOptions(application.model.getOptions());
                
//        tbpBottom.addComponentListener(resizeListener);
//        pnlWorkArea.addComponentListener(resizeListener);
//        pnlComponentTree.addComponentListener(resizeListener);
    }
    

    // when splitter locations are changing or components are resized
    // we memorize new positions in the options
//    class ResizeListener extends ComponentAdapter{
//    	int count = 0;
//    	public void componentResized(ComponentEvent e) {
//    		if(count < 3) {
//    			// first time we read value from options
//    			// because it happens when UI is layed-out for the first time
//	    		Options options = application.model.getOptions();
//	    		sppMain.setDividerLocation(options.getVerticalDividerLocation());
//	    		sppRight.setDividerLocation(options.getHorizontalDividerLocation());
//	    		count ++;
//    		} else {
//    			// next time we save divider value to options
//				Options options = application.model.getOptions();
//				options.setVerticalDividerLocation(sppMain.getDividerLocation());
//				options.setHorizontalDividerLocation(sppRight.getDividerLocation());
//    		}
//		}
//    }
//    
    class ComboScaleListener implements ItemListener, PopupMenuListener {
    	void changeScale() {
    		String item = (String)cmbScale.getSelectedItem();
    		
    		int percentIndex = item.lastIndexOf("%");
    		if(percentIndex != -1) {
    			item = item.substring(0, percentIndex);
    		}
    		application.model.setDisplayScale(Double.parseDouble(item.toString())/100);	
    	}
		public void itemStateChanged(ItemEvent e) {
			if(ItemEvent.SELECTED == e.getStateChange()) {
				if(!cmbScale.isPopupVisible()) {
					changeScale();		
				}
			}			
		}
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			changeScale();		
		}
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
    }
    
    public void addAction(RichAction act) {
    	tlbMain.addActionEx(act);
    	act.setApplication(application);
    }

    
    class ModelListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			String propName = evt.getPropertyName();
			if ("displayedComponent".equals(propName)) {
                actRefresh.refreshTreeModel();
				txtPath.setText(application.model.getComponentPath(application.model.getDisplayedComponent(), true));
			} else if ("displayScale".equals(propName)) {
				cmbScale.getEditor().setItem("" + (int)(application.model.getDisplayScale()*100) + "%");
			} else if("displayedComponentImage".equals(propName)) {
                application.player.setOperations(application.model.getDisplayedComponent());
            }
		}
	}
    
    class PlayerListenerImpl implements PlayerListener {
    	
		public void imageRendered(ImageEvent evt) {
            // listener should be removed and restored after
            // setDisplayedComponentImage to avoid redundant reaction on
		    // change of "displayedComponentImage" property inside ModelListener
            // without this slider in the player works very slowely
            // because all operations are recalculated after slider is moved a bit
            application.model.removePropertyChangeListener(listener);
			
            application.model.setDisplayedComponentImage(new ImageIcon(evt.getImage()));
            
            application.model.addPropertyChangeListener(listener);
		}

		public void stateChanged(StateEvent evt) {
		}

		public void operationsReset(OperationResetEvent operations) {
		}

		public void currentOperationChanged(CurrentOperationChangeEvent evt) {
			
		}
    }

	public void setApplication(Launcher app) {
		application = app;
		initActions();
	}

	public Launcher getApplication() {
		return application;
	}
}

