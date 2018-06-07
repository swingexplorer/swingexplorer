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

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class PNLGuiDisplay extends JComponent {

	private MdlSwingExplorer model;

	private ModelListener modelListener = new ModelListener();
	
	
	public PNLGuiDisplay() {
	}

	@Override
	protected void paintComponent(Graphics g_old) {

		Graphics2D g = (Graphics2D) g_old.create();

		if (model == null) {
			g.drawString("No model set", 0, 20);
			return;
		}
		
		ImageIcon displayedComponentImage = model.getDisplayedComponentImage();
		if (displayedComponentImage == null) {
			g.drawString("Displayable component is not selected", 0, 20);
			return;
		}

		Graphics2D gScaled =  (Graphics2D)g.create();
        gScaled.scale(model.getDisplayScale(), model.getDisplayScale());
		displayedComponentImage.paintIcon(this, gScaled, 0, 0);

		// paint all selected components
		for (Component comp : model.getSelectedComponents()) {
			
			if(!SwingUtilities.isDescendingFrom(comp, model.getDisplayedComponent())) {
				continue;
			}
			
			Rectangle selRect = translateComponentBoundsToDisplay(comp);
			if (selRect != null) {
				paintSelection((Graphics2D)g_old, selRect, getBorderInsets(comp));
			}
		}

		// paint current component		
		Component comp = model.getCurrentComponent();		
		if (comp != null) {
			Rectangle compRect = translateComponentBoundsToDisplay(comp);
			Rectangle prefSizeRect = translateComponentPreferredSizeToDisplay(comp);
            Insets translatedInsets = translateBorderInsetsToDisplay(getBorderInsets(comp));
			paintCurrent((Graphics2D)g_old, compRect, prefSizeRect, translatedInsets);			
			
		}
		
		// measure line
		paintMeasureLine((Graphics2D)g_old, model.getMeasurePoint1(), model.getMouseLocation());

		g.dispose();
	}

	private Insets getBorderInsets(Component comp) {
		if(comp instanceof JComponent) {
			JComponent casted = (JComponent)comp;
			Border border = casted.getBorder();
			if(border != null) {
				return border.getBorderInsets(casted);
			}
		}
		return null;
	}
	
	private void paintCurrent(Graphics2D g, Rectangle compRect, Rectangle prefSizeRect, Insets borderInsets) {
		g = (Graphics2D) g.create();

		Options settings = model.getOptions();
		g.setColor(settings.getComponentIncludingBorderColor());
		g.setStroke(settings.getCurrentStroke());
		g.drawRect(compRect.x, compRect.y, compRect.width, compRect.height);
		
		if(model.getOptions().isDisplayPreferredSize()) {
			g.setColor(settings.getPreferredSizeColor());
			g.setStroke(settings.getPreferredSizeStroke());
			g.drawRect(prefSizeRect.x, prefSizeRect.y, prefSizeRect.width, prefSizeRect.height);
//			g.drawLine(prefSizeRect.x, prefSizeRect.y + prefSizeRect.height, prefSizeRect.x + prefSizeRect.width, prefSizeRect.y + prefSizeRect.height);
//			g.drawLine(prefSizeRect.x + prefSizeRect.width, prefSizeRect.y + prefSizeRect.height, prefSizeRect.x + prefSizeRect.width, prefSizeRect.y);
		}
		
		if(borderInsets != null) {

			Paint paint = new TexturePaint(settings.getComponentWithoutBorderTexture(), new Rectangle(0, 0, 4, 4));
			g.setPaint(paint);
			g.drawRect(compRect.x + borderInsets.left, compRect.y + borderInsets.top, 
					compRect.width - borderInsets.left - borderInsets.right, 
					compRect.height  - borderInsets.top - borderInsets.bottom);
		}

		g.dispose();
	}

	protected void paintSelection(Graphics2D g, Rectangle selRect, Insets borderInsets) {
		g = (Graphics2D) g.create();

		Options settings = model.getOptions();
		g.setColor(settings.getSelectedColor());
		g.setStroke(settings.getSelectedStroke());
		g.fillRect(selRect.x, selRect.y, selRect.width, selRect.height);

		g.dispose();
	}
	

	protected void paintMeasureLine(Graphics2D g, Point p1, Point p2) {

		if (p1 == null || p2 == null) {
			return;
		}
        
		g = (Graphics2D) g.create();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Options settings = model.getOptions();
		int size = settings.getMeasurePointSize();

		g.setColor(settings.getMeasureLineColor());

		if (p1 != null) {
			paintMeasurePoint(g, p1, size);
		}
		if (p2 != null) {
			paintMeasurePoint(g, p2, size);
		}

		if (p1 != null && p2 != null && !p1.equals(p2)) {
			g.setStroke(settings.getMeasureLineStroke());
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
			
			int distance = (int)(p1.distance(p2)/model.getDisplayScale());
			String txt = "" + distance + " px";
			
			
			Font font = g.getFont().deriveFont(Font.BOLD);
			g.setFont(font);
			Rectangle2D fontBounds = font.getStringBounds(txt, g.getFontRenderContext());
			fontBounds.setFrame(fontBounds.getX() - 1, fontBounds.getY() - 1 , fontBounds.getWidth() + 2, fontBounds.getHeight() + 2);
			
			int xAvg = (p1.x + p2.x)/2;
			int yAvg = (p1.y + p2.y)/2;
            
            double dX = p1.x - p2.x ;
            double dY = p1.y - p2.y;
            double K = 20/p1.distance(p2);
            
            double x = xAvg - K*dY;
            double y = yAvg + K*dX;
            
            Rectangle fontRect = new Rectangle(
                            (int)(x + fontBounds.getX()), 
                            (int)(y + fontBounds.getY()), 
                            (int)(fontBounds.getWidth()), 
                            (int)(fontBounds.getHeight()));
            
            
            Point minDelta = calcMinDelta(x, y, fontRect.getX(), fontRect.getY(), fontRect.getWidth(), fontRect.getHeight(), 10);
            fontRect.x -= minDelta.x;
            fontRect.y -= minDelta.y;
            
            fontRect = adjustFontRect(p1, p2, fontRect, 10);
            
            if(!getVisibleRect().contains(fontRect)) {
                x = xAvg + K*dY;
                y = yAvg - K*dX;
                
                fontRect = new Rectangle(
                                (int)(x + fontBounds.getX()), 
                                (int)(y + fontBounds.getY()), 
                                (int)(fontBounds.getWidth()), 
                                (int)(fontBounds.getHeight()));
                
                minDelta = calcMinDelta(x, y, fontRect.getX(), fontRect.getY(), fontRect.getWidth(), fontRect.getHeight(), 10);
                fontRect.x -= minDelta.x;
                fontRect.y -= minDelta.y;
                fontRect = adjustFontRect(p1, p2, fontRect, 5);
            }
            
            x = fontRect.x;
            y = fontRect.y;
			g.setStroke(new BasicStroke(1));
			g.setColor(settings.getHintBackground());
			g.fill(fontRect);
			g.setColor(settings.getHintForeground());
			g.draw(fontRect);
			g.drawString(txt, fontRect.x - (int)fontBounds.getX(), fontRect.y - (int)fontBounds.getY());
		}

		g.dispose();
	}
    
    
    Rectangle adjustFontRect(Point p1, Point p2, Rectangle rect, int minDist) {
        int xAvg = (p1.x + p2.x)/2;
        int yAvg = (p1.y + p2.y)/2;
        int curDist = Integer.MAX_VALUE;
        Rectangle curRect = rect.getBounds();
        rect = rect.getBounds();
        
        if(!rect.intersectsLine(p1.x, p1.y, p2.x, p2.y)) {
            curDist = distance(rect, xAvg, yAvg);
            if(minDist < curDist) {
                curRect = rect.getBounds();
            }
        }
        rect.setLocation(rect.x, rect.y + rect.height);
        if(!rect.intersectsLine(p1.x, p1.y, p2.x, p2.y)) {
            int newDist = distance(rect, xAvg, yAvg);
            if(newDist < curDist && minDist < newDist) {
                curRect = (Rectangle)rect.getBounds();
                curDist = newDist;
            }
        }
        rect.setLocation(rect.x - rect.width, rect.y);
        if(!rect.intersectsLine(p1.x, p1.y, p2.x, p2.y)) {
            int newDist = distance(rect, xAvg, yAvg);
            if(newDist < curDist && minDist < newDist) {
                curRect = (Rectangle)rect.getBounds();
                curDist = newDist;
            }
        }
        rect.setLocation(rect.x, rect.y - rect.height);
        if(!rect.intersectsLine(p1.x, p1.y, p2.x, p2.y)) {
            int newDist = distance(rect, xAvg, yAvg);
            if(newDist < curDist && minDist < newDist) {
                curRect = (Rectangle)rect.getBounds();
                curDist = newDist;
            }
        }
        return curRect;
    }
    
    int distance(Rectangle r, int x, int y) {
        int dist = (int)Point.distance(r.x, r.y, x, y);
        dist = Math.min((int)Point.distance(r.x + r.width, r.y, x, y), dist);
        dist = Math.min((int)Point.distance(r.x + r.width, r.y + r.height, x, y), dist);
        dist = Math.min((int)Point.distance(r.x, r.y + r.height, x, y), dist);
        return dist;
    }
    
    
    
    Point calcMinDelta(double x, double y, double rectX, double rectY, double rectWidth, double rectHeight, int minDist) {
        Point delta = new Point();
        
        double d1 = Point.distance(x, y, rectX, rectY);
        delta.x =   (int)((rectX - x) * (1 - minDist/d1)); 
        delta.y =   (int)((rectY - y) * (1 - minDist/d1));
        double curMin = d1;
        
        double d2 = Point.distance(x, y, rectX + rectWidth, rectY);
        if(d2 < curMin) {
            delta.x =   (int)((rectX + rectWidth - x) * (1 - minDist/d2)); 
            delta.y =   (int)((rectY - y) * (1 - minDist/d2));
            curMin = d2;
        }
        double d3 = Point.distance(x, y, rectX + rectWidth, rectY + rectHeight);
        if(d3 < curMin) {
            delta.x =   (int)((rectX + rectWidth - x) * (1 - minDist/d3)); 
            delta.y =   (int)((rectY + rectHeight - y) * (1 - minDist/d3));
            curMin = d3;
        }
        double d4 = Point.distance(x, y, rectX, rectY + rectHeight);
        if(d4 < curMin) {
            delta.x =   (int)((rectX - x) * (1 - minDist/d4)); 
            delta.y =   (int)((rectY + rectHeight - y) * (1 - minDist/d4));
            curMin = d4;
        }
        return delta;
    }
    

	protected void paintMeasurePoint(Graphics2D g, Point p, int size) {
		int halfSize = size / 2;
		g.drawLine(p.x, p.y, p.x, p.y + halfSize);
		g.drawLine(p.x, p.y, p.x + halfSize, p.y);
		g.drawLine(p.x, p.y, p.x - halfSize, p.y );
		g.drawLine(p.x, p.y, p.x, p.y - halfSize);
	}

	class ModelListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			String propName = evt.getPropertyName();
			if ("displayedComponentImage".equals(propName)) {
				revalidate();
				repaint();
			} else if ("currentComponent".equals(propName)) {
				repaint();
			} else if ("selectedComponents".equals(propName)) {
				repaint();
			} else if ("measurePoint1".equals(propName)) {
				repaint();
			} else if ("measurePoint2".equals(propName)) {
				repaint();
			} else if ("displayScale".equals(propName)) {
				revalidate();
				repaint();
			} else if ("mouseLocation".equals(propName)) {
				repaint();
			}
		}
	}

	public void setModel(MdlSwingExplorer model) {

		if (model == this.model) {
			return;
		}

		if (this.model != null) {
			this.model.removePropertyChangeListener(modelListener);
		}
		if (model != null) {
			model.addPropertyChangeListener(modelListener);
		}
		this.model = model;
		repaint();
	}


	public Dimension getPreferredSize() {

		if(model == null) {
			return super.getPreferredSize();
		}
		
		ImageIcon displayedComponentImage = model.getDisplayedComponentImage();
		if (displayedComponentImage == null) {
			return new Dimension(0, 0);
		}

		double scale = model.getDisplayScale();
		return new Dimension((int) Math.round(scale
				* displayedComponentImage.getIconWidth()), (int) Math
				.round(scale * displayedComponentImage.getIconHeight()));
	}

	// calculate rectangle corresponding to given component
	private Rectangle translateComponentBoundsToDisplay(Component comp) {
		if(comp == null) {
			return null;
		}
	
		Point translated = calculateLocation(comp);

		return new Rectangle((int)(translated.x*model.getDisplayScale()), 
                            (int)(translated.y*model.getDisplayScale()), 
                            (int)(comp.getWidth()*model.getDisplayScale()), 
                            (int)(comp.getHeight()*model.getDisplayScale()));
	}
	
	private Rectangle translateComponentPreferredSizeToDisplay(Component comp) {
		if(comp == null) {
			return null;
		}
		
		Point translated = calculateLocation(comp);

		Dimension prefSize = comp.getPreferredSize();
		return new Rectangle((int)(translated.x*model.getDisplayScale()), 
                            (int)(translated.y*model.getDisplayScale()), 
                            (int)(prefSize.getWidth()*model.getDisplayScale()), 
                            (int)(prefSize.getHeight()*model.getDisplayScale()));
	}
    
    private Insets translateBorderInsetsToDisplay(Insets insets) {
        if(insets == null) {
            return null;
        }
        insets.top = (int)(insets.top*model.getDisplayScale());
        insets.left = (int)(insets.left*model.getDisplayScale());
        insets.bottom = (int)(insets.bottom*model.getDisplayScale());
        insets.right = (int)(insets.right*model.getDisplayScale());
        return insets;
    }
	
	Point calculateLocation(Component comp) {
		
		int x = 0;
		int y = 0;
		
		Component displayedComponent = model.getDisplayedComponent();
		
		while( comp != null && !(comp instanceof Window) && comp != displayedComponent) {
			x = comp.getX() + x;
			y = comp.getY() + y;
			comp = comp.getParent();
		}
		return new Point(x, y);
	}
	
	public Component getDisplayedComponentAt(Point location) {
		Component comp = model.getDisplayedComponent();
		if(comp != null) {
			Component over = SwingUtilities.getDeepestComponentAt(comp, 
					(int)(location.getX()/model.getDisplayScale()), 
					(int)(location.getY()/model.getDisplayScale()));
			
			
			// this is hack to select layered panel instead of glass panel
			// in the JInternalFrame because SwingUtilities.getDeepestComponentAt
			// selects glass pane by default
			if(over != null && over.getParent() instanceof JRootPane) {
				JRootPane rootPane = (JRootPane)over.getParent();
				if(rootPane.getParent() instanceof JInternalFrame) {
					// if we detected that it is inside internal frame then return
					// layered pane
					return rootPane.getLayeredPane();
				}
			}
			
			return over;
		}
		return null;
	}
	
	
}

