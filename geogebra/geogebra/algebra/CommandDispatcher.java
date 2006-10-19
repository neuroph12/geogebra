/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.algebra;

import geogebra.Application;
import geogebra.MyError;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.Command;
import geogebra.util.FastHashMapKeyless;


public class CommandDispatcher {
    
	private AlgebraController algCtrl;
    private Construction cons;
    private Application app;
    
    // stores (String name, CommandProcessor cmdProc) pairs
    private FastHashMapKeyless cmdTable;
    
    public CommandDispatcher(AlgebraController algCtrl) {
        this.algCtrl = algCtrl;      
        app = algCtrl.getApplication();
        cons = algCtrl.getKernel().getConstruction();              
    }
    
    /**
     * @param labelOutput: specifies if output GeoElements of this command should get labels
     */
    final public GeoElement[] processCommand(Command c, boolean labelOutput)
        throws MyError {
    	
    	if (cmdTable == null) {
    		initCmdTable();
    	}
    	
        String name = c.getName();

        // switch on macro mode to avoid labeling of output if desired
        boolean oldMacroMode = cons.isInMacroMode();
        if (!labelOutput)
            cons.setMacroMode(true);
                     
        // get CommandProcessor object for command name from command table
        CommandProcessor cmdProc = (CommandProcessor) cmdTable.get(name);
                
        GeoElement[] ret = null;
        try {              	         	
        	ret =  cmdProc.process(c);        	        	
        } 
        catch (MyError e) {
        	cons.setMacroMode(oldMacroMode);
            throw e;
        } catch (Exception e) {        	  
            cons.setMacroMode(oldMacroMode);        	  
            e.printStackTrace();
            throw new MyError(app, app.getError("UnknownCommand") + " : " + c);
        }
        
        cons.setMacroMode(oldMacroMode);
        return ret;
    }
           
    private void initCmdTable() {    	 
    	cmdTable = new FastHashMapKeyless(500);

    	cmdTable.put("UnitVector", new CmdUnitVector(algCtrl));	   
    	cmdTable.put("SecondAxis", new CmdSecondAxis(algCtrl));	   
    	cmdTable.put("CircleArc", new CmdCircleArc(algCtrl));	   
    	cmdTable.put("Parameter", new CmdParameter(algCtrl));	   
    	cmdTable.put("TurningPoint", new CmdTurningPoint(algCtrl));	   
    	cmdTable.put("Derivative", new CmdDerivative(algCtrl));	   
    	cmdTable.put("Integral", new CmdIntegral(algCtrl));	   
    	cmdTable.put("LowerSum", new CmdLowerSum(algCtrl));	   
    	cmdTable.put("Root", new CmdRoot(algCtrl));	   
    	cmdTable.put("Dilate", new CmdDilate(algCtrl));	   
    	cmdTable.put("Vector", new CmdVector(algCtrl));	   
    	cmdTable.put("Ellipse", new CmdEllipse(algCtrl));	   
    	cmdTable.put("Hyperbola", new CmdHyperbola(algCtrl));	   
    	cmdTable.put("TaylorSeries", new CmdTaylorSeries(algCtrl));	   
    	cmdTable.put("SecondAxisLength", new CmdSecondAxisLength(algCtrl));	   
    	cmdTable.put("Ray", new CmdRay(algCtrl));	   
    	cmdTable.put("AngularBisector", new CmdAngularBisector(algCtrl));	   
    	cmdTable.put("Angle", new CmdAngle(algCtrl));	   
    	cmdTable.put("Corner", new CmdCorner(algCtrl));	   
    	cmdTable.put("Midpoint", new CmdMidpoint(algCtrl));	   
    	cmdTable.put("Direction", new CmdDirection(algCtrl));	   
    	cmdTable.put("Polynomial", new CmdPolynomial(algCtrl));	   
    	cmdTable.put("Tangent", new CmdTangent(algCtrl));	   
    	cmdTable.put("UnitOrthogonalVector", new CmdUnitOrthogonalVector(algCtrl));	   
    	cmdTable.put("Distance", new CmdDistance(algCtrl));	   
    	cmdTable.put("Asymptote", new CmdAsymptote(algCtrl));	   
    	cmdTable.put("Mirror", new CmdMirror(algCtrl));	   
    	cmdTable.put("Center", new CmdCenter(algCtrl));	   
    	cmdTable.put("Directrix", new CmdDirectrix(algCtrl));	   
    	cmdTable.put("Diameter", new CmdDiameter(algCtrl));	   
    	cmdTable.put("Line", new CmdLine(algCtrl));	   
    	cmdTable.put("Intersect", new CmdIntersect(algCtrl));	   
    	cmdTable.put("CircumcircleSector", new CmdCircumcircleSector(algCtrl));	   
    	cmdTable.put("Focus", new CmdFocus(algCtrl));	   
    	cmdTable.put("OrthogonalVector", new CmdOrthogonalVector(algCtrl));	   
    	cmdTable.put("Length", new CmdLength(algCtrl));	   
    	cmdTable.put("Delete", new CmdDelete(algCtrl));	   
    	cmdTable.put("Radius", new CmdRadius(algCtrl));	   
    	cmdTable.put("Arc", new CmdArc(algCtrl));	   
    	cmdTable.put("CircleSector", new CmdCircleSector(algCtrl));	   
    	cmdTable.put("Polar", new CmdPolar(algCtrl));	   
    	cmdTable.put("Semicircle", new CmdSemicircle(algCtrl));	   
    	cmdTable.put("FirstAxisLength", new CmdFirstAxisLength(algCtrl));	   
    	cmdTable.put("Parabola", new CmdParabola(algCtrl));	   
    	cmdTable.put("Rotate", new CmdRotate(algCtrl));	   
    	cmdTable.put("Function", new CmdFunction(algCtrl));	   
    	cmdTable.put("Extremum", new CmdExtremum(algCtrl));	   
    	cmdTable.put("CircumcircleArc", new CmdCircumcircleArc(algCtrl));	   
    	cmdTable.put("Translate", new CmdTranslate(algCtrl));	   
    	cmdTable.put("Excentricity", new CmdExcentricity(algCtrl));	   
    	cmdTable.put("OrthogonalLine", new CmdOrthogonalLine(algCtrl));	   
    	cmdTable.put("Relation", new CmdRelation(algCtrl));	   
    	cmdTable.put("Polygon", new CmdPolygon(algCtrl));	   
    	cmdTable.put("Segment", new CmdSegment(algCtrl));	   
    	cmdTable.put("Sector", new CmdSector(algCtrl));	   
    	cmdTable.put("Locus", new CmdLocus(algCtrl));	   
    	cmdTable.put("Centroid", new CmdCentroid(algCtrl));	   
    	cmdTable.put("Vertex", new CmdVertex(algCtrl));	   
    	cmdTable.put("Conic", new CmdConic(algCtrl));	   
    	cmdTable.put("FirstAxis", new CmdFirstAxis(algCtrl));	   
    	cmdTable.put("Circle", new CmdCircle(algCtrl));	   
    	cmdTable.put("LineBisector", new CmdLineBisector(algCtrl));	   
    	cmdTable.put("Area", new CmdArea(algCtrl));	   
    	cmdTable.put("Slope", new CmdSlope(algCtrl));	   
    	cmdTable.put("Axes", new CmdAxes(algCtrl));	   
    	cmdTable.put("Point", new CmdPoint(algCtrl));	   
    	cmdTable.put("UpperSum", new CmdUpperSum(algCtrl));    	  
    	cmdTable.put("If", new CmdIf(algCtrl));
    	cmdTable.put("Sequence", new CmdSequence(algCtrl));
    }


}
