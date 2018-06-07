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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.StringReader;
import java.util.Calendar;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.EditorKit;


/**
 * 
 * @author Maxim Zakharenkov
 */
@SuppressWarnings("serial")
public class PNLAbout extends javax.swing.JPanel {
	
    private JScrollPane scpAbout;
    private JTextPane txaAbout;

	
    
    /** Creates new form PNLAbout */
    public PNLAbout() {
    	scpAbout = new JScrollPane();
        txaAbout = new JTextPane();
        txaAbout.setBorder(new EmptyBorder(10, 10, 10, 10));
        txaAbout.setEditable(false);
        txaAbout.setOpaque(false);
        scpAbout.setOpaque(false);
        scpAbout.setViewportView(txaAbout);
        scpAbout.getViewport().setOpaque(false);
        setLayout(new BorderLayout());
        add(scpAbout, BorderLayout.CENTER);
        setPreferredSize(new java.awt.Dimension(250, 170));        
        
        // it is not easy to scroll document to top in Swing
        // we need to set setAsynchronousLoadPriority for document and
        // read through editor kit
        txaAbout.setContentType("text/html");
        EditorKit edKit = txaAbout.getEditorKit();
        AbstractDocument doc = (AbstractDocument) edKit.createDefaultDocument();//(HTMLDocument) txaAbout.getDocument(); //new HTMLDocument();
        doc.setAsynchronousLoadPriority(-1);
        try {
        	int year = Calendar.getInstance().get(Calendar.YEAR);
            String text = "<html>" +
                    "<body>" +
                    "<big><span style=\"font-weight: bold;\">" +
                    "<a name=\"begin\"></a>Swing Explorer</span></big><br>" +
                    "&nbsp;&nbsp;&nbsp;Version: " + SysUtils.getApplicationVersion() +
                    "<br>&nbsp;&nbsp;&nbsp;(c) Maxim Zakharenkov 2007-" + year + "<br><br>" +
                    "&nbsp;&nbsp;&nbsp;<a href=\"http://www.swingexplorer.com/\">https://www.swingexplorer.com/</a><br><br>" +
                    "&nbsp;&nbsp;&nbsp;License: <a href=\"license\">LGPL</a>" +
                    "</body></html>";
            edKit.read(new StringReader(text), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        txaAbout.setDocument(doc);
        
        // here is scrolling finally
        txaAbout.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        
        txaAbout.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
            	
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                	if("license".equals(e.getDescription())) {
                		Dialog dlg = (Dialog)SwingUtilities.getWindowAncestor(PNLAbout.this);
                		DLGLicense.open(dlg);
                	} else if(!SysUtils.openBrowser(e.getURL().toString())) {
                		JOptionPane.showMessageDialog(null, "Can not open browser!", "Error", JOptionPane.ERROR_MESSAGE);
                	}
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics _g) {
    	Graphics2D g = (Graphics2D)_g;
    	GradientPaint paint = new GradientPaint(25, 25, Color.WHITE, getHeight(), getWidth(), new Color(0xc8ddf2));
    	g.setPaint(paint);
    	g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    
    public static void openModal(Frame owner) {
	    final JDialog dlgAbout = new JDialog(owner, "About", true);
		final JPanel glassPane = new JPanel(new BorderLayout());
		JPanel northGlass = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		glassPane.add(northGlass);
		glassPane.setOpaque(false);
		northGlass.setOpaque(false);
		
		JButton btnClose = new CloseButton();
		northGlass.add(btnClose);
		
		dlgAbout.setGlassPane(glassPane); 
		
		dlgAbout.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				glassPane.setVisible(true);
			}
		});
		
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dlgAbout.dispose();
			}
		});
		dlgAbout.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		PNLAbout pnlAbout = new PNLAbout();
		dlgAbout.setUndecorated(true);
		dlgAbout.add(pnlAbout);
		dlgAbout.pack();
		GuiUtils.center(owner, dlgAbout);
		dlgAbout.setVisible(true);
		glassPane.setVisible(true);
    }
    
	static class CloseButton extends JButton {
		
		public CloseButton() {
			setBorderPainted(false);
			setOpaque(false);
		}
		
		@Override
		protected void paintComponent(Graphics g) {

			
			ButtonModel model = getModel();
			if(model.isPressed() ) {
				g.setColor(Color.GRAY);
				g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
				g.drawLine(1, 1, getWidth() - 2, getHeight() - 2);
				g.drawLine(getWidth() - 2, 2, 2, getHeight() - 2);
			}  else  {
				g.setColor(Color.GRAY);
				g.drawRect(0, 0, getWidth() - 2, getHeight() - 2);
				g.drawLine(0, 0, getWidth() - 2, getHeight() - 2);
				g.drawLine(getWidth() - 2, 0, 0, getHeight() - 2);

			}
		}
		
		public Dimension getPreferredSize() {
			return new Dimension(13, 13);
		}
	}

}

