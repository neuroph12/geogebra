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

import geogebra.kernel.arithmetic.NumberValue;


/**
 * Maximum value of a list.
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListMax extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoNumeric max; //output	

    AlgoListMax(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        max = new GeoNumeric(cons);

        setInputOutput();
        compute();
        max.setLabel(label);
    }

    String getClassName() {
        return "AlgoListMax";
    }

    void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = max;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getMax() {
        return max;
    }

    final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		max.setUndefined();
    		return;
    	}
    	
    	double maxVal = Double.NEGATIVE_INFINITY;
    	for (int i=0; i < size; i++) {
    		GeoElement geo = geoList.get(i);
    		if (geo.isNumberValue()) {
    			NumberValue num = (NumberValue) geo;
    			maxVal = Math.max(maxVal, num.getDouble());
    		} else {
    			max.setUndefined();
        		return;
    		}    		    		
    	}   
    	
    	max.setValue(maxVal);
    }
    
}
