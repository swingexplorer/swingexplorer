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
package org.swingexplorer.internal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;


/**
 * 
 * @author Maxim Zakharenkov
 */
@SuppressWarnings("serial")
public class PnlAbout extends javax.swing.JPanel {
	
    /** Creates new form PnlAbout */
    private PnlAbout() {
    	Font titleFont = UIManager.getFont("Label.font").deriveFont(20f);
    	Font copyrightFont = UIManager.getFont("Label.font").deriveFont(Font.PLAIN);
    	
    	JLabel lblLogo = new JLabel();
    	lblLogo.setIcon(Icons.appLogo());
    	
    	JLabel lblTitle = new JLabel();
    	lblTitle.setVerticalTextPosition(SwingConstants.TOP);
    	lblTitle.setHorizontalTextPosition(SwingConstants.LEFT);
    	lblTitle.setText("Swing Explorer");
		lblTitle.setFont(titleFont);
    	
    	JLabel lblVersion = new JLabel();
    	lblVersion.setText("Version: " + SysUtils.getApplicationVersion());
    	
    	String webLink = "http://swingexplorer.github.io";
    	
    	JLabel lblWeblink = new JLabel();
    	lblWeblink.setText("<HTML><FONT color=\"#000099\"><U>" + webLink + "</U></FONT></HTML>");
    	lblWeblink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    	lblWeblink.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mousePressed(MouseEvent e) {
    			lblWeblink.setText("<HTML><FONT color=\"#009999\"><U>" + webLink + "</U></FONT></HTML>");
    		}
    		@Override
    		public void mouseReleased(MouseEvent e) {
    			lblWeblink.setText("<HTML><FONT color=\"#000099\"><U>" + webLink + "</U></FONT></HTML>");
    		}
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			if(!SysUtils.openBrowser(webLink)) {
    				
    				int res = JOptionPane.showOptionDialog(PnlAbout.this, "Sorry, we cannot open browser on your platform!\nWould you like to copy the link to clipboard?.", "Error", 
            				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
    				if(res == JOptionPane.YES_OPTION) {
	            		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	            		clipboard.setContents(new StringSelection(webLink), null);
    				}
            	}
    		}
		});
    	
    	JLabel lblCopyright = new JLabel();
    	lblCopyright.setFont(copyrightFont);
    	int year = Calendar.getInstance().get(Calendar.YEAR);
    	lblCopyright.setText("\u00a9 Swing Explorer team 2007-" + year);
    	
    	
    	JPanel pnlGrid = new JPanel();
    	pnlGrid.setLayout(new GridBagLayout());
    	pnlGrid.add(lblTitle, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    	pnlGrid.add(lblLogo, new GridBagConstraints(1, 0, 1, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    	pnlGrid.add(lblVersion, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    	pnlGrid.add(lblWeblink, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 20, 5), 0, 0));
    	
        setLayout(new BorderLayout());
        add(pnlGrid, BorderLayout.NORTH);
        add(lblCopyright, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
    
    static void openModal(Frame owner) {
	    final JDialog dlgAbout = new JDialog(owner, "About", true);
		
		dlgAbout.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		PnlAbout pnlAbout = new PnlAbout();
		dlgAbout.add(pnlAbout);
		dlgAbout.setResizable(false);
		dlgAbout.pack();
		GuiUtils.center(owner, dlgAbout);
		dlgAbout.setVisible(true);
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

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(13, 13);
		}
	}

	public static void main(String[] args) {
		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frm.setBounds(100,  100, 400, 300);
		frm.setVisible(true);
		openModal(frm);
	}
}

