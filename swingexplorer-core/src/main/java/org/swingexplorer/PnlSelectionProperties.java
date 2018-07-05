/*
 * PnlSelectionProperties.java
 *
 * Created on March 9, 2008, 8:39 PM
 */

package org.swingexplorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.swingexplorer.properties.PnlPropertySheet;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class PnlSelectionProperties extends javax.swing.JPanel {
	
	PnlPropertySheet pnlPropertySheet;
    MdlSwingExplorer model;
    ModelListener modelListener = new ModelListener();
	
    /** Creates new form PnlSelectionProperties */
    public PnlSelectionProperties() {
    	pnlPropertySheet = new PnlPropertySheet();
    	setLayout(new BorderLayout());
    	add(pnlPropertySheet, BorderLayout.CENTER);
        pnlPropertySheet.setColumnSize(0, 130);
        pnlPropertySheet.setColumnSize(1, 435);
        pnlPropertySheet.setName("pnlPropertySheet");
    }
    
    class ModelListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            if (!"selectedComponents".equals(propName)) {
                return;
            }
            Component[] selComps= model.getSelectedComponents();
            if(selComps.length ==0) {
                return;
            }
            pnlPropertySheet.setBean(selComps[0]);
        }
    }

    public MdlSwingExplorer getModel() {
        return model;
    }

    public void setModel(MdlSwingExplorer model) {
        this.model = model;
        model.addPropertyChangeListener(modelListener);
    }
}

