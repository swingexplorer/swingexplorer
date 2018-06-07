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
import java.awt.Graphics;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.swingexplorer.beans.BeanSaver;
import org.swingexplorer.beans.Property;

/**
 * Class holding all parametrizeable properties used in Swing Explorer
 * @author Maxim Zakharenkov
 */
public class Options {

	BeanSaver saver = new BeanSaver();
	
	private BufferedImage componentWithoutBorderTexture = null;
	
    private boolean displayPreferredSize;
    private Color componentIncludingBorderColor;
	private Color selectedColor;
	private Stroke selectedStroke;
	private Color preferredSizeColor;
	private Color hintForeground;
	private Color hintBackground;
	private Color borderInsetColor;
	private Color measureLineColor;
	private int measurePointSize;
	private Stroke measureLineStroke;
	private Stroke preferredSizeStroke;
	private Stroke currentStroke;
	private Color componentWithoutBorderColor;
	private Color gridDarkColor;
	private Color gridBrightColor;
	
	private int verticalDividerLocation;
	private int horizontalDividerLocation;
	private int windowX;
	private int windowY;
	private int windowWidth;
	private int windowHeight;
	private int selectedToolTabIndex;
	private int selectedTreeTabIndex;
	private int eventTabDividerLocation;
	private int[] eventTableColumnSizes;
	private int[] eventDetailTableColumnSizes;
	private int[] propertyTableColumnSizes;
	
	
	
	public Options() {
		reset();
	}
	
	public void reset() {
		saver.resetToDefaults(this);
	}

	@Property(defaultValue="93,113")
	public int[] getEventDetailTableColumnSizes() {
		return eventDetailTableColumnSizes;
	}

	public void setEventDetailTableColumnSizes(int[] eventDetailTableColumnSizes) {
		this.eventDetailTableColumnSizes = eventDetailTableColumnSizes;
	}

	@Property(defaultValue="120,501")
	public int[] getPropertyTableColumnSizes() {
		return propertyTableColumnSizes;
	}

	public void setPropertyTableColumnSizes(int[] propertyTableColumnSizes) {
		this.propertyTableColumnSizes = propertyTableColumnSizes;
	}
	

	@Property(defaultValue="328,83")
	public int[] getEventTableColumnSizes() {
		return eventTableColumnSizes;
	}
	
	public void setEventTableColumnSizes(int[] eventTableColumnSizes) {
		this.eventTableColumnSizes = eventTableColumnSizes;
	}
	
	@Property(defaultValue="356")
	public int getEventTabDividerLocation() {
		return eventTabDividerLocation;
	}

	public void setEventTabDividerLocation(int eventTabDividerLocation) {
		this.eventTabDividerLocation = eventTabDividerLocation;
	}

	@Property(defaultValue="0")
	public int getSelectedToolTabIndex() {
		return selectedToolTabIndex;
	}

	public void setSelectedToolTabIndex(int selectedToolTabIndex) {
		this.selectedToolTabIndex = selectedToolTabIndex;
	}

	@Property(defaultValue="0")
	public int getSelectedTreeTabIndex() {
		return selectedTreeTabIndex;
	}
	
	public void setSelectedTreeTabIndex(int selectedTreeTabIndex) {
		this.selectedTreeTabIndex = selectedTreeTabIndex;
	}

	/** selected component */
	@Property(defaultValue="0,0,255,50")
	public Color getSelectedColor() {
		return selectedColor;
	}
	
	@Property(defaultValue="255,0,0")
	public Color getComponentIncludingBorderColor() {
		return componentIncludingBorderColor;
	}

	@Property(defaultValue="1")
	public Stroke getSelectedStroke() {
		return selectedStroke;
	}

	@Property(defaultValue="1")
	public Stroke getCurrentStroke() {
		return currentStroke;
	}

	/** diameter of measure point */
	@Property(defaultValue="6")
	public int getMeasurePointSize() {
		return measurePointSize;
	}

	@Property(defaultValue="0,0,255")
	public Color getMeasureLineColor() {
		return measureLineColor;
	}
	
	@Property(defaultValue="1")
	public Stroke getMeasureLineStroke() {
		return measureLineStroke;
	}
	
	@Property(defaultValue="0,255,0")
	public Color getBorderInsetColor() {
		return borderInsetColor;
	}
	@Property(defaultValue="255,255,0,150")
	public Color getHintBackground() {
		return hintBackground;
	}

	@Property(defaultValue="0,0,0")
	public Color getHintForeground() {
		return hintForeground;
	}

	@Property(defaultValue="0,0,0")
	public Color getPreferredSizeColor() {
		return preferredSizeColor;
	}

	@Property(defaultValue="1")
	public Stroke getPreferredSizeStroke() {
		return preferredSizeStroke;
	}
	
    /**
	 * Determines if border with preferred size is displayed in the display
	 * area when mouse is over a component.
	 * 
	 * @return
	 */
	@Property(defaultValue="false")
	public boolean isDisplayPreferredSize() {
		return displayPreferredSize;
	}
	
	/** Location of vertical divider between ree and work area */
	@Property(defaultValue="200")
	public int getVerticalDividerLocation() {
		return verticalDividerLocation;
	}
	
	/** Divider location between bottom tabbed pane and display area*/
	@Property(defaultValue="314")
	public int getHorizontalDividerLocation() {
		return horizontalDividerLocation;
	}
	
	@Property(defaultValue="100")
	public int getWindowX() {
		return windowX;
	}

	@Property(defaultValue="100")
	public int getWindowY() {
		return windowY;
	}

	@Property(defaultValue="800")
	public int getWindowWidth() {
		return windowWidth;
	}
	
	@Property(defaultValue="600")
	public int getWindowHeight() {
		return windowHeight;
	}
	
	@Property(defaultValue="0,255,0")
	public Color getComponentWithoutBorderColor() {
		return componentWithoutBorderColor;
	}
	
	@Property(defaultValue="230,230,230")
	public Color getGridDarkColor() {
		return gridDarkColor;
	}

	@Property(defaultValue="255,255,255")
	public Color getGridBrightColor() {
		return gridBrightColor;
	}
	
	
	public void setMeasureLineStroke(Stroke _stroke) {
		measureLineStroke = _stroke;
	}
	
	public BufferedImage getComponentWithoutBorderTexture() {
		return componentWithoutBorderTexture;
	}

	public void setDisplayPreferredSize(boolean _displayPreferredSize) {
		displayPreferredSize = _displayPreferredSize;
	}
	
	public void setComponentIncludingBorderColor(Color currentColor) {
		this.componentIncludingBorderColor = currentColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	public void setSelectedStroke(Stroke selectedStroke) {
		this.selectedStroke = selectedStroke;
	}

	public void setPreferredSizeColor(Color preferredSizeColor) {
		this.preferredSizeColor = preferredSizeColor;
	}

	public void setHintForeground(Color hintForeground) {
		this.hintForeground = hintForeground;
	}

	public void setHintBackground(Color hintBackground) {
		this.hintBackground = hintBackground;
	}

	public void setBorderInsetColor(Color borderInsetColor) {
		this.borderInsetColor = borderInsetColor;
	}

	public void setMeasureLineColor(Color measureLineColor) {
		this.measureLineColor = measureLineColor;
	}

	public void setMeasurePointSize(int measurePointSize) {
		this.measurePointSize = measurePointSize;
	}

	public void setPreferredSizeStroke(Stroke preferredSizeStroke) {
		this.preferredSizeStroke = preferredSizeStroke;
	}

	public void setCurrentStroke(Stroke currentStroke) {
		this.currentStroke = currentStroke;
	}
	
	public void setComponentWithoutBorderColor(Color _componentWithoutBorderColor) {
		componentWithoutBorderColor = _componentWithoutBorderColor;
		
		componentWithoutBorderTexture = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		Graphics g = componentWithoutBorderTexture.createGraphics();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, 4, 4);
		g.setColor(componentWithoutBorderColor);			
		g.fillRect(0, 0, 2, 2);
		g.fillRect(2, 2, 2, 2);
		g.dispose();
	}
	
	public void setVerticalDividerLocation(int dividerLocation) {
		verticalDividerLocation = dividerLocation;
	}

	public void setHorizontalDividerLocation(int dividerLocation) {
		horizontalDividerLocation = dividerLocation;
	}
	
	public void setWindowX(int windowX) {
		this.windowX = windowX;
	}
	
	public void setWindowY(int windowY) {
		this.windowY = windowY;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}
	
	public void setGridDarkColor(Color _gridDarkColor) {
		gridDarkColor = _gridDarkColor;
	}
	
	public void setGridBrightColor(Color _gridBrightColor) {
		gridBrightColor = _gridBrightColor;
	}
	
	/**
	 * Returns property value by name
	 * @param propertyName
	 * @return
	 */
	public Object getValue(String propertyName) {
		return saver.getValue(this, propertyName);
	}
	
	/**
	 * Set property value by property name
	 * @param propertyName
	 * @param value
	 */
	public void setValue(String propertyName, Object value) {
		saver.setValue(this, propertyName, value);
	}
	
	/**
	 * Loads options from file
	 * file is determined by SysUtils.getOptionFilePath method
	 */
	public void load() {
		// load options if they exist from previous executions
        String optionFile = SysUtils.getOptionFilePath(false);
        try {
        	// here we reuse "load" mechanism of properties
        	Properties props = new Properties();
			props.load(new FileInputStream(optionFile));

			// copy properties to map
			HashMap<String, String> map = new HashMap<String, String>();
			for(Map.Entry<Object, Object> entry: props.entrySet()) {
				map.put((String)entry.getKey(), (String)entry.getValue());
			}
			saver.load(this, map);
		} catch (FileNotFoundException e) {
			// do nothing, default options will be used
			Log.general.info("Option file \"" + optionFile + "\" not found. Use default options.");
		} catch(IOException e) {
			Log.general.error("Can not load oprions from file " + optionFile, e);
		}
	}
	
	/**
	 * Saves options to file
	 * file is determined by SysUtils.getOptionFilePath method
	 */
	public void save() {
		// save options
		String optionFile = SysUtils.getOptionFilePath(true);
		
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			saver.save(this, map);
		
			// here we reuse "store" mechanism of properties
			Properties props = new Properties();
			for(Map.Entry<String, String> entry: map.entrySet()) {
				props.put(entry.getKey(), entry.getValue());
			}
			
			props.store(new FileOutputStream(optionFile), "Option file for Swing Explorer");
		} catch (Exception e) {
			// do nothing, default options will be used next time
			Log.general.error("Error saving options to file " + optionFile, e);
		}	
	}
}

