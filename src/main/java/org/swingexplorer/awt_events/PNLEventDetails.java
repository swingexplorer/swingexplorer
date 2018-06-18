/*
 * PNLEventDetails.java
 *
 * Created on February 6, 2008, 12:11 PM
 */

package org.swingexplorer.awt_events;

import java.awt.AWTEvent;
import java.awt.BorderLayout;

import org.swingexplorer.properties.PNLPropertySheet;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class PNLEventDetails extends javax.swing.JPanel {

	 private org.swingexplorer.properties.PNLPropertySheet pnlEventProperties;
	 
    /** Creates new form PNLEventDetails */
    public PNLEventDetails() {
    	setLayout(new BorderLayout());
    	pnlEventProperties = new PNLPropertySheet();
    	add(pnlEventProperties, BorderLayout.CENTER);
    	pnlEventProperties.setName("pnlEventProperties");
    }
    
    public void setEvent(AWTEvent evt) {
    	pnlEventProperties.setBean(evt);
    }
    
    public AWTEvent getEvent() {
    	return (AWTEvent)pnlEventProperties.getBean();
    }
    
    @Override
    public void setToolTipText(String text) {
    	pnlEventProperties.setToolTipText(text);
    }
    
    @Override
    public String getToolTipText() {
        return pnlEventProperties.getToolTipText();
    }
}
