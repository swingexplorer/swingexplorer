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

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;

import org.swingexplorer.Log;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class MdlProperties extends AbstractTableModel {

	Object bean;	
	String[][] properties = new String[0][0];
	String[] colNames = new String[] {"name", "value"};
	
    // properties shown first of all at the beginning
    HashSet<String> firstKeys = new HashSet<String>(Arrays.asList(new String[] {
                    "size", "opaque", "class", "constraints", "location", "locationOnScreen", "visible", "layout", "border", "borderInsets"            
                }));
    
    
    
	@SuppressWarnings("rawtypes")
	public Map describe(Object bean) {
		if(bean == null) {
			return new HashMap();
		}
		
		Method[] methods = bean.getClass().getMethods();
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(Method m : methods) {
			if(m.getParameterTypes().length > 0) {
				continue;
			}
			
			// construct property name
			String propName = null;
			if(m.getName().startsWith("get")) {
				propName = m.getName().substring(3); 
			} else if(m.getName().startsWith("is")) {
				propName = m.getName().substring(2); 
			}
            
            if(propName == null || propName.length() == 0) {
                continue;
            }
            
            // make first character in lower case
            if(propName.length() == 1) {
                propName = propName.substring(0, 1).toLowerCase();
            } else {
                propName = propName.substring(0, 1).toLowerCase() + propName.substring(1);
            }

			// obtain property value
			if(propName != null) {
				Object result;
				try {
					result = m.invoke(bean, new Object[0]);
					String strRes = valueToString(result);
					map.put(propName, strRes);
				} catch (Exception e) {} 
			}
		}
        
        if(bean instanceof JComponent) {
            Border border =((JComponent)bean).getBorder();
            if(border != null) {
                Insets insets = border.getBorderInsets((JComponent)bean);
                map.put("borderInsets", insets.toString());
            }
            
            try {
	            map.put("constraints", constraintsAsString((JComponent)bean));
            } catch(Exception ex) {
            	// if exception accures we do not show this property
            	Log.general.debug(ex.getMessage(), ex);
            }
        }
		return map;
	}
	
	private String constraintsAsString(JComponent bean) throws Exception {
		Container parent = bean.getParent();
        LayoutManager layout = parent.getLayout();
        Method methGetConstraints = layout.getClass().getMethod("getConstraints", new Class[]{Component.class});
        Object result = methGetConstraints.invoke(layout, new Object[]{bean});
        
        if(result instanceof GridBagConstraints) {
        	GridBagConstraints gbc = (GridBagConstraints)result;
        	
        	String strValue =
        	"gridx: " + gbc.gridx + ", "
        	+ "gridy:" + gbc.gridy + ", "
        	+ "gridwidth:" + gbc.gridwidth + ", "
        	+ "gridheight:" + gbc.gridheight + ", "
        	+ "weightx:" + gbc.weightx + ", "
        	+ "weighty:" + gbc.weighty + ", "
        	+ "anchor:" + gbc.anchor + ", "
        	+ "fill:" + gbc.fill + ", "
        	+ "insets:" + gbc.insets + ", "
        	+ "ipadx:" + gbc.ipadx + ", "
        	+ "ipady:" + gbc.ipady + "";
        	return strValue;
        }
        return "" + result;
	}
	
    private String valueToString(Object value) {
        if(value == null) {
            return "null";
        }
//        if(value instanceof Dimension) {
//            Dimension dimVal = (Dimension)value;
//            return "width: " + dimVal.width + " height: " + dimVal.height;
//        }
//        if(value instanceof Point) {
//            Point ptVal = (Point)value;
//            return "x: " + ptVal.x + " y: " + ptVal.y;
//        }
        
        if(value instanceof Object[]) {
            // display arrays in more readable form
            Object[] casted = (Object[])value;
            StringBuilder buf = new StringBuilder();
            
            buf.append("size: ");
            buf.append(casted.length);
            
            // show first 5 elements
            buf.append(" [");
            
            int i;
            for(i = 0; i < 5 && i < casted.length; i ++) {
                buf.append(casted[i]);
                buf.append(",");
            }
            if(casted.length != 0) {
                buf.delete(buf.length() - 1, buf.length());
            }
            
            // do not display all
            if(casted.length > i) {
                buf.append(",...");
            }
            buf.append("]");
            return buf.toString();
        }
        return value.toString();
    }

	public void setBean(Object _bean) {
		bean = _bean;
		
		try {
			Map props = describe(bean);
            
            
			properties = new String[props.size()][2];
			int i = 0;
			for(Object key : props.keySet()) {
				properties[i][0] = (String)key;
				properties[i][1] = (String)props.get(key);
				i++;
			}
            
            Arrays.sort(properties, new Comparator<String[]>() {
                public int compare(String[] o1, String[] o2) {
                    String key1 = o1[0];
                    String key2 = o2[0];
                    
                    if(firstKeys.contains(key1) && !firstKeys.contains(key2)) {
                        return -1;
                    }
                    if(!firstKeys.contains(key1) && firstKeys.contains(key2)) {
                        return 1;
                    }
                    return key1.compareTo(key2);
                }
            });
		} catch (Exception e) {
			properties = new String[0][0];
			e.printStackTrace();
			return;
		} finally {
			fireTableDataChanged();
		}
	}
    
	
	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}
	
	public Object getBean() {
		return bean;
	}
	
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return properties.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return properties[rowIndex][columnIndex];
	}
}
