/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.HashSet;

/**
 * Function of N variables that returns either a number
 * or a boolean. This depends on the expression this function
 * is based on.
 * 
 * @author Markus Hohenwarter + mathieu
 */
public class FunctionNVar extends ValidExpression 
implements ExpressionValue, FunctionalNVar {    	
	
    private ExpressionNode expression;
    private FunctionVariable[] fVars;    
    
    // standard case: number function, see initFunction()
    private boolean isBooleanFunction = false;
    
    // if the function is of type f(x) = c
    private boolean isConstantFunction = false; 
      
    transient private Application app;
    transient private Kernel kernel;    
    
	private StringBuilder sb = new StringBuilder(80);	

	 /**
     * Creates new Function from expression. 
     * Note: call initFunction() after this constructor.
     */ 
    public FunctionNVar(ExpressionNode expression) {
    	kernel = expression.getKernel();
    	app = kernel.getApplication();        	
        
        this.expression = expression;       
    }
    
    
    /**
     * Creates new Function from expression where the function
     * variables in expression is already known.
     */ 
    public FunctionNVar(ExpressionNode exp, FunctionVariable[] fVars) {
    	kernel = exp.getKernel();
    	app = kernel.getApplication();               
        
        expression = exp;
        this.fVars = fVars;
    }
    
    /**
     * Creates a Function that has no expression yet. Use setExpression() to
     * do this later.    
     */ 
    public FunctionNVar(Kernel kernel) {
        this.kernel = kernel;
        app = kernel.getApplication();

    }
    
    // copy constructor
    public FunctionNVar(FunctionNVar f, Kernel kernel) {   
        expression = f.expression.getCopy(kernel);
        fVars = f.fVars; // no deep copy of function variable             
        isBooleanFunction = f.isBooleanFunction;
        isConstantFunction = f.isConstantFunction;
       
        app = kernel.getApplication();
        this.kernel = kernel;
    }
    
    public boolean isFunctionVariable(String var) {
    	if (fVars == null) 
    		return false;
    	else{
    		for (int i=0; i<fVars.length; i++)
    			if (fVars[i].toString().equals(var))
    				return true;
    		return false; //if none of function vars equals var
    	}
    }
       
    
    public Kernel getKernel() {
    	return kernel;
    }
    
    public ExpressionValue deepCopy(Kernel kernel) {
        return new FunctionNVar(this, kernel);        
    }
    
    final public ExpressionNode getExpression() {
        return expression;
    }    
    
    public void resolveVariables() {
       expression.resolveVariables();                
    }
    
    /**
     * Replaces geo and all its dependent geos in this function's
     * expression by copies of their values.
     */
    public void replaceChildrenByValues(GeoElement geo) {     	
    	if (expression != null) {
    		expression.replaceChildrenByValues(geo);
    	}
    }
    
    /**
     * Use this method only if you really know
     * what you are doing.
     */
    public void setExpression(ExpressionNode exp) {
        expression = exp;
    }
    
    /**
     * Use this methode only if you really know
     * what you are doing.
     */
    public void setExpression(ExpressionNode exp, FunctionVariable[] vars) {
        expression = exp;
        fVars = vars;
    }
    
    final public FunctionNVar getFunction() {
        return this;
    }
    
    public FunctionVariable[] getFunctionVariable() {
        return fVars;
    }
    
    final public String getVarString(int i) {
    	return fVars[i].toString();
    }

    /**
     * Call this function to resolve variables and init the function.
     * May throw MyError (InvalidFunction).
     */
    public void initFunction() {              	
        
        // replace variable names by objects
        expression.resolveVariables();
        
        // the idea here was to allow something like: Derivative[f] + 3x
        // but wrapping the GeoFunction objects as ExpressionNodes of type FUNCTION
        // leads to Derivative[f](x) + 3x
        // expression.wrapGeoFunctionsAsExpressionNode();

        // replace all polynomials in expression (they are all equal to "1x" if we got this far)
        // by an instance of MyDouble
        
        
        //  simplify constant parts in expression
        expression.simplifyConstantIntegers();

        initType();
    }               
    
    private void initType() {
    	// check type of function
        ExpressionValue ev = expression.evaluate();        
        if (ev.isBooleanValue()) {
        	isBooleanFunction = true;
        }        
        else if (ev.isNumberValue()) {
        	isBooleanFunction = false;
        } 
        else {
			throw new MyError(app, "InvalidFunction");  
        }
    }

    /**
     * Returns whether this function always evaluates to BooleanValue.
     */
    final public boolean isBooleanFunction() {
    	return isBooleanFunction;
    }
    
    /**
     * Returns whether this function always evaluates to the same
     * numerical value, i.e. it is of the form f(x1,...,xn) = c.
     */
    final public boolean isConstantFunction() {
    	if (isConstantFunction)
    		return true;
    	for (int i=0;i<fVars.length; i++)
    		if (expression.contains(fVars[i]))
    			return false;
    	return true; //none of the vars appears in the expression
    }       
    
    public boolean isConstant() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public ExpressionValue evaluate() {
        return this;
    }
    
    /**
     * Returns this function's value at position.    
     * @param vals
     * @return f(vals)
     */
    final public double evaluate(double[] vals) {
    	if (isBooleanFunction) {
    		// BooleanValue
    		return evaluateBoolean(vals) ? 1 : 0;
    	}
    	else {
    		// NumberValue
    		for (int i=0;i<fVars.length; i++)
    			fVars[i].set(vals[i]);
    		return ((NumberValue) expression.evaluate()).getDouble();
    	}     
    }
    
    /**
     * Returns this function's value at position vals.
     * (Note: use this method if isBooleanFunction() returns true.
     * @param vals
     * @return f(vals)
     */
    final public boolean evaluateBoolean(double[] vals) {       
    	for (int i=0;i<fVars.length; i++)
			fVars[i].set(vals[i]);
        return ((BooleanValue) expression.evaluate()).getBoolean();       
    }
    
    public HashSet getVariables() {
        return expression.getVariables();
    }

    public GeoElement[] getGeoElementVariables() {
        return expression.getGeoElementVariables();
    }
    
    public String toString() {
        return expression.toString();
    }

    final public String toValueString() {
        return expression.toValueString();
    }
    
	final public String toLaTeXString(boolean symbolic) {
		return expression.toLaTeXString(symbolic);		
	}
	
	
    
    final private void addNumber(double n) {        
        if (n > 0) {
            expression =
                new ExpressionNode(kernel, 
                    expression,
                    ExpressionNode.PLUS,
                    new MyDouble(kernel,n));
        } else {
            expression =
                new ExpressionNode(kernel,
                    expression,
                    ExpressionNode.MINUS,
                    new MyDouble(kernel,-n));
        }   
    }
 
    
    
    /**
     * Parses given String str and tries to evaluate it to an ExpressionNode.
     * Returns null if something went wrong.
     */
    private ExpressionNode evaluateToExpressionNode(String str) {
         try {
            ExpressionNode en = kernel.getParser().parseExpression(str);
            en.resolveVariables();
            return en;
         }
         catch (Exception e) {
            e.printStackTrace();
             return null;
         } 
         catch (Error e) {
            Application.debug("error in evaluateToExpressionNode: " + str);
            e.printStackTrace();
             return null;
         }
    }
    


    
    
    
    /**
     * Calculates the derivative of this function
     * @param order of derivative
     * @return result as function
     */
    //TODO use map as in Function.java
    final public FunctionNVar derivative(int var, int order) {     	 
		// use CAS to get derivative
        // temporarily replace the variable by "x"
        //String oldVar = fVar.toString();
        //fVar.setVarString("x");
                
        // build expression string for CAS             
		sb.setLength(0);
        sb.append("Derivative(");
        // function expression with multiply sign "*"   
		sb.append(expression.getCASstring(kernel.getCurrentCAS(), true));		
		//if (order > 1) {
	        sb.append(",");
	        sb.append(fVars[var].toString());
	        sb.append(",");
	        sb.append(order);
		//}
        sb.append(") ");
		
        //Application.debug("command:"+sb.toString());
						

        try {                   	            
            // evaluate expression by CAS 
            String result = kernel.evaluateGeoGebraCAS(sb.toString());  
            
            
           
            /*
            // fast fail for e.g. "Differentiate(x)Floor(x)"
            if (result.startsWith("Differentiate(") || result.startsWith("'diff("))
            	return null;
            
    		// look for "Deriv(x)f(x)" strings and
    		// replace them with "f'(x)"
    		String [] derivs = result.split("Deriv\\(x\\)");
    		sb.setLength(0);
    		sb.append(derivs[0]); // part before first "Deriv(x)"
    		for (int i=1; i < derivs.length; i++) {
    			// replace "(x)" by "'(x)"
    			sb.append(derivs[i].replaceFirst("\\(x\\)", "'(x)"));
    		}
    		result = sb.toString();
    		*/
                		
            // it doesn't matter what label we use here as it is never used   
            /*
    		sb.setLength(0);
    		sb.append("f(x) = ");			
            sb.append(result);
            */
    
             // parse result
             FunctionNVar fun = new FunctionNVar(kernel.getParser().parseExpression(result),fVars);
             fun.initFunction();
             Application.debug("fun:"+fun.toString());
             //fun.getFunctionVariable().setVarString(oldVar);                       
             return fun;
         } catch (Error err) {       
             err.printStackTrace();
        	 return null;
         } catch (Exception e) {
        	 e.printStackTrace();
             return null;
         } catch (Throwable e) {
			 return null;
		}     
         /*
         finally {
        	 fVar.setVarString(oldVar);
         }
         */
     
         
    }	    

    
    
    
    
    
    
    
    

    
    
    
    public boolean isNumberValue() {
        return false;
    }

    public boolean isVectorValue() {
        return false;
    }
    
    public boolean isBooleanValue() {
        return false;
    }
    
    public boolean isListValue() {
        return false;
    }

    public boolean isPolynomialInstance() {
        return false;
    }   
    
    public boolean isTextValue() {
        return false;
    }
    
    final public boolean isExpressionNode() {
        return false;
    }
    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }       
    
	

	public boolean isVector3DValue() {
		return false;
	}
	
	 public String getLabelForAssignment() {
		 StringBuilder sb = new StringBuilder();
			// function, e.g. f(x) := 2*x
			sb.append(getLabel());
			sb.append("(");
			sb.append(getFunctionVariable());
			sb.append(")");	
			return sb.toString();
	 }
}