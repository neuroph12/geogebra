/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
/**
 * Find a limit
 * 
 * @author Michael Borcherds
 */
public class AlgoPartialFractions extends AlgoSimplify {
   
    public AlgoPartialFractions(Construction cons, String label, GeoFunction f) {
    	super(cons, label, f);
    }
    
    protected String getClassName() {
        return "AlgoPartialFractions";
    }
     
    protected String getMathPiperString(String functionIn) {
	    sb.setLength(0);
        sb.append("Apart(");
        sb.append(functionIn);
        sb.append(')');

        
        return sb.toString();
        

    }

}