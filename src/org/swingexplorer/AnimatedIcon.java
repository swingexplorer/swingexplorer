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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class AnimatedIcon implements Icon {
    
    public final Icon EMPTY_ICON = new EmptyIcon();
    JComponent ownerComponent; 
    Icon currentIcon = EMPTY_ICON;
    private ArrayList<Icon> icons = new ArrayList<Icon>();
    private Dimension maxIconSize = new Dimension(0, 0);
    private Timer timer;
    private int iconNumber = 0;
    boolean inProgress = false;
    
    public int getIconHeight() {
        return currentIcon.getIconHeight();
    }

    public int getIconWidth() {
        return currentIcon.getIconWidth();
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        currentIcon.paintIcon(c, g, x, y);
    }

    
    /**
     * Set icons to be animated.
     * @param _icons
     */
    public void setIcons(Icon[] _icons) {
        
        icons.clear();
        
        // calculate maximal icon size and set icons to list
        maxIconSize = new Dimension(0, 0);
        for(int i = 0; i < _icons.length; i++) {
            Icon curIcon = _icons[i] == null ? EMPTY_ICON : _icons[i];
            maxIconSize.height = Math.max(curIcon.getIconHeight(), maxIconSize.height);
            maxIconSize.width = Math.max(curIcon.getIconWidth(), maxIconSize.width);
            
            icons.add(curIcon);
        }
    }
    
    /**
     * Current icons for animation
     * @return
     */
    public Icon[] getIcons() {
        return icons.toArray(new Icon[icons.size()]);
    }
    
    public AnimatedIcon(JComponent _ownerComponent) {
        ownerComponent = _ownerComponent;
        try {
            initActions();
            
            // this is necessary to switch off timer when
            // progress indicator becomes invisible
            ownerComponent.addComponentListener(new ComponentAdapter() {
                public void componentHidden(ComponentEvent e) {
                    if(inProgress) {
                        timer.stop();
                    }
                }
                public void componentShown(ComponentEvent e) {
                    if(inProgress) {
                        timer.start();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }                                
    }
    
    private void initActions(){
        timer = new Timer(100, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if(icons.size() > 0) {
                    iconNumber = (iconNumber + 1) % icons.size();
                    Icon icon = icons.get(iconNumber);
                    currentIcon = new NormalizedIcon(icon);
                    ownerComponent.repaint();
                }
            }            
        });
        timer.setRepeats(true);
    }
    
    /**
     * Specifies time to show one icon.
     * @param interval
     */
    public void setAnimationInterval(int interval) {
        timer.setDelay(interval);
    }
    
    /**
     * Returns current time a single icon is shown
     * @return
     */
    public int getAnimationInterval() {
        return timer.getDelay();
    }
    
    /**
     * Makes progress indicator running.
     * @param inProgress
     */
    public void setInProgress(boolean _inProgress){
        if(inProgress == _inProgress) {
            return;
        }
        inProgress = _inProgress;
        if(inProgress && !timer.isRunning()) {
            timer.start();
        } else {
            timer.stop();
            currentIcon = EMPTY_ICON;
            ownerComponent.repaint();
        }
    }

    /**
     * @return true if is in progress
     */
    public boolean isInProgress(){
        return inProgress;
    }
    
    /**
     * Normalizes wrapped icon to maxIconSize 
     */
    class NormalizedIcon implements Icon {
        
        Icon icon;
        
        NormalizedIcon(Icon _icon) {
            if(_icon == null) {
                throw new NullPointerException("null icon is not allowed");
            }
            icon = _icon;
        }
        
        public int getIconHeight() {
            return maxIconSize.height;
        }
        public int getIconWidth() {
            return maxIconSize.width;
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {

            // calculate shift of icon
            int dx = 0;
            int dy = 0;
            if(icon.getIconHeight() < getIconHeight()) {
                dx = (getIconHeight() - icon.getIconHeight())/2;
            }
            if(icon.getIconWidth() < getIconWidth()) {
                dy = (getIconWidth() - icon.getIconWidth())/2;
            }
            
            // paint icon
            g.translate(dx, dy);
            icon.paintIcon(c, g, x, y);
            g.translate(-dx, -dy);
        }
    }
    
    
    // icon for inactive state
    class EmptyIcon implements Icon {
        public int getIconHeight() {
            return maxIconSize.height;
        }
        public int getIconWidth() {
            return maxIconSize.width;
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
        }
    }
    
//    public static void main(String[] args) {
//        JFrame frm = new JFrame();
//        
//        JTabbedPane tbp = new JTabbedPane();
//        
//        
//        JLabel lbl = new JLabel();
//        AnimatedIcon ico = new AnimatedIcon(lbl);
//        ico.setIcons(Icons.monitor());
//        frm.add(lbl);
//        ico.setInProgress(true);
//        lbl.setIcon(ico);
//        
//        AnimatedIcon ico2 = new AnimatedIcon(tbp);
//        ico2.setIcons(Icons.monitor());
//        tbp.addTab("Title", ico2, lbl);
//        tbp.addTab("Title", new JPanel());
//        ico2.setInProgress(true);
//        
//        frm.add(tbp);
//        
//        
//        frm.setVisible(true);
//        frm.setBounds(100, 100, 200, 200);
//        frm.setVisible(true);
//    }
}
