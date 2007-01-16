package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class GeometricProduct extends VectorOperator {
	public GeometricProduct(Generic vector1, Generic vector2) {
		super("geometric",new Generic[] {vector1,vector2});
	}

	public Generic compute() {
		if(parameter[0] instanceof JSCLVector && parameter[1] instanceof JSCLVector) {
			JSCLVector v1=(JSCLVector)parameter[0];
			JSCLVector v2=(JSCLVector)parameter[1];
			return v1.geometricProduct(v2);
		}
		return expressionValue();
	}

//    protected void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        parameter[0].toMathML(element,null);
//        parameter[1].toMathML(element,null);
//    }

	protected Variable newinstance() {
		return new GeometricProduct(null,null);
	}
}
