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
package org.swingexplorer.beans;

import java.awt.Color;
import java.awt.Stroke;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Saves/loads java beans from/to Properties objects.
 * @author  Maxim Zakharenkov
 */
public class BeanSaver {

	private HashMap<Class<?>, Converter<?>> converters;
	
	public BeanSaver() {
		converters = new HashMap<Class<?>, Converter<?>>();
		converters.put(Boolean.class, new BooleanConverter());
		converters.put(boolean.class, new BooleanConverter());
		converters.put(Integer.class, new IntegerConverter());
		converters.put(int.class, new IntegerConverter());
		converters.put(Stroke.class, new StrokeConverter());
		converters.put(Color.class, new ColorConverter());
		converters.put(int[].class, new IntArrayConverter());
	}
	
	public void save(Object bean, Map<String, String> props) {
		Map<String, Method> getters  = getAllAnnotatedPropertyGetters(bean.getClass());
		for(String property: getters.keySet()) {
			Method method = getters.get(property);
			String strValue = getStringValue(bean, method);
			props.put(property, strValue);
		}
	}
	
	public void load(Object bean, Map<String, String> props) {
		load(bean, props, false);
	}
	
	public void load(Object bean, Map<String, String> props, boolean ignoreError) {
		resetToDefaults(bean);
		for(Object property : props.keySet()) {
			String strProperty = (String)property;
			setStringValue(bean, strProperty, props.get(strProperty), ignoreError);
		}
	}

	private void setStringValue(Object bean, String property, String strValue, boolean ignoreError) {
		Method setter = findSetter(bean.getClass(), property);
		if(setter == null && ignoreError) {
			return;
		}
		
		// invoke setter
		Class<?> type = setter.getParameterTypes()[0];
		Converter<?> converter = findConverter(type);
		try {
			Object value = converter.fromString(strValue);
			setter.invoke(bean, value);
		} catch(ParseException ex) {
			if(ignoreError) {
				return;
			}
			throw new RuntimeException("Can not convert string value \"" + strValue + "\" to  " + type, ex);
		} catch(Exception ex) {
			if(ignoreError) {
				return;
			}
			throw new RuntimeException("Error setting property \"" + property + "\" to \"" + strValue, ex);
		}
	}

	/**
	 * Reset all bean's properties to their default values.
	 * @param bean to reset propertie for
	 */
	public void resetToDefaults(Object bean) {
		Map<String, Method> getters = getAllAnnotatedPropertyGetters(bean.getClass());
		try {
			for(String property : getters.keySet()) {
				
				Method getter = getters.get(property);
				Object defaultValue = getDefaultValueByGetter(getter);
				
				Method setter = findSetter(bean.getClass(), property);
				
				setter.invoke(bean, new Object[]{defaultValue});
			}
		}catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Reset bean's property to default value
	 * @param bean
	 * @param property
	 */
	public void resetToDefault(Object bean, String property) {
		Object defaultValue = getDefaultValue(bean.getClass(), property);
		try {
			Method setter = findSetter(bean.getClass(), property);
			setter.invoke(bean, defaultValue);
		} catch (Exception e) {
			throw new RuntimeException("Error invoking setter of the property \"" + property + "\"", e);
		}
	}
	
	/**
	 * Returns property value by its name
	 * @param bean
	 * @param property
	 * @return
	 */
	public Object getValue(Object bean, String property) {
		Method method = findGetter(bean.getClass(), property);
		if(method == null) {
			throw new RuntimeException("Getter for \"" + property + "\" not found ");
		}
		try {
			return method.invoke(bean, new Object[0]);
		} catch (Exception e) {
			throw new RuntimeException("Error invoking getter for \"" + bean.getClass() + " bean's  property \"" + property + "\"");
		}
	}
	
	/**
	 * Sets porperty value by property name
	 * @param bean
	 * @param property
	 * @param value
	 */
	public void setValue(Object bean, String property, Object value) {
		Method method = findSetter(bean.getClass(), property);
		if(method == null) {
			throw new RuntimeException("Setter for \"" + property + "\" not found ");
		}
		try {
			method.invoke(bean, new Object[] {value});
		} catch (Exception e) {
			throw new RuntimeException("Error invoking setter for \"" + bean.getClass() + " bean's  property \"" + property + "\"");
		}
	}
	
	/**
	 * Returns default value of bean's property which is declared in the annotation.
	 * @param bean
	 * @param property
	 * @return
	 */
	public Object getDefaultValue(Object bean, String property) {
		return getDefaultValueByClass(bean.getClass(), property);
	}
	
	/**
	 * Returns default value of bean's class property which is declared in the annotation.
	 * @param beanClass
	 * @param property
	 * @return
	 */
	public Object getDefaultValueByClass(Class<?> beanClass, String property) {
		Method method = findGetter(beanClass, property);
		if(method == null) {
			throw new RuntimeException("Canb not find getter for the property \"" + property +"\"");
		}
		return getDefaultValueByGetter(method);
	}
	
	private Object getDefaultValueByGetter(Method method) {
		Property annotation = method.getAnnotation(Property.class);
		
		String strDefaultValue = annotation.defaultValue();
		
		// find converter and convert string annotation to value
		Converter<?> converter = findConverter(method.getReturnType());
		if(converter == null) {
			throw new RuntimeException("Can not find converter for the property \"" + method.getName() +"\" of the \"" + method.getReturnType());
		}
		try {
			return converter.fromString(strDefaultValue);
		} catch (ParseException e) {
			throw new RuntimeException("Invalid default value \"" + strDefaultValue + "\" is specified for the property \"" + method.getName() + "\". Converter can not convert it to object.", e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private String getStringValue(Object bean, Method getter) {
		Object value = null;
		try {
			value = getter.invoke(bean);
		} catch (Exception e) {
			throw new RuntimeException("Error invoking getter \"" + getter.getName() + "\"", e);
		}
		@SuppressWarnings("rawtypes")
		Converter converter = findConverter(getter.getReturnType());
		return converter.toString(value);
	}
	
	private Map<String, Method> getAllAnnotatedPropertyGetters(Class<?> beanClass) {
		HashMap<String, Method> list = new HashMap<String, Method>();
		Method[] allMethodArray = beanClass.getMethods();
		for(Method method:allMethodArray) {
			Property property = method.getAnnotation(Property.class);
			if(property != null) {
				String propertyName = getPropertyByGetter(method);
				list.put(propertyName, method);
			}
		}
		return list;
	}
	
	
	private String getPropertyByGetter(Method getter) {
		String methName = getter.getName();
		if(methName.startsWith("is")) {
			return methName.substring(2, 3).toLowerCase() + methName.substring(3);
		}
		if(methName.startsWith("get")) {
			return methName.substring(3, 4).toLowerCase() + methName.substring(4);
		}
		throw new IllegalArgumentException("Invalid getter method " + getter);
	}
	
	private Method findSetter(Class<?> beanClass, String property) {
		Method getter = findGetter(beanClass, property);
		if(getter == null) {
			return null;
		}
		Class<?> type = getter.getReturnType();
		
		// determine property type by getter or is method
		String first = property.substring(0, 1);
		String rest = property.substring(1);
		String setterName = "set" + first.toUpperCase() + rest;
		
		try {
			Method setter = beanClass.getMethod(setterName, new Class<?>[] {type});
			return setter;
		} catch (Exception e) {
			return null;
		}
	}
	
	private Method findGetter(Class<?> beanClass, String property) {
		// determine property type by getter or is method
		String first = property.substring(0, 1);
		String rest = property.substring(1);
		String getter = "get" + first.toUpperCase() + rest;
		Method methGetter;
		try {
			try {
				methGetter = beanClass.getMethod(getter);
			} catch (NoSuchMethodException e) {
				getter = "is" + first.toUpperCase() + rest;
				methGetter = beanClass.getMethod(getter);
			}
			return methGetter;
		} catch (Exception ex) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> Converter<T> findConverter(Class<T> c) {
		return (Converter<T>)converters.get(c);
	}
}
