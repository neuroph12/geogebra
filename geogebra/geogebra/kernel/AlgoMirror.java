/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * AlgoMirrorPointPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoMirror extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Mirrorable out;   
    private GeoElement geoIn, geoOut; 
    private GeoLine mirrorLine;   
    private GeoPoint mirrorPoint;      
    private GeoElement mirror;
    
    AlgoMirror(Construction cons, String label,Mirrorable in,GeoPoint p) {
    	this(cons, in, null, p);  
    	 geoOut.setLabel(label);
    }
    
    AlgoMirror(Construction cons, String label,Mirrorable in,GeoLine g) {
    	this(cons, in, g, null);
    	 geoOut.setLabel(label);
    }    
    
    AlgoMirror(Construction cons, Mirrorable in, GeoLine g, GeoPoint p) {
        super(cons);
        //this.in = in;      
        mirrorLine = g;
        mirrorPoint = p;
        
        if (g != null)
        	mirror = g;
		else 
			mirror = p;
              
        geoIn = in.toGeoElement();
        out = (Mirrorable) geoIn.copy();               
        geoOut = out.toGeoElement();                       
        setInputOutput();
                
        compute();                                     
    }           
    
    String getClassName() {
        return "AlgoMirror";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = geoIn; 
        input[1] = mirror;
        
        output = new GeoElement[1];        
        output[0] = geoOut;        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoElement getResult() { 
    	return geoOut; 
    }       

    final void compute() {
        geoOut.set(geoIn);
        
        if (mirror == mirrorLine)
        	out.mirror(mirrorLine);
        else
        	out.mirror(mirrorPoint);
    }       
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(geoIn.getLabel());
        sb.append(' ');        
        sb.append(app.getPlain("mirroredAt"));
        sb.append(' ');
        sb.append(mirror.getLabel());   
        
        return sb.toString();
    }
}
