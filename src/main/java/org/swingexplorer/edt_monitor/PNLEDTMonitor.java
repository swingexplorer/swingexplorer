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

import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringReader;
import java.util.HashSet;

import javax.swing.Icon;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.Iterator;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.swingexplorer.GuiUtils;
import org.swingexplorer.Icons;
import org.swingexplorer.PNLNoAgentModeMessage;
import org.swingexplorer.idesupport.IDESupport;
import org.swingexplorer.instrument.Problem;
import org.swingexplorer.instrument.ProblemListener;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class PNLEDTMonitor extends javax.swing.JPanel {
    
    /** Creates new form PNLEDTMonitor */
    public PNLEDTMonitor() {
        initComponents();
        setModel(new MdlEDTMonitor());
        
        //  init spinner
        spnDelay.setModel(new SpinnerNumberModel(1000, 0, 10000, 100));
        JSpinner.DefaultEditor editor = (DefaultEditor)spnDelay.getEditor();
        editor.getTextField().setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        spnDelay.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                mdlMonitor.setMinimalMonitoredHangTime(((Number)spnDelay.getValue()).intValue());
            }
        });
        
        // init tree
        treProblems.setRootVisible(false);
        treProblems.setShowsRootHandles(true);
        DefaultTreeModel model = (DefaultTreeModel) treProblems.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        model.setRoot(root);
        model.nodeStructureChanged(root);
        
        // init tree renderer
        renderer = new DefaultTreeCellRenderer();
        renderer.setOpenIcon(Icons.warning());
        renderer.setClosedIcon(Icons.warning());
        renderer.setLeafIcon(Icons.codeLineBall());
        treProblems.setCellRenderer(renderer);
        initActions();
        
        treProblems.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
			}
			public void mouseMoved(MouseEvent e) {
		        if(getLink(e.getPoint()) != null) {
		        	treProblems.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					treProblems.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		        }
			}
        });
        
        treProblems.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String link = getLink(e.getPoint());
				if (link != null) {
					actOpenSourceCode.openSourceCode(link);
				}
			}
		});
    }
    
    
    String getLink(Point p) {
    	int row = treProblems.getRowForLocation(p.x, p.y);
        if(row == -1) {
        	return null;
        }
        TreePath path =  treProblems.getPathForRow(row);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        if(!(node.getUserObject() instanceof String)) {
        	return null;
        }
        String component = (String)node.getUserObject();
        
        

        StringReader reader = new StringReader(component);
        HTMLDocument doc = new HTMLDocument();
        String text = null;
        try {
			new HTMLEditorKit().read(reader, doc, 0);
			text = doc.getText(0, doc.getLength());
		} catch (Exception e1) {
		} 
		
		
		int idx1 = text.indexOf("(");
        int idx2 = text.indexOf(")", idx1);
        String strBefore = text.substring(0, idx1);
        String strIn = text.substring(idx1 - 1, idx2);
		
        
        FontMetrics metrics = getGraphics().getFontMetrics();
        int dx = metrics.stringWidth(strBefore);
        int width = metrics.stringWidth(strIn);
        
        Rectangle bounds = treProblems.getRowBounds(row);
        
        
        int labelStart = 0;
        Icon currentI = renderer.getIcon();
		if(currentI != null && renderer.getText() != null) {
			labelStart = currentI.getIconWidth() + Math.max(0, renderer.getIconTextGap() - 1);
		}

		bounds.x += (dx + labelStart);
        bounds.width = width;
        
        if(bounds.contains(p)) {
    		// obtain href value
    		Iterator iter = (Iterator) doc.getIterator(HTML.getTag("a"));
    		AttributeSet set = iter.getAttributes();
    		if(set != null) {
    			return (String)set.getAttribute(HTML.Attribute.HREF);
    		}
        } 
        return null;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scpProblems = new javax.swing.JScrollPane();
        treProblems = new javax.swing.JTree();
        chbEDTViolations = new javax.swing.JCheckBox();
        chbHangs = new javax.swing.JCheckBox();
        btnTrace = new javax.swing.JButton();
        spnDelay = new javax.swing.JSpinner();
        btnClear = new javax.swing.JButton();
        chbEDTExceptions = new javax.swing.JCheckBox();

        scpProblems.setMinimumSize(new java.awt.Dimension(0, 0));
        scpProblems.setViewportView(treProblems);

        chbEDTViolations.setText("EDT violations");
        chbEDTViolations.setToolTipText("Monitor violations of event diapatch thread");
        chbEDTViolations.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbEDTViolationsItemStateChanged(evt);
            }
        });

        chbHangs.setText("EDT hangs more than (ms):");
        chbHangs.setToolTipText("Monitor hangs occured in the event dispatch thread more than specified time");
        chbHangs.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chbHangs.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        chbHangs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbHangsItemStateChanged(evt);
            }
        });

        btnTrace.setText("Trace");
        btnTrace.setToolTipText("Dump selected problem's stack trace to console");
        btnTrace.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        spnDelay.setToolTipText("Minimal delays to monitor (in milliseconds)");

        btnClear.setText("Clear");
        btnClear.setToolTipText("Clear problem list");
        btnClear.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        chbEDTExceptions.setText("EDT exceptions");
        chbEDTExceptions.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbEDTExceptionsItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(chbEDTViolations)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chbEDTExceptions)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chbHangs)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spnDelay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnTrace)
                .add(0, 0, 0)
                .add(btnClear)
                .add(37, 37, 37))
            .add(scpProblems, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chbEDTViolations)
                    .add(chbHangs)
                    .add(btnTrace)
                    .add(btnClear)
                    .add(spnDelay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(chbEDTExceptions))
                .add(3, 3, 3)
                .add(scpProblems, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void initActions() {
        actClear = new ActClear(this);
        btnClear.addActionListener(actClear);
        
        actTrace = new ActTrace(this);
        btnTrace.addActionListener(actTrace);
        
        actOpenSourceCode = new ActOpenSourceCode(this);
    }
    
    private void chbEDTViolationsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbEDTViolationsItemStateChanged
        if(!mdlMonitor.isViolationMonitoringAvailable() && evt.getStateChange() == ItemEvent.SELECTED) {
        	PNLNoAgentModeMessage.openDialog(this);
            chbEDTViolations.setSelected(false);
            return;
        }
        mdlMonitor.setMonitorViolations(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_chbEDTViolationsItemStateChanged

    private void chbHangsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbHangsItemStateChanged
        mdlMonitor.setMonitorHangs(evt.getStateChange() == ItemEvent.SELECTED);
}//GEN-LAST:event_chbHangsItemStateChanged

    private void chbEDTExceptionsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbEDTExceptionsItemStateChanged
        mdlMonitor.setMonitorExceptions(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_chbEDTExceptionsItemStateChanged
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton btnClear;
    javax.swing.JButton btnTrace;
    javax.swing.JCheckBox chbEDTExceptions;
    javax.swing.JCheckBox chbEDTViolations;
    javax.swing.JCheckBox chbHangs;
    javax.swing.JScrollPane scpProblems;
    javax.swing.JSpinner spnDelay;
    javax.swing.JTree treProblems;
    // End of variables declaration//GEN-END:variables
    
    ActClear actClear;
    ActTrace actTrace;
    ActOpenSourceCode actOpenSourceCode;
    MdlEDTMonitor mdlMonitor;
    DefaultTreeCellRenderer renderer;
    ModelPropertyChangeListener modelPropChangeListener = new ModelPropertyChangeListener();
    ProblemListenerImpl problemListener = new ProblemListenerImpl();
    
    
    public void setModel(MdlEDTMonitor _model) {
        mdlMonitor = _model;
        mdlMonitor.addProblemListener(problemListener);
        mdlMonitor.addPropertyChangeListener(modelPropChangeListener);
        
        chbHangs.setSelected(mdlMonitor.isMonitorHangs());
        chbEDTViolations.setSelected(mdlMonitor.isMonitorViolations());
        spnDelay.setValue((int)mdlMonitor.getMinimalMonitoredHangTime());
        spnDelay.setEnabled(mdlMonitor.isMonitorHangs());
    }
    
    public MdlEDTMonitor getModel() {
        return mdlMonitor;
    }
    
    
    public void addProblem(Problem problem){
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treProblems.getModel().getRoot();
        
        // adding problem
        DefaultMutableTreeNode problemNode = new DefaultMutableTreeNode(problem);
        
        // adding stack trace elements
        if(problem.getProblemTrace() != null) {
            for(StackTraceElement elem : problem.getProblemTrace()) {
            	
            	
            	String strElement  = "<html>" + GuiUtils.formatElementToHTML(elem) + "</html>";
                DefaultMutableTreeNode traceNode = new DefaultMutableTreeNode(strElement);
                problemNode.add(traceNode);
            }
        }
        root.add(problemNode);
        GuiUtils.notifyTreeChanged(treProblems);
    }
    
    public void clearProblems() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treProblems.getModel().getRoot();
        root.removeAllChildren();
        GuiUtils.notifyTreeChanged(treProblems);
    }

    /** Returns array of problems selected */
    public Problem[] getSelectedProblems() {
        TreePath[] paths = treProblems.getSelectionPaths();
        if(paths == null) {
            return null;
        }
        
        // getting only "problem" type nodes without duplications
        HashSet<DefaultMutableTreeNode> problemNodeSet = new HashSet<DefaultMutableTreeNode>(); 
        for(TreePath curPath : paths) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)curPath.getPathComponent(1);
            problemNodeSet.add(node);
        }
        
        //
        Problem[] problems = new Problem[problemNodeSet.size()];
        int i = 0;
        for(DefaultMutableTreeNode curNode : problemNodeSet) {
            problems[i++] = (Problem) curNode.getUserObject();
        }
        
        return problems;
    }

    
    class ProblemListenerImpl implements ProblemListener {
        public void problemOccured(Problem problem) {
            addProblem(problem);
        }
    }
    
    
    class ModelPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if("monitorViolations".equals(evt.getPropertyName())) {
                chbEDTViolations.setSelected((Boolean)evt.getNewValue());
            } else if("minimalMonitoredHangTime".equals(evt.getPropertyName())) {
                spnDelay.setValue(evt.getNewValue());
            } else if("monitorHangs".equals(evt.getPropertyName())) {
                chbHangs.setSelected((Boolean)evt.getNewValue());
                spnDelay.setEnabled((Boolean)evt.getNewValue());
            }
        }
    }

	public void setIDESupport(IDESupport _ideSupport) {
		actOpenSourceCode.ideSupport = _ideSupport;
	}
}
