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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class MdlEvents extends AbstractTableModel {

	ArrayList<AWTEvent> events = new ArrayList<AWTEvent>(); 
	Column[] columns = new Column[] {
            new ColParamString(),
            new ColEventSource()
	};
	
	abstract class Column {
		abstract Object getValue(int row);
		abstract String getName();
	}
	
    class ColParamString extends Column {
        @Override
        String getName() {
            return "Event";
        }

        @Override
        Object getValue(int row) {
            AWTEvent event = events.get(row);
            return event.paramString();
        }
        
    }
    
	class ColEvent extends Column {
		@Override
		Object getValue(int row) {
			AWTEvent event = events.get(row);
			if(event instanceof MouseEvent) {
				switch(event.getID()) {
					case MouseEvent.MOUSE_CLICKED: return "Mouse clicked";
					case MouseEvent.MOUSE_DRAGGED: return "Mouse dragged";
					case MouseEvent.MOUSE_ENTERED: return "Mouse entered";
					case MouseEvent.MOUSE_EXITED: return "Mouse exited";
					case MouseEvent.MOUSE_MOVED: return "Mouse moved";
					case MouseEvent.MOUSE_PRESSED: return "Mouse pressed";
					case MouseEvent.MOUSE_RELEASED: return "Mouse released";
					case MouseEvent.MOUSE_WHEEL: return "Mouse wheel";
				}
			}
			if(event instanceof KeyEvent) {
				switch(event.getID()) {
					case KeyEvent.KEY_PRESSED: return "Key pressed";
					case KeyEvent.KEY_RELEASED: return "Key released";
					case KeyEvent.KEY_TYPED: return "Key typed";
				}
			}
			
			return events.get(row).getClass().getSimpleName();
		}

		@Override
		String getName() {
			return "Event class";
		}
	}
	
	class ColEventId extends Column {
		@Override
		Object getValue(int row) {
			return events.get(row).getID();
		}

		@Override
		String getName() {
			return "ID";
		}
	}
	
	class ColEventSource extends Column {
		@Override
		Object getValue(int row) {
			return events.get(row).getSource().getClass().getSimpleName();
		}

		@Override
		String getName() {
			return "Source";
		}
	}
	
	public void addEvent(AWTEvent evt) {
		events.add(evt);
		int insRow = events.size() - 1;
		fireTableRowsInserted(insRow, insRow);
	}
	
	public void clear() {
		events.clear();
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column].getName();
	}
	
	public int getColumnCount() {
		return columns.length;
	}

	public int getRowCount() {
		return events.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return columns[columnIndex].getValue(rowIndex);
	}

	public AWTEvent getEvent(int row) {
		if(events.size() <= row || row < 0) {
			return null;
		}
		return events.get(row);
	}

}
