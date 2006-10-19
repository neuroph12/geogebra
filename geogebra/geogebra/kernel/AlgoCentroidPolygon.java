/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;


public class AlgoCentroidPolygon extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPolygon p;  // input
    private GeoPoint centroid; // output                         
        
    AlgoCentroidPolygon(Construction cons, String label,GeoPolygon p) {
        super(cons);
        this.p = p;       
        centroid = new GeoPoint(cons);  
        setInputOutput(); // for AlgoElement
                
        compute();              
        centroid.setLabel(label);         
    }   
    
    String getClassName() {
        return "AlgoCentroidPolygon";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = p;        
        
        output = new GeoElement[1];        
        output[0] = centroid;
        setDependencies(); // done by AlgoElement
    }    
    
    GeoPolygon getPolygon() { return p; }
    GeoPoint getPoint() { return centroid; }    
        
    final void compute() {
        p.calcCentroid(centroid);
    }
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if(app.isReverseLanguage()){//FKH 20040906
          sb.append(p.getLabel());
          sb.append(' ');
          sb.append(app.getPlain("CentroidOf"));
        }else{
        sb.append(app.getPlain("CentroidOf"));
        sb.append(' ');
        sb.append(p.getLabel());        
        }
        
        return sb.toString();
    }
}
