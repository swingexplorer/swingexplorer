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
package org.swingexplorer.properties;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import org.swingexplorer.plaf.CustomTableHeaderUI;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class PNLPropertySheet extends JPanel {

	MdlProperties mdlProperties;
	JTable tblProperties;
	JScrollPane scpTable;
	
	public PNLPropertySheet() {
		
		tblProperties = new JTable();
		mdlProperties = new MdlProperties();
		tblProperties.setModel(mdlProperties);
		
		scpTable = new JScrollPane();
		scpTable.setViewportView(tblProperties);
		
		this.setLayout(new BorderLayout());
		this.add(scpTable, BorderLayout.CENTER);
		tblProperties.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblProperties.getColumnModel().getColumn(0).setPreferredWidth(80);
		tblProperties.getColumnModel().getColumn(1).setPreferredWidth(200);
		
		JTableHeader header = tblProperties.getTableHeader();
		header.setUI(new CustomTableHeaderUI());
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		tblProperties.setName(name + ".tblProperties");
	}
    
    @Override
    public void setToolTipText(String text) {
        tblProperties.setToolTipText(text);
    }
    
    @Override
    public String getToolTipText() {
        return tblProperties.getToolTipText();
    }
    
    public void setColumnSize(int column, int size) {
        tblProperties.getColumnModel().getColumn(column).setPreferredWidth(size);
    }
	
	public void setBean(Object _bean) {
		mdlProperties.setBean(_bean);
	}
	
	public Object getBean() {
		return mdlProperties.getBean();
	}
}
