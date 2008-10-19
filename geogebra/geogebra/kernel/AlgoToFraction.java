/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

public class AlgoToFraction extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoNumeric num; //input
    private GeoText text; //output	
    
    private int frac[] = {0,0};
 
    private StringBuffer sb = new StringBuffer();
    
    AlgoToFraction(Construction cons, String label, GeoNumeric num) {
    	this(cons, num);
        text.setLabel(label);
    }

    AlgoToFraction(Construction cons, GeoNumeric num) {
        super(cons);
        this.num = num;
               
        text = new GeoText(cons);

        setInputOutput();
        compute();
    }

    protected String getClassName() {
        return "AlgoToFraction";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = num;

        output = new GeoElement[1];
        output[0] = text;
        setDependencies(); // done by AlgoElement
    }

    GeoText getResult() {
        return text;
    }

    protected final void compute() {
		if (input[0].isDefined()) {
			frac = DecimalToFraction(num.getDouble(),0.00000001);

			sb.setLength(0);
	    	sb.append("\\frac{");
	    	sb.append(frac[0]+"");
	    	sb.append("}{");
	    	sb.append(frac[1]+"");
	    	sb.append("}");
	    	
	    	text.setTextString(sb.toString());
	    	text.setLaTeX(true,false);
		
		} else
			text.setUndefined();
	}
  
    /*	Algorithm To Convert A Decimal To A Fraction
     * by
     * John Kennedy
     * Mathematics Department
     * Santa Monica College
     * 1900 Pico Blvd.
     * Santa Monica, CA 90405
     * http://homepage.smc.edu/kennedy_john/DEC2FRAC.PDF
     */
	private int[] DecimalToFraction(double Decimal, double AccuracyFactor) {
	double FractionNumerator, FractionDenominator;
	double DecimalSign;
	double Z;
	double PreviousDenominator;
	double ScratchValue;
	
	
	int ret[] = {0,0};
	if (Decimal == Double.NaN) return ret; // return 0/0 
	
	if (Decimal == Double.POSITIVE_INFINITY) {
	  ret[0] = 1;
	  ret[1] = 0 ; // 1/0
	  return ret;
	}
	if (Decimal == Double.NEGATIVE_INFINITY) {
		ret[0] = -1;
		ret[1] = 0; // -1/0
		return ret;
	}
	
	if (Decimal < 0.0) DecimalSign = -1.0; else DecimalSign = 1.0;
	
	Decimal = Math.abs(Decimal);
	
	if (Math.abs(Decimal - Math.floor(Decimal)) < AccuracyFactor) { // handles exact integers including 0 �
		FractionNumerator = Decimal * DecimalSign;
		FractionDenominator = 1.0;
		
		ret[0] = (int)FractionNumerator;
		ret[1] = (int)FractionDenominator;
		return ret;
	}
	if (Decimal < 1.0E-19) { // X = 0 already taken care of �
		FractionNumerator = DecimalSign;
		FractionDenominator = 9999999999999999999.0;
		
		ret[0] = (int)FractionNumerator;
		ret[1] = (int)FractionDenominator;
		return ret;
	}
	if (Decimal > 1.0E19) {
		FractionNumerator = 9999999999999999999.0 * DecimalSign;
		FractionDenominator = 1.0;
		
		ret[0] = (int)FractionNumerator;
		ret[1] = (int)FractionDenominator;
		return ret;
	}
	
	Z = Decimal;
	PreviousDenominator = 0.0;
	FractionDenominator = 1.0;
	do {
		Z = 1.0/(Z - Math.floor(Z));
		ScratchValue = FractionDenominator;
		FractionDenominator = FractionDenominator * Math.floor(Z) + PreviousDenominator;
		PreviousDenominator = ScratchValue;
		FractionNumerator = Math.floor(Decimal * FractionDenominator + 0.5); // Rounding Function
	} while ( Math.abs((Decimal - (FractionNumerator /FractionDenominator))) > AccuracyFactor && Z != Math.floor(Z));
	FractionNumerator = DecimalSign*FractionNumerator;
	
	ret[0] = (int)FractionNumerator;
	ret[1] = (int)FractionDenominator;
	return ret;
	}

}
