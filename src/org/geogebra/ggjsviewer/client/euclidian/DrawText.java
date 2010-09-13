/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.ggjsviewer.client.euclidian;

import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.GeoText;
import org.geogebra.ggjsviewer.client.kernel.gawt.Font;
import org.geogebra.ggjsviewer.client.kernel.gawt.Rectangle;
import org.geogebra.ggjsviewer.client.main.Application;

import com.google.gwt.widgetideas.graphics.client.Color;



/**
 *
 * @author  Markus
 * @version 
 */
public final class DrawText extends Drawable {
	 
	//private static final int SELECTION_DIAMETER_ADD = 4;     
	//private static final int SELECTION_OFFSET = SELECTION_DIAMETER_ADD / 2;
	       
    private GeoText text;    
    boolean isVisible, isLaTeX;     
    private int fontSize = -1;
    private int fontStyle = -1;
    private boolean serifFont;
    private Font textFont;
    private GeoPoint loc; // text location
    
    //private Image eqnImage;
    private int oldXpos, oldYpos;
    private boolean needsBoundingBoxOld;
    
    /** Creates new DrawText */
    public DrawText(EuclidianView view, GeoText text) {      
    	this.view = view;          
        this.text = text;
        geo = text;
        
        textFont = view.fontPoint;
        //AG needed for fontStyle and fontSize
       
        
        // this is needed as (bold) LaTeX texts are created with isLaTeX = false at this stage
        updateStrokes(text);
          
        update();
    }
    
    final public void update() {     
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible 
        		&& !text.isNeedsUpdatedBoundingBox()) // needed for Corner[Element[text
        	return;          
        
        if (isLaTeX) // needed eg for \sqrt
        	updateStrokes(text);
        
        if (isLaTeX) // needed eg for \sqrt
        	updateStrokes(text);
        
        String newText = text.getTextString();
        boolean textChanged = labelDesc == null || 
        	!labelDesc.equals(newText) || 
        	isLaTeX != text.isLaTeX() ||
        	text.isNeedsUpdatedBoundingBox() != needsBoundingBoxOld;
		labelDesc = newText;
		isLaTeX = text.isLaTeX();	
		needsBoundingBoxOld = text.isNeedsUpdatedBoundingBox();
				
        		
        // compute location of text		
		if (text.isAbsoluteScreenLocActive()) {
			xLabel = text.getAbsoluteScreenLocX();
			yLabel = text.getAbsoluteScreenLocY(); 
		} else {
			loc = (GeoPoint) text.getStartPoint();
	        if (loc == null) {
				xLabel = (int) view.xZero;
				yLabel = (int) view.yZero;
	        } else {
	        	if (!loc.isDefined()) {
	        		isVisible = false;
	        		return;
	        	}
				xLabel = view.toScreenCoordX(loc.inhomX);
				yLabel = view.toScreenCoordY(loc.inhomY);        	
	        }
	        xLabel += text.labelOffsetX;
			yLabel += text.labelOffsetY; 
			
		}        
		
		boolean positionChanged = xLabel != oldXpos || yLabel != oldYpos;
		oldXpos = xLabel;
		oldYpos = yLabel;
		
		/*
		// use hotEqn for LaTeX
		if (isLaTeX && eqn == null && !JarManager.JSMATHTEX_LOADED) {
				eqn = new sHotEqn();
				eqn.setDoubleBuffered(false);
				eqn.setEditable(false);	
				eqn.removeMouseListener(eqn);
				eqn.removeMouseMotionListener(eqn);				
				eqn.setDebug(false);
				eqn.setOpaque(false);	
				//eqn.setFont(view.getFont());		
				
				eqn.setFontname(Application.STANDARD_FONT_NAME);
				setEqnFontSize();																												
				view.add(eqn);					
		}*/
		
		boolean fontChanged = doUpdateFontSize();				
			/*
		// avoid unnecessary updates of LaTeX equation
		if (isLaTeX) {
			
			if (JarManager.JSMATHTEX_LOADED)
			{
				TeXFormula formula;
				Icon icon;
				try{
					formula = new TeXFormula(labelDesc);
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
				}
				catch (Exception e)
				{
					formula = new TeXFormula("LaTeXerror");
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
					
				}

				labelRectangle.setBounds(xLabel, yLabel, icon.getIconWidth(), icon.getIconHeight());										
			}
			else
			{
				eqn = new sHotEqn();
				eqn.setDoubleBuffered(false);
				eqn.setEditable(false);	
				eqn.removeMouseListener(eqn);
				eqn.removeMouseMotionListener(eqn);				
				eqn.setDebug(false);
				eqn.setOpaque(false);	
				
				eqn.setFontname(Application.STANDARD_FONT_NAME);
				setEqnFontSize();																												
				eqn.setForeground(geo.getObjectColor());		
				eqn.setBackground(view.getBackground());
	
//			// set equation 
//			eqn.setEquation(labelDesc);
//			
//			// draw equation once to get it's image and size
//			eqn.paintComponent(view.getTempGraphics2D());
//			eqnImage = eqn.getImage();
//			eqnSize = eqn.getSize();
				
				eqnSize = eqn.getSizeof(labelDesc);

				// set bounding box of text component
				labelRectangle.setBounds(xLabel, yLabel, eqnSize.width, eqnSize.height);										
				eqn.setBounds(labelRectangle);		
			}
		} 
		else if (text.isNeedsUpdatedBoundingBox()) {
			// ensure that bounding box gets updated by drawing text once
			drawMultilineText(view.getTempGraphics2D());	
		}*/
		
		
		
		if (text.isNeedsUpdatedBoundingBox() && 
			(textChanged || positionChanged || fontChanged )) 
		{									
			// ensure that bounding box gets updated by drawing text once
			if (isLaTeX) 
			;//AG	drawMultilineLaTeX(view.getTempGraphics2D(textFont), textFont, geo.getObjectColor(),view.getBackground());
			else 
				drawMultilineText(textFont/*AGview.getTempGraphics2D(textFont)*/);	
			
			// Michael Borcherds 2007-11-26 BEGIN update corners for Corner[] command
			double xRW = view.toRealWorldCoordX(labelRectangle.x);
			double yRW = view.toRealWorldCoordY(labelRectangle.y);		
						
			text.setBoundingBox(xRW, yRW,
					labelRectangle.width * view.invXscale,
					-labelRectangle.height * view.invYscale);	
		}
    }

    final public void draw(/*AGGraphics2D g2*/) { 
        if (isVisible) {
        	//AG setting font settings
        	textFont.setFontStyle(text.getFontStyle());
        	textFont.setFontSize(text.getFontSize());
        	
			if (isLaTeX) {
        		//AGg2.setPaint(geo.getObjectColor());				
    			//AGg2.setFont(textFont);    			
    			//AGg2.setStroke(objStroke); // needeg eg for \sqrt
				view.setPaint(geo.getObjectColor());
				view.setFont(textFont);
				view.setStroke(objStroke);
    			//AGdrawMultilineLaTeX(g2, textFont, geo.getObjectColor(),view.getBackground());       
        	} 
        	else {
        		view.setPaint(geo.getObjectColor());
    			view.setFont(textFont);    			
    			drawMultilineText(/*AGg2*/textFont);   
        	}   
			  
			// draw label rectangle
			if (geo.doHighlighting()) {
				view.setStroke(objStroke);
				view.setPaint(Color.LIGHTGREY);		
				view.draw(labelRectangle);         
			}   							
        }
    }
    /*
	final public void drawEquation(Graphics2D g2, int x, int y)
	{
		if (!JarManager.JSMATHTEX_LOADED)
		{
			eqn = new sHotEqn(labelDesc);
			eqn.setDoubleBuffered(false);
			eqn.setEditable(false);	
			eqn.removeMouseListener(eqn);
			eqn.removeMouseMotionListener(eqn);				
			eqn.setDebug(false);
			eqn.setOpaque(false);	
			eqn.setFontname(Application.STANDARD_FONT_NAME);
			setEqnFontSize();																												
			eqn.paintComponent(g2,xLabel,yLabel);					
		}
		else
		{
   	 // TEST CODE FOR JMathTeX
			TeXFormula formula;
			Icon icon;
			try{
				formula = new TeXFormula(labelDesc);
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
			}
			catch (Exception e)
			{
				formula = new TeXFormula("LaTeXerror");
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
				
			}
			icon.paintIcon(new JLabel(), g2, x, y); // component can't be null
		}

	}*/
   
    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
		return super.hitLabel(x, y);				      
    }
    
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(labelRectangle);  
    }
    
    /**
     * Returns false     
     */ 
	public boolean hitLabel(int x, int y) {
		return false;
	}
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 

	private boolean doUpdateFontSize() {
		// text's font size is relative to the global font size
		int newFontSize = view.fontSize + text.getFontSize();		
		int newFontStyle = text.getFontStyle();	
		boolean newSerifFont = text.isSerifFont();
		
		if (textFont.canDisplayUpTo(text.getTextString()) != -1 ||
				fontSize !=newFontSize || fontStyle != newFontStyle || newSerifFont != serifFont) {					
			super.updateFontSize();
			
			fontSize = newFontSize;
			fontStyle = newFontStyle;
			serifFont = newSerifFont;
						
			//if (isLaTeX) {
			//	//setEqnFontSize();				
			//} else {				
				Application app = view.getApplication();
				textFont = app.getFontCanDisplay(text.getTextString(), serifFont, fontStyle, fontSize);				
			//}	
				return true;
		}			
		
		return false;
	}
	
	/*
	private void setEqnFontSize() {		
		
		// hot eqn may only have even font sizes from 10 to 28
		int size = (fontSize / 2) * 2; 
		if (size < 10) 
			size = 10;
		else if (size > 28) 
			size = 28;
		
		eqn.setFontname(serifFont ? "Serif" : "SansSerif");
		eqn.setFontsizes(size, size - 2, size - 4, size - 6);
		eqn.setFontStyle(fontStyle);
		
	}
	*/
	
    /**
	 * Returns the bounding box of this Drawable in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || ((GeoText)geo).isAbsoluteScreenLocActive() || !geo.isEuclidianVisible())
			return null;
		else 
			return labelRectangle;	
	}
}

