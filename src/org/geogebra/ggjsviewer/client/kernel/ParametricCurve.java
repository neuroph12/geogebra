/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.ggjsviewer.client.kernel;

import org.geogebra.ggjsviewer.client.kernel.roots.RealRootFunction;



public interface ParametricCurve extends Traceable, Path {
	double getMinParameter(); 
	double getMaxParameter();	
	
	RealRootFunction getRealRootFunctionX();
	RealRootFunction getRealRootFunctionY();
			
	void evaluateCurve(double t, double [] out);
	GeoVec2D evaluateCurve(double t);	

	double evaluateCurvature(double t);	
	boolean isFunctionInX();
    
	GeoElement toGeoElement();
}
