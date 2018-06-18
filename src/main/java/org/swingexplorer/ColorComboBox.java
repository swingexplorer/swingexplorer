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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;


/**
 *
 * @author  Maxim Zakharenkov
 */
public class ColorComboBox extends JComboBox {

	
	public ColorComboBox() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(Color.BLACK);
		model.addElement(Color.DARK_GRAY);
		model.addElement(Color.GRAY);
		model.addElement(Color.LIGHT_GRAY);
		model.addElement(Color.BLUE);
		model.addElement(Color.MAGENTA);
		model.addElement(Color.CYAN);
		model.addElement(Color.GREEN);
		model.addElement(Color.RED);
		model.addElement(Color.ORANGE);
		model.addElement(Color.YELLOW);
		model.addElement(Color.WHITE);
		setModel(model);
		
		ListCellRenderer renderer = new ColorCellRenderer();
		setRenderer(renderer);
		setEditable(false);
	}
	
	public Color getSelectedColor() {
		return (Color)getSelectedItem();
	}
	
	
	
	static class ColorCellRenderer extends DefaultListCellRenderer {

		Color color;
		boolean selected;
		boolean cellHasFocus;
		
		ColorCellRenderer() {
			setPreferredSize(new Dimension(50, 20));
		}
		
		public Component getListCellRendererComponent(JList _list, Object _value,
				int _index, boolean _isSelected, boolean _cellHasFocus) {
			super.getListCellRendererComponent(_list, _value, _index, _isSelected, _cellHasFocus);
			color = (Color)_value;
			selected = _isSelected;
			cellHasFocus = _cellHasFocus;
			setBackground(color);
			return this;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			if(selected || cellHasFocus) {
				Color selectionBackground = UIManager.getColor("ComboBox.selectionBackground");
				g.setColor(selectionBackground);
			} else {
				g.setColor(Color.WHITE);
			}
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(color);
			g.fillRect(2, 2, getWidth() - 4, getHeight() - 4);
			g.setColor(Color.BLACK);
			g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
		}
	}
	
	
	public static void main(String[] args) {
		javax.swing.JFrame frm =new javax.swing.JFrame();
		frm.setBounds(new java.awt.Rectangle(100, 100, 200, 200));
		ColorComboBox cmb = new ColorComboBox();
		cmb.addItemListener(new java.awt.event.ItemListener(){
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				System.out.println(" Changed " + e);
			}
		});
		frm.add(cmb);
		frm.setVisible(true);
	}
}
