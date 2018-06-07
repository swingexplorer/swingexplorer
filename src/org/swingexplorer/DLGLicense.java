/*
 * Copyright 2007, Maxim Zakharenkov
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * $Header: $
 */
package org.swingexplorer;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author  Maxim Zakharenkov
 */
@SuppressWarnings("serial")
public class DLGLicense extends JDialog {

	private JTextArea txaLicense;
	private JButton btnClose;
	private JScrollPane scp;

	public DLGLicense(Dialog owner) {
		super(owner, true);
		ByteArrayOutputStream strBuf = getLicenseText();
		
		txaLicense = new JTextArea();
		txaLicense.setEditable(false);
		txaLicense.setText(strBuf.toString());
		btnClose = new JButton("Close");
		
		JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlSouth.add(btnClose);
		
		scp = new JScrollPane(txaLicense);
		this.add(scp);
		this.add(pnlSouth, BorderLayout.SOUTH);
		this.setTitle("License");
		
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DLGLicense.this.dispose();
			}
		});
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				txaLicense.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
			}
		});
	}

	private ByteArrayOutputStream getLicenseText(){
		try {
			ByteArrayOutputStream strBuf = new ByteArrayOutputStream();
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("license.txt");
			byte[] buf = new byte[1024];
			int count = is.read(buf);
			while(count != -1) {
				strBuf.write(buf, 0, count);
				count = is.read(buf);
			}
			return strBuf;
		} catch(IOException ex) {
			return null;
		}
	}
	
	public static final void open(Dialog owner) {
		DLGLicense dlg = new DLGLicense(owner);
		dlg.setModal(true);
		dlg.setSize(500, 300);
		GuiUtils.center(owner, dlg);
		dlg.setVisible(true);
	}
}
