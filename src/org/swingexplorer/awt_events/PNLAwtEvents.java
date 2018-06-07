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
package org.swingexplorer.awt_events;


import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import org.swingexplorer.RichAction;
import org.swingexplorer.plaf.CustomTableHeaderUI;



/**
 *
 * @author Maxim Zakharenkov
 */
public class PNLAwtEvents extends javax.swing.JPanel {

	/** Creates new form PNLEvents */
    public PNLAwtEvents() {
        initComponents();
        tblEvents.setName("tblEvents");
        
        sppMasterDetail.setName("sppMasterDetail");
        sppMasterDetail.setResizeWeight(1);
        setEventModel(new LocalAWTEventModel(this));
        tblEvents.setModel(mdlTblEvents);
        initActions();
        tblEvents.getColumnModel().getColumn(0).setPreferredWidth(270);
        tblEvents.getColumnModel().getColumn(1).setPreferredWidth(65);
        tblEvents.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventModel.addPropertyChangeListener(new ModelPropertyChangeListener());
        
        JTableHeader header = tblEvents.getTableHeader();
        header.setUI(new CustomTableHeaderUI());
    }
    
    void initActions() {
    	actStartEventMonitoring = new ActStartEventMonitoring(eventModel);
    	actStopEventMonitoring = new ActStopEventMonitoring(eventModel);
    	
    	InputMap map = tblEvents.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    	map.put(KeyStroke.getKeyStroke("ENTER"), "show-source");

    	tblEvents.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			if(e.getClickCount() == 2) {
    				actShowEventSource.actionPerformed(null);
    			}
    		}
    	});
    	
    	actClearEvents = new ActClearEvents(this);
    	btnClear.setAction(actClearEvents);
    	btnStart.setAction(actStartEventMonitoring);
    	tblEvents.getSelectionModel().addListSelectionListener(new ActEventSelected(this));
    	
    	actShowFilter = new ActShowFilter(this);
    	btnShowFilter.setAction(actShowFilter);
    }
    
    public void setShowEventSourceAction(RichAction act) {
    	btnShowSource.setAction(act);
    	ActionMap actMap = tblEvents.getActionMap();
    	actMap.put("show-source", act);
    	actShowEventSource = act;
    }
    
    public AWTEvent getSelectedEvent() {
    	int selRow = tblEvents.getSelectedRow();
		return mdlTblEvents.getEvent(selRow);
    }
    
    public void addEvent(AWTEvent evt) {    	
    	mdlTblEvents.addEvent(evt);
    	int rowNo = mdlTblEvents.getRowCount() - 1;
    	mdlTblEvents.fireTableRowsInserted(rowNo, rowNo);
        tblEvents.scrollRectToVisible(tblEvents.getCellRect(rowNo, 1, true));
    }
    
    public void clearEvents() {
        mdlTblEvents.clear();
    }
    
    private void updateSttus() {
        
    }
    
    public void setEventModel(AWTEventModel modelP) {
    	if(eventModel != null) {
    		eventModel.removeEventListener(modelListener);
    	}
    	
    	eventModel = modelP;
    	eventModel.addEventListener(modelListener);
    }
    
    public AWTEventModel getEventModel() {
    	return eventModel;
    }
    
    class ModelListener implements AWTEventListener {
		public void eventDispatched(AWTEvent event) {
			addEvent(event);			
		}    	
    }

    public class ModelPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if("monitoring".equals(evt.getPropertyName())) {
				if((Boolean)evt.getNewValue()) {
					btnStart.setAction(actStopEventMonitoring);
				} else {
					btnStart.setAction(actStartEventMonitoring);
				}
			}
		}
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sppMasterDetail = new javax.swing.JSplitPane();
        pnlEventDetails = new org.swingexplorer.awt_events.PNLEventDetails();
        scpTable = new javax.swing.JScrollPane();
        tblEvents = new javax.swing.JTable();
        txtStatus = new javax.swing.JTextField();
        btnStart = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnShowSource = new javax.swing.JButton();
        btnShowFilter = new javax.swing.JButton();

        sppMasterDetail.setDividerLocation(356);
        sppMasterDetail.setMinimumSize(new java.awt.Dimension(0, 0));

        pnlEventDetails.setToolTipText("Selected event details");
        pnlEventDetails.setMinimumSize(new java.awt.Dimension(0, 0));
        sppMasterDetail.setRightComponent(pnlEventDetails);

        scpTable.setMinimumSize(new java.awt.Dimension(0, 0));

        tblEvents.setToolTipText("List of captured AWT events");
        tblEvents.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scpTable.setViewportView(tblEvents);

        sppMasterDetail.setLeftComponent(scpTable);

        txtStatus.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        txtStatus.setEditable(false);
        txtStatus.setBorder(null);
        txtStatus.setMinimumSize(new java.awt.Dimension(0, 0));

        btnStart.setText("Start");
        btnStart.setToolTipText("Start event monitoring");
        btnStart.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnStart.setPreferredSize(new java.awt.Dimension(31, 19));

        btnClear.setText("Clear");
        btnClear.setToolTipText("Clear all current events");
        btnClear.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnShowSource.setText("Source");
        btnShowSource.setToolTipText("Show selected event's component in the tree");
        btnShowSource.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnShowFilter.setText("Filter");
        btnShowFilter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(btnStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(btnClear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(btnShowSource)
                .add(0, 0, 0)
                .add(btnShowFilter)
                .add(3, 3, 3)
                .add(txtStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, sppMasterDetail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnShowSource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnShowFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnClear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sppMasterDetail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {btnClear, btnShowFilter, btnShowSource, btnStart}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    javax.swing.JButton btnShowFilter;
    private javax.swing.JButton btnShowSource;
    private javax.swing.JButton btnStart;
    org.swingexplorer.awt_events.PNLEventDetails pnlEventDetails;
    private javax.swing.JScrollPane scpTable;
    private javax.swing.JSplitPane sppMasterDetail;
    private javax.swing.JTable tblEvents;
    private javax.swing.JTextField txtStatus;
    // End of variables declaration//GEN-END:variables
    
    MdlEvents mdlTblEvents = new MdlEvents();
    AWTEventModel eventModel;
    AWTEventListener modelListener = new ModelListener();
    ActStartEventMonitoring actStartEventMonitoring;
    ActStopEventMonitoring actStopEventMonitoring;
    ActClearEvents actClearEvents;
    RichAction actShowEventSource;
    ActShowFilter actShowFilter;
}

