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


/**
 * Finds all inflection points of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoTurningPointPolynomial extends AlgoRootsPolynomial {
        
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoTurningPointPolynomial(Construction cons, String [] labels, GeoFunction f) {
        super(cons, labels, f);             
    }
    
    String getClassName() {
        return "AlgoTurningPointPolynomial";    
    }
            
    public GeoPoint [] getInflectionPoints() {
        return super.getRootPoints();
    }
    
    final void compute() {              
        if (f.isDefined()) {
            yValFunction = f.getFunction();                                                                    
            
            // roots of second derivative 
            //(roots without change of sign are removed)
            calcRoots(yValFunction, 2);                                                          
        } else {
            curRealRoots = 0;                           
        }                       
                
        setRootPoints(curRoots, curRealRoots);                  
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if(!app.isReverseLanguage()){//FKH 20040906
        sb.append(app.getPlain("TurningPoint"));
        sb.append(' ');
        sb.append(app.getPlain("of"));
        sb.append(' ');
        }
        sb.append(f.getLabel());
        if(app.isReverseLanguage()){//FKH 20040906
        sb.append(' ');
        sb.append(app.getPlain("of"));
        sb.append(' ');
        sb.append(app.getPlain("TurningPoint"));
        }
        return sb.toString();
    }

}
