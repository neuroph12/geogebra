/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawSegment
 *
 * Created on 21. 8 . 2003
 */

package geogebra.euclidian;

import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawSegment extends Drawable
implements Previewable {
   
    private GeoLine s;
    private GeoPoint A, B;
       
    boolean isVisible, labelVisible;
    private ArrayList points;
    
    private Line2D.Float line = new Line2D.Float();               
    private double [] coordsA = new double[2];
	private double [] coordsB = new double[2];
    
	// For drawing ticks
	private Line2D.Double [] decoTicks;	
    
	/** Creates new DrawSegment */
    public DrawSegment(EuclidianView view, GeoLine s) {
    	this.view = view;
    	this.s = s;
    	geo = s;
    	        
        update();
    }
    
	/**
	 * Creates a new DrawSegment for preview.     
	 */
	DrawSegment(EuclidianView view, ArrayList points) {
		this.view = view; 
		this.points = points;

		updatePreview();
	} 

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return; 
		labelVisible = geo.isLabelVisible();       
		updateStrokes(s);
		
		A = s.getStartPoint();
        B = s.getEndPoint();
		
        A.getInhomCoords(coordsA);
        B.getInhomCoords(coordsB);
		view.toScreenCoords(coordsA);
		view.toScreenCoords(coordsB);					
		line.setLine(coordsA[0], coordsA[1], coordsB[0], coordsB[1]);        

		// line on screen?		
		if (!line.intersects(0,0, view.width, view.height)) {				
			isVisible = false;			
		}	
		    		    	
		// draw trace
		if (s.trace) {
			isTracing = true;
			Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}			
		
		// if no label and no decoration then we're done
		if (!labelVisible && geo.decorationType == GeoElement.DECORATION_NONE) 
			return;
		
		// calc midpoint (midX, midY) and perpendicular vector (nx, ny)
		double midX = (coordsA[0] + coordsB[0])/ 2.0;
		double midY = (coordsA[1] + coordsB[1])/ 2.0;		
		double nx = coordsA[1] - coordsB[1]; 			
		double ny = coordsB[0] - coordsA[0];		
		double nLength = GeoVec2D.length(nx, ny);			
			
		// label position
        // use unit perpendicular vector to move away from line
        if (labelVisible) {   
        	labelDesc = geo.getLabelDescription();	
        	if (nLength > 0.0) {    		
        		xLabel = (int) (midX + nx * 16 / nLength);
    			yLabel = (int) (midY + ny * 16 / nLength);	
    		} else {
    			xLabel = (int) midX;
    			yLabel = (int) (midY + 16);    			
    		}	        													  			  
			addLabelOffset();        
        }	
        
        // update decoration    		
		//added by Lo�c and Markus BEGIN,
		if (geo.decorationType != GeoElement.DECORATION_NONE && nLength > 0) {	
			if (decoTicks == null) {
				// only create these object when they are really needed
				decoTicks =	new Line2D.Double[6]; // Michael Borcherds 20071006 changed from 3 to 6
				for (int i = 0; i < decoTicks.length; i++)
					decoTicks[i] = new Line2D.Double();
			}
			
			// tick spacing and length.
			double tickSpacing = 2.5 + geo.lineThickness/2d;
			double tickLength =  tickSpacing + 1;	
//			 Michael Borcherds 20071006 start
			double arrowlength = 1.5;
//			 Michael Borcherds 20071006 end
			double vx, vy, factor;
																	
			switch(geo.decorationType){
			case GeoElement.DECORATION_SEGMENT_ONE_TICK:
				// use perpendicular vector to set tick	
				factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - nx, midY - ny,
									 midX + nx, midY + ny);	
				break;
		 	
		 	case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
		 		// vector (vx, vy) to get 2 points around midpoint		
		 		factor = tickSpacing / (2 * nLength);		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
		 		// use perpendicular vector to set ticks			 		
		 		factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX + vx - nx, midY + vy - ny,
									 midX + vx + nx, midY + vy + ny);						
				decoTicks[1].setLine(midX - vx - nx, midY - vy - ny,
						 			 midX - vx + nx, midY - vy + ny);
		 		break;
		 	
		 	case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
		 		// vector (vx, vy) to get 2 points around midpoint				 		
		 		factor = tickSpacing / nLength;		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
		 		// use perpendicular vector to set ticks			 		
		 		factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX + vx - nx, midY + vy - ny,
									 midX + vx + nx, midY + vy + ny);	
				decoTicks[1].setLine(midX - nx, midY - ny,
						 			 midX + nx, midY + ny);
				decoTicks[2].setLine(midX - vx - nx, midY - vy - ny,
			 			 			 midX - vx + nx, midY - vy + ny);
		 		break;
//		 	 Michael Borcherds 20071006 start
			case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
		 		// vector (vx, vy) to get 2 points around midpoint				 		
		 		factor = tickSpacing / (1.5 * nLength);		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
				// use perpendicular vector to set tick	
				factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - arrowlength*vx, midY - arrowlength*vy,
						 midX - arrowlength*vx + arrowlength*(nx + vx), midY - arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[1].setLine(midX - arrowlength*vx, midY - arrowlength*vy,
						 midX - arrowlength*vx + arrowlength*(-nx + vx), midY - arrowlength*vy + arrowlength*(-ny + vy));	
				break;
		 	
		 	case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
		 		// vector (vx, vy) to get 2 points around midpoint		
		 		factor = tickSpacing / (1.5 * nLength);		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
		 		// use perpendicular vector to set ticks			 		
		 		factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - 2*arrowlength*vx, midY - 2*arrowlength*vy,
						 midX - 2*arrowlength*vx + arrowlength*(nx + vx), midY - 2*arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[1].setLine(midX - 2*arrowlength*vx, midY - 2*arrowlength*vy,
						 midX - 2*arrowlength*vx + arrowlength*(-nx + vx), midY - 2*arrowlength*vy + arrowlength*(-ny + vy));	
				
				decoTicks[2].setLine(midX, midY,
						 midX + arrowlength*(nx + vx), midY + arrowlength*(ny + vy));	
				decoTicks[3].setLine(midX, midY,
						 midX + arrowlength*(-nx + vx), midY + arrowlength*(-ny + vy));	
		 		break;
		 	
		 	case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
		 		// vector (vx, vy) to get 2 points around midpoint				 		
		 		factor = tickSpacing / (1.5 * nLength);		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
		 		// use perpendicular vector to set ticks			 		
		 		factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - arrowlength*vx, midY - arrowlength*vy,
						 midX - arrowlength*vx + arrowlength*(nx + vx), midY - arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[1].setLine(midX - arrowlength*vx, midY - arrowlength*vy,
						 midX - arrowlength*vx + arrowlength*(-nx + vx), midY - arrowlength*vy + arrowlength*(-ny + vy));	
				
				decoTicks[2].setLine(midX + arrowlength*vx, midY + arrowlength*vy,
						 midX + arrowlength*vx + arrowlength*(nx + vx), midY + arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[3].setLine(midX + arrowlength*vx, midY + arrowlength*vy,
						 midX + arrowlength*vx + arrowlength*(-nx + vx), midY + arrowlength*vy + arrowlength*(-ny + vy));	
				
				decoTicks[4].setLine(midX - 3*arrowlength*vx, midY - 3*arrowlength*vy,
						 midX - 3*arrowlength*vx + arrowlength*(nx + vx), midY - 3*arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[5].setLine(midX - 3*arrowlength*vx, midY - 3*arrowlength*vy,
						 midX - 3*arrowlength*vx + arrowlength*(-nx + vx), midY - 3*arrowlength*vy + arrowlength*(-ny + vy));	
		 		break;
//		 	 Michael Borcherds 20071006 end
			}    		    		    		
    	}			                                           
    }
	
   
	final public void draw(Graphics2D g2) {
        if (isVisible) {		        	
            if (geo.doHighlighting()) {
                g2.setPaint(s.getSelColor());
                g2.setStroke(selStroke);            
                g2.draw(line);       
            }
            
            g2.setPaint(s.getObjectColor());             
            g2.setStroke(objStroke);            
			g2.draw(line);

			//added by Lo�c BEGIN			
			if (geo.decorationType != GeoElement.DECORATION_NONE){
				g2.setStroke(decoStroke);
				
				switch(geo.decorationType){
				case GeoElement.DECORATION_SEGMENT_ONE_TICK:
					g2.draw(decoTicks[0]);
					break;
					
				case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					break;
					
				case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					break;
// Michael Borcherds 20071006 start
				case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					break;
					
				case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					g2.draw(decoTicks[3]);
					break;
					
				case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					g2.draw(decoTicks[3]);
					g2.draw(decoTicks[4]);
					g2.draw(decoTicks[5]);
					break;
// Michael Borcherds 20071006 end
				}
			}
			//END

			if (labelVisible) {
				g2.setPaint(s.getLabelColor());
				g2.setFont(view.fontLine);
				drawLabel(g2);
            }
        }
    }
    
	final void drawTrace(Graphics2D g2) {
		g2.setPaint(geo.getObjectColor());
		g2.setStroke(objStroke);  
		g2.draw(line);
	}
    
	final public void updatePreview() {		
		isVisible = points.size() == 1;
		if (isVisible) { 
			//	start point
			A = (GeoPoint) points.get(0);						   			
			A.getInhomCoords(coordsA);			                        
			view.toScreenCoords(coordsA);						
			line.setLine(coordsA[0], coordsA[1], coordsA[0], coordsA[1]);                                   			                                            
		}
	}
	
	final public void updateMousePos(int x, int y) {		
		if (isVisible) { 											
			line.setLine(coordsA[0], coordsA[1], x, y);                                   			                                            
		}
	}
    
	final public void drawPreview(Graphics2D g2) {
		if (isVisible) {			            
			g2.setPaint(ConstructionDefaults.colPreview);             
			g2.setStroke(objStroke);            
			g2.draw(line);                        		
		}
	}
	
	public void disposePreview() {	
	}
    
	final public boolean hit(int x,int y) {        
        return line.intersects(x-3, y-3, 6, 6);        
    }
	
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(line.getP1()) &&
    			rect.contains(line.getP2());  
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
    /**
	 * Returns the bounding box of this Drawable in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		else 
			return line.getBounds();	
	}
    
}
