/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.ggjsviewer.client.kernel;

import org.geogebra.ggjsviewer.client.Matrix.GgbVector;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionNode;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionValue;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.Function;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.MyDouble;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.MyList;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.NumberValue;
import org.geogebra.ggjsviewer.client.kernel.optimization.ExtremumFinder;
import org.geogebra.ggjsviewer.client.kernel.roots.RealRootFunction;


/**
 * Cartesian parametric curve, e.g. (cos(t), sin(t)) for t from 0 to 2pi.
 * 
 * @author Markus Hohenwarter
 */
public class GeoCurveCartesian extends GeoCurveCartesianND
implements Path, Translateable, Rotateable, PointRotateable, Mirrorable, Dilateable, MatrixTransformable,Traceable, CasEvaluableFunction, ParametricCurve, LineProperties {

	private static final long serialVersionUID = 1L;
	
	// samples to find interval with closest parameter position to given point
	private static final int CLOSEST_PARAMETER_SAMPLES = 100;
	
	private Function funX, funY;
	private boolean isClosedPath;
	private boolean trace = false;//, spreadsheetTrace = false; -- not used, commented out (Zbynek Konecny, 2010-06-16)	
//	Victor Franco Espino 25-04-2007
	/**
	 * Parameter in dialog box for adjust color of curvature
	 */
	double CURVATURE_COLOR = 15;//optimal value 
    //Victor Franco Espino 25-04-2007


	private ParametricCurveDistanceFunction distFun;
	
	/**
	 * Creates new curve
	 * @param c construction
	 * @param fx x-coord function
	 * @param fy y-coord function
	 */
	public GeoCurveCartesian(Construction c, 
			Function fx, Function fy) 
	{
		super(c);
		setFunctionX(fx);
		setFunctionY(fy);					
	}
	
	public String getClassName() {
		return "GeoCurveCartesian";
	}
	
	protected String getTypeString() {
		return "CurveCartesian";
	}
	
	public String translatedTypeString() {
		return bApp.getPlain("Curve");
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_CURVE_CARTESIAN;
    }

	/** copy constructor 
	 * @param f Curve to copy*/
	public GeoCurveCartesian(GeoCurveCartesian f) {
		super(f.cons);
		set(f);
	}

	public GeoElement copy() {
		return new GeoCurveCartesian(this);
	}
	
	/** 
	 * Sets the function of the x coordinate of this curve.
	 * @param funX new x-coord function
	 */
	final public void setFunctionX(Function funX) {
		this.funX = funX;			
	}		
	
	/** 
	 * Sets the function of the y coordinate of this curve.
	 * @param funY new y-coord function
	 */
	final public void setFunctionY(Function funY) {
		this.funY = funY;			
	}
	
	/**
     * Replaces geo and all its dependent geos in this function's
     * expression by copies of their values.
     * @param geo Element to be replaced
     */
    public void replaceChildrenByValues(GeoElement geo) {     	
    	if (funX != null) {
    		funX.replaceChildrenByValues(geo);
    	}
    	if (funY != null) {
    		funY.replaceChildrenByValues(geo);
    	}
    }
	
	/** 
	 * Sets the start and end parameter value of this curve.
	 * Note: setFunctionX() and setFunctionY() has to be called 
	 * before this method.
	 */
	public void setInterval(double startParam, double endParam) {
		
		/*
		this.startParam = startParam;
		this.endParam = endParam;
		
		isDefined = startParam <= endParam;		
		*/
		
		super.setInterval(startParam, endParam);
		
		// update isClosedPath, i.e. startPoint == endPoint
		isClosedPath =  
			Kernel.isEqual(funX.evaluate(startParam), funX.evaluate(endParam), Kernel.MIN_PRECISION) &&
			Kernel.isEqual(funY.evaluate(startParam), funY.evaluate(endParam), Kernel.MIN_PRECISION);
	}

	public void set(GeoElement geo) {
		GeoCurveCartesian geoCurve = (GeoCurveCartesian) geo;				
		
		funX = new Function(geoCurve.funX, kernel);
		funY = new Function(geoCurve.funY, kernel);
		startParam = geoCurve.startParam;
		endParam = geoCurve.endParam;
		isDefined = geoCurve.isDefined;
		
		// macro OUTPUT
		if (geo.cons != cons && isAlgoMacroOutput()) {	
			if (!geo.isIndependent()) {
//				System.out.println("set " + this.label);
//				System.out.println("   funX before: " + funX.toLaTeXString(true));
				
				// this object is an output object of AlgoMacro
				// we need to check the references to all geos in its function's expression
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				algoMacro.initFunction(funX);
				algoMacro.initFunction(funY);
//				System.out.println("   funX after: " + funX.toLaTeXString(true));
			}
		}
	}		
	
	
	/**
	 * Set this curve by applying CAS command to f.
	 */
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f, boolean symbolic) {
		GeoCurveCartesian c = (GeoCurveCartesian) f;
		
		if (c.isDefined()) {			
			funX = (Function) c.funX.evalCasCommand(ggbCasCmd, symbolic);
			funY = (Function) c.funY.evalCasCommand(ggbCasCmd, symbolic);
			isDefined = !(funX == null || funY == null);
			if (isDefined)
				setInterval(c.startParam, c.endParam);			
		} else {
			isDefined = false;
		}	
	}
	
	
	// added by Loïc Le Coq 2009/08/12
	/**
	 * @return value string x-coord function
	 */
	final public String getFunX(){
		return funX.toValueString();
	}
	/**
	 * @return value string y-coord function
	 */
	final public String getFunY(){
		return funY.toValueString();
	}
	// end Loïc Le Coq
	
	final public RealRootFunction getRealRootFunctionX() {
		return funX;
	}
	
	final public RealRootFunction getRealRootFunctionY() {
		return funY;
	}
	
	public ExpressionValue evaluate() {
		return this;
	}
	
	/**
	 * translate function by vector v
	 */
	final public void translate(GgbVector v) {
		funX.translateY(v.getX());
		funY.translateY(v.getY());
	}
	
	final public boolean isTranslateable() {
		return true;
	}
	
	final public boolean isMatrixTransformable() {
		return true;
	}
	
	/**
	 * Translates the curve by vector given by coordinates
	 * @param vx x-coord of the translation vector
	 * @param vy y-coord of the translation vector
	 */
	final public void translate(double vx, double vy) {
		funX.translateY(vx);
		funY.translateY(vy);
	}	
    //<Zbynek Konecny, 2010-06-16>
	final public void rotate(NumberValue phi,GeoPoint P){
		translate(-P.getX(),-P.getY());
		rotate(phi);
		translate(P.getX(),P.getY());
	}
	final public void mirror(GeoPoint P){
		dilate(new MyDouble(kernel,-1.0),P);
	}
	
	final public void mirror(GeoLine g) {
	        // Y = S(phi).(X - Q) + Q
	        // where Q is a point on g, S(phi) is the mirrorTransform(phi)
	        // and phi/2 is the line's slope angle
	        
	        // get arbitrary point of line
	        double qx, qy; 
	        if (Math.abs(g.x) > Math.abs(g.y)) {
	            qx = g.z / g.x;
	            qy = 0.0d;
	        } else {
	            qx = 0.0d;
	            qy = g.z / g.y;
	        }
	        
	        // translate -Q
	        translate(qx, qy);     
	        
	        // S(phi)        
	        mirror(new MyDouble(kernel,2.0 * Math.atan2(-g.x, g.y)));
	        
	        // translate back +Q
	        translate(-qx, -qy);
	        
	         // update inhom coords
	         
	    }
		
	
	final public void rotate(NumberValue phi){
		ExpressionNode cosPhi = new ExpressionNode(kernel,phi,ExpressionNode.COS,null);
		ExpressionNode sinPhi = new ExpressionNode(kernel,phi,ExpressionNode.SIN,null);
		ExpressionNode minSinPhi = new ExpressionNode(kernel,new MyDouble(kernel,0.0),ExpressionNode.MINUS,sinPhi);
		matrixTransform(cosPhi,minSinPhi,sinPhi,cosPhi);
	}
	
	public void dilate(NumberValue ratio,GeoPoint P){
		translate(-P.getX(),-P.getY());
		ExpressionNode exprX = ((Function)funX.deepCopy(kernel)).getExpression();
		ExpressionNode exprY = ((Function)funY.deepCopy(kernel)).getExpression();
		funX.setExpression(new ExpressionNode(kernel,ratio,ExpressionNode.MULTIPLY,exprX));
		funY.setExpression(new ExpressionNode(kernel,ratio,ExpressionNode.MULTIPLY,exprY));
		translate(P.getX(),P.getY());
	}
	
	/**
     * mirror transform with angle phi
     *  [ cos(phi)       sin(phi)   ]
     *  [ sin(phi)      -cos(phi)   ]  
     */
	private void mirror(NumberValue phi){				
		ExpressionNode cosPhi = new ExpressionNode(kernel,phi,ExpressionNode.COS,null);
		ExpressionNode sinPhi = new ExpressionNode(kernel,phi,ExpressionNode.SIN,null);
		ExpressionNode minCosPhi = new ExpressionNode(kernel,new MyDouble(kernel,0.0),ExpressionNode.MINUS,cosPhi);
		matrixTransform(cosPhi,sinPhi,sinPhi,minCosPhi);				
	}
	
	
	public void matrixTransform(GeoList matrix) {
		
		MyList list = matrix.getMyList();
		
		if (list.getMatrixCols() != 2 || list.getMatrixRows() != 2) {
			setUndefined();
			return;
		}
		 
		ExpressionValue a,b,c,d;
		
		a = ((NumberValue)(MyList.getCell(list,0,0).evaluate()));
		b = ((NumberValue)(MyList.getCell(list,1,0).evaluate()));
		c = ((NumberValue)(MyList.getCell(list,0,1).evaluate()));
		d = ((NumberValue)(MyList.getCell(list,1,1).evaluate()));
 
		matrixTransform(a,b,c,d);
		
	}
	
	/**
	 * Transforms curve using matrix
	 * [a b]
	 * [c d]
	 * @param a top left matrix element
	 * @param b top right matrix element
	 * @param c bottom left matrix element
	 * @param d bottom right matrix element
	 */
	public void matrixTransform(ExpressionValue a,ExpressionValue b, ExpressionValue c, ExpressionValue d){
		ExpressionNode exprX = ((Function)funX.deepCopy(kernel)).getExpression();
		ExpressionNode exprY = ((Function)funY.deepCopy(kernel)).getExpression();
		ExpressionNode transX = new ExpressionNode(kernel,
				new ExpressionNode(kernel,exprX,ExpressionNode.MULTIPLY,a),
				ExpressionNode.PLUS,
				new ExpressionNode(kernel,exprY,ExpressionNode.MULTIPLY,b));
		ExpressionNode transY = new ExpressionNode(kernel,
				new ExpressionNode(kernel,exprX,ExpressionNode.MULTIPLY,c),
				ExpressionNode.PLUS,
				new ExpressionNode(kernel,exprY,ExpressionNode.MULTIPLY,d));
		funX.setExpression(transX);
		funY.setExpression(transY);
	}
	//</Zbynek>
	
	public boolean showInAlgebraView() {
		return true;
	}

	protected boolean showInEuclidianView() {
		return isDefined();
	}
	
	
	
	//TODO remove and use super method (funX and funY should be removed in fun[])
	public String toString() {
		if (sbToString == null) {
			sbToString = new StringBuilder(80);
		}
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append('(');
			sbToString.append(funX.getVarString());
			sbToString.append(") = ");					
		}		
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	//private StringBuilder sbToString;
	//private StringBuilder sbTemp;
	
	//TODO remove and use super method (funX and funY should be removed in fun[])
	public String toValueString() {		
		
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append('(');
			sbTemp.append(funX.toValueString());
			sbTemp.append(", ");
			sbTemp.append(funY.toValueString());
			sbTemp.append(')');
			return sbTemp.toString();
		} else
			return bApp.getPlain("undefined");
	}	
	
	//TODO remove and use super method (funX and funY should be removed in fun[])
	public String toSymbolicString() {	
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append('(');
			sbTemp.append(funX.toString());
			sbTemp.append(", ");
			sbTemp.append(funY.toString());
			sbTemp.append(')');
			return sbTemp.toString();
		} else
			return bApp.getPlain("undefined");
	}
	
	//TODO remove and use super method (funX and funY should be removed in fun[])
	public String toLaTeXString(boolean symbolic) {
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append("\\left(\\begin{array}{c}");
			sbTemp.append(funX.toLaTeXString(symbolic));
			sbTemp.append("\\\\");
			sbTemp.append(funY.toLaTeXString(symbolic));
			sbTemp.append("\\end{array}\\right)");
			return sbTemp.toString();
		} else
			return bApp.getPlain("undefined");		
	}		
	


	/* 
	 * Path interface
	 */	 
	public void pointChanged(GeoPointInterface PI) {	
		
		GeoPoint P = (GeoPoint) PI;
		
		// get closest parameter position on curve
		PathParameter pp = P.getPathParameter();
		double t = getClosestParameter(P, pp.t);
		pp.t = t;
		pathChanged(P);	
	}
	
	public boolean isOnPath(GeoPointInterface PI, double eps) {		
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
			
		// get closest parameter position on curve
		PathParameter pp = P.getPathParameter();
		double t = getClosestParameter(P, pp.t);
		boolean onPath =
			Math.abs(funX.evaluate(t) - P.inhomX) <= eps &&
			Math.abs(funY.evaluate(t) - P.inhomY) <= eps;				
		return onPath;
	}

	public void pathChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		PathParameter pp = P.getPathParameter();
		if (pp.t < startParam)
			pp.t = startParam;
		else if (pp.t > endParam)
			pp.t = endParam;
		
		// calc point for given parameter
		P.x = funX.evaluate(pp.t);
		P.y = funY.evaluate(pp.t);
		P.z = 1.0;		
	}
	
	
	/**
	 * Returns the parameter value t where this curve has minimal distance
	 * to point P.
	 * @param startValue an interval around startValue is specially investigated
	 * @param P point to which the distance is minimized
	 * @return optimal parameter value t				
	 */
	public double getClosestParameter(GeoPoint P, double startValue) {		
		if (distFun == null)
			distFun = new ParametricCurveDistanceFunction(this);				
		distFun.setDistantPoint(P.x/P.z, P.y/P.z);	
		
		// check if P is on this curve and has the right path parameter already
    	if (P.getPath() == this) { 
    		// point A is on curve c, take its parameter
    		PathParameter pp = P.getPathParameter();
    		double pathParam = pp.t;
    		if (distFun.evaluate(pathParam) < Kernel.MIN_PRECISION * Kernel.MIN_PRECISION)
    			return pathParam;   
    			
			// if we don't have a startValue yet, let's take the path parameter as a guess
    		if (Double.isNaN(startValue))
    			startValue = pathParam;
    	} 									
		
		// first sample distFun to find a start intervall for ExtremumFinder		
		double step = (endParam - startParam) / CLOSEST_PARAMETER_SAMPLES;
		double minVal = distFun.evaluate(startParam);
		double minParam = startParam;
		double t = startParam;		
		for (int i=0; i < CLOSEST_PARAMETER_SAMPLES; i++) {
			t = t + step;
			double ft = distFun.evaluate(t);
			if (ft < minVal) {
				// found new minimum
				minVal = ft;
				minParam = t;
			}
		}
		
		// use interval around our minParam found by sampling
		// to find minimum
		double left = Math.max(startParam, minParam - step);
		double right = Math.min(endParam, minParam + step);	
		ExtremumFinder extFinder = kernel.getExtremumFinder();
		double sampleResult = extFinder.findMinimum(left, right,
									distFun, Kernel.MIN_PRECISION);	
		
		// if we have a valid startParam we try the intervall around it too
		// however, we don't check the same intervall again
		if (!Double.isNaN(startValue) &&
			(startValue < left || right < startValue)) {
			left = Math.max(startParam, startValue - step);
			right = Math.min(endParam, startValue + step);				
			double startValResult = extFinder.findMinimum(left, right,
										distFun, Kernel.MIN_PRECISION);
			if (distFun.evaluate(startValResult) <
					distFun.evaluate(sampleResult)	) {				
				return startValResult;
			}				
		}

		return sampleResult;
	}

	

	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	public boolean isClosedPath() {	
		return isClosedPath;
	}

	public boolean isNumberValue() {
		return false;		
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   

	public boolean isTextValue() {
		return false;
	}	
	
	public boolean isGeoCurveCartesian() {
		return true;
	}	
	
	final public boolean isTraceable() {
		return true;
	}

	final public boolean getTrace() {		
		return trace;
	}
	
	//G.Sturr 2010-5-18  get/set spreadsheet trace not needed here
	/*
	public void setSpreadsheetTrace(boolean spreadsheetTrace) {
		this.spreadsheetTrace = spreadsheetTrace;
	}

	public boolean getSpreadsheetTrace() {
		return spreadsheetTrace;
	}
	*/
	
	public void setTrace(boolean trace) {
		this.trace = trace;	
	}   	
	
	/**
	 * Calculates the Cartesian coordinates of this curve
	 * for the given parameter paramVal. The result is written
	 * to out.
	 */
	public void evaluateCurve(double paramVal, double [] out) {		
		out[0] = funX.evaluate(paramVal);
		out[1] = funY.evaluate(paramVal);
	}
	
	public GeoVec2D evaluateCurve(double t) {		
		return new GeoVec2D(kernel, funX.evaluate(t), funY.evaluate(t));
	}  
	
	/**
	 * Calculates curvature for curve: 
	 * k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T = sqrt(a'(t)^2+b'(t)^2)
	 * @author Victor Franco, Markus Hohenwarter
	 */
	public double evaluateCurvature(double t){		
		Function f1X, f1Y, f2X, f2Y;		
		f1X = funX.getDerivative(1);
		f1Y = funY.getDerivative(1);
		f2X = funX.getDerivative(2);
		f2Y = funY.getDerivative(2);
		
		if (f1X == null || f1Y == null || f2X == null || f2Y == null)
			return Double.NaN;
		
	  	double f1eval[] = new double[2];
    	double f2eval[] = new double[2];    	   
    	f1eval[0] = f1X.evaluate(t);
    	f1eval[1] = f1Y.evaluate(t);
    	f2eval[0] = f2X.evaluate(t);
    	f2eval[1] = f2Y.evaluate(t);
        double t1 = Math.sqrt(f1eval[0]*f1eval[0] + f1eval[1]*f1eval[1]);
        double t3 = t1 * t1 * t1;
        return (f1eval[0]*f2eval[1] - f2eval[0]*f1eval[1]) / t3;
	}
	

	
	final public boolean isGeoCurveable() {
		return true;
	}
	
	public boolean isCasEvaluableFunction() {
		return true;
	}

	public String getVarString() {	
		return funX.getFunctionVariables().toString();
	}
	
	final public boolean isFunctionInX() {		
		return false;
	}
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// TODO check for equality?
		return false;
		//if (geo.isGeoCurveCartesian()) return xxx; else return false;
	}
	
	public boolean isFillable() {
		return true;
	}


	public boolean isVector3DValue() {
		return false;
	}

	@Override
	public String getXML() {
		// TODO Auto-generated method stub
		return null;
	}


}