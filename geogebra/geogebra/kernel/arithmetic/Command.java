/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * Command.java
 *
 * Created on 05. September 2001, 12:05
 */

package geogebra.kernel.arithmetic;

import geogebra.Application;
import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.util.ArrayList;
import java.util.HashSet;
/**
 *
 * @author  Markus
 * @version 
 */
public class Command extends ValidExpression 
implements ExpressionValue {
    
     
    // list of arguments
    private ArrayList args = new ArrayList();
    private String name; // internal command name (in English)
    
    private Kernel kernel;
    private Application app;
    private GeoElement evalGeo; // evaluated Element
    
    /** Creates new Command */
    public Command(Kernel kernel, String name, boolean translateName) {    
        this.kernel = kernel;
        app = kernel.getApplication();
            
        if (translateName) {           
            //  translate command name to internal name
            this.name = app.translateCommand(name);           
        } else {
            this.name = name;
        }               
    }
    
    public Kernel getKernel() {
        return kernel;
    }
    
    public void addArgument(ValidExpression arg) {
        args.add( arg );
    }
    
    /**
     * Returns the name of the variable at the specified argument position.
     * If there is no variable name at this position, null is returned.
     */
    public String getVariableName(int i) {
    	ExpressionValue ev = ((ExpressionNode) args.get(i)).getLeft();
    	if (ev instanceof Variable)
    		return ((Variable) ev).getName();
    	else {    		
    		return null;
    	}
    }
    
    public ExpressionNode [] getArguments() {
    	int size = args.size();
        ExpressionNode [] ret = new ExpressionNode[size];
        
        for (int i=0; i < args.size(); i++) {        
            ret[i] = (ExpressionNode) args.get(i);        
        }
        return ret;                
    }
    
    public void resolveVariables() {
        for (int i=0; i < args.size(); i++) {        
            ((ExpressionNode) args.get(i)).resolveVariables();        
        }
    }
    
    public ExpressionNode getArgument(int i) {
        return (ExpressionNode) args.get(i);
    }
    
    public int getArgumentNumber() {
        return args.size();
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        int size = args.size();
        StringBuffer sb = new StringBuffer(); 
        if (kernel.isTranslateCommandName()) {
            sb.append(app.getCommand(name));                       
        } else {
            sb.append(name);
        }        
        sb.append("[");
        for (int i = 0; i < size - 1; i++) {
            sb.append( args.get(i) );
            sb.append(", ");
        }
        if (size > 0)
        	sb.append( args.get(size-1));
        sb.append("]");
        return sb.toString();
    }
    
    public ExpressionValue evaluate() {
        // not yet evaluated: process command
        if (evalGeo == null) {  
            GeoElement [] geos = null;
            try {
                 geos = kernel.getAlgebraController().processCommand(this, false); 
            } catch (Error me) {
                throw me;
            }
                                     
             if (geos != null && geos.length == 1) {
                evalGeo = geos[0]; 
             } else {
                System.err.println("invalid command evaluation: " + name);
                throw new MyError(app, app.getError("InvalidInput") + ":\n" + this);                                                                 
             }          
        }                       
        
        return evalGeo;
    }

    public boolean isConstant() {
        return evaluate().isConstant();
    }

    public boolean isLeaf() {
        //return evaluate().isLeaf();
        return true;
    }

    public boolean isNumberValue() {
        return evaluate().isNumberValue();
    }

    public boolean isVectorValue() {
        return evaluate().isVectorValue();
    }
    
    final public boolean isBooleanValue() {
        return evaluate().isBooleanValue();
    }

    public boolean isPolynomialInstance() {
        return false;
                
        //return evaluate().isPolynomial();
    }
    
    public boolean isTextValue() {
        return evaluate().isTextValue();
    }   

    public ExpressionValue deepCopy() {
        Command c = new Command(kernel, name, false);
        // copy arguments     
        int size = args.size();
        for (int i=0; i < size; i++) {
            c.addArgument(((ExpressionNode) args.get(i)).getCopy());
        }
        return c;
    }

    public HashSet getVariables() {             
        HashSet set = new HashSet();
        int size = args.size();
        for (int i=0; i < size; i++) {
            HashSet s = ((ExpressionNode)args.get(i)).getVariables();
            if (s != null) set.addAll(s);
        }
        return set;
    }

    public String toValueString() {
        return evaluate().toValueString();
    }
    
	public String toLaTeXString(boolean symbolic) {
		return evaluate().toLaTeXString(symbolic);
	}    

    final public boolean isExpressionNode() {
        return false;
    }
    
 
    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }    
}
