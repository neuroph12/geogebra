/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * NumberValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package org.geogebra.ggjsviewer.client.kernel.arithmetic;

import org.geogebra.ggjsviewer.client.kernel.GeoElement;


/**
 *
 * @author  Markus
 * @version 
 */
public interface NumberValue extends ExpressionValue { 
    public MyDouble getNumber();
    public boolean isAngle();
    public double getDouble(); 
    //AGpublic GeoElement toGeoElement();
	public GeoElement toGeoElement();
}
