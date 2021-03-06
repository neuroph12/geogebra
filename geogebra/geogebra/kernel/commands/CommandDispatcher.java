/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.commands;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.HashMap;
import java.util.Set;

/**
 * Runs commands and handles string to command processor conversion.
 * 
 */
public class CommandDispatcher {
    /** kernel **/
	protected Kernel kernel;
    private Construction cons;
    private Application app;
    
    /** stores public (String name, CommandProcessor cmdProc) pairs*/   
    protected HashMap<String,CommandProcessor> cmdTable;
    
    protected HashMap[] cmdSubTable;
    public static final int TABLE_GEOMETRY = 0;
    public static final int TABLE_ALGEBRA = 1;
    public static final int TABLE_TEXT = 2;
    public static final int TABLE_LOGICAL = 3;
    public static final int TABLE_FUNCTION = 4;
    public static final int TABLE_CONIC = 5;
    public static final int TABLE_LIST = 6;
    public static final int TABLE_VECTOR = 7;
    public static final int TABLE_TRANSFORMATION = 8;
    public static final int TABLE_CHARTS = 9;
    public static final int TABLE_STATISTICS = 10;
    public static final int TABLE_PROBABILITY = 11;
    public static final int TABLE_SPREADSHEET = 12;
    public static final int TABLE_SCRIPTING = 13;
    public static final int TABLE_OTHER = 14;
    
    private int tableCount = 15;
    
    
    public String getSubCommandSetName(int index){
    	switch (index) {
    	case TABLE_GEOMETRY: return app.getMenu("Geometry Commands");
    	case TABLE_ALGEBRA: return app.getMenu("Algebra Commands");
    	case TABLE_TEXT: return app.getMenu("Text Commands");
    	case TABLE_LOGICAL: return app.getMenu("Logical Commands");
    	case TABLE_FUNCTION: return app.getMenu("Function Commands");
    	case TABLE_CONIC: return app.getMenu("Conic Commands");
    	case TABLE_LIST: return app.getMenu("List Commands");
    	case TABLE_VECTOR:return app.getMenu("Vector Commands");
    	case TABLE_TRANSFORMATION: return app.getMenu("Transformation Commands");
    	case TABLE_CHARTS: return app.getMenu("Chart Commands");
    	case TABLE_STATISTICS: return app.getMenu("Statistics Commands");
    	case TABLE_PROBABILITY: return app.getMenu("Probability Commands");
    	case TABLE_SPREADSHEET: return app.getMenu("Spreadsheet Commands");
    	case TABLE_SCRIPTING: return app.getMenu("Scripting Commands");
    	case TABLE_OTHER: return app.getMenu("Other Commands");
    	default: return null;
    	}
    }
    
    
    
    
    /** stores internal (String name, CommandProcessor cmdProc) pairs*/
    protected HashMap<String,CommandProcessor>internalCmdTable;
    private MacroProcessor macroProc;
    
    /**
     * Creates new command dispatcher
     * @param kernel Kernel of current application
     */
    public CommandDispatcher(Kernel kernel) {             
    	this.kernel = kernel;
    	cons = kernel.getConstruction();  
    	app = kernel.getApplication();                    
    }
    
    /**
     * Returns a set with all command names available
     * in the GeoGebra input field.
     * @return Set of all command names
     */
    public Set<String> getPublicCommandSet() {
    	if (cmdTable == null) {
    		initCmdTable();
    	}  
    	
    	return cmdTable.keySet();
    }
    
    
    /**
     * Returns an array of sets containing the command names 
     * found in each table of the array cmdSubTable.
     */
    public Set[] getPublicCommandSubSets() {
    	
    	if (cmdTable == null) {
    		initCmdTable();
    	}  
    	
    	Set[] subSet = new Set[tableCount];  	
        for(int i = 0; i < tableCount; i++){
        	subSet[i] = cmdSubTable[i].keySet();
        }
  
    	return subSet;
    }
    
    
    
    /**
     * @param c Command to be executed
     * @param labelOutput specifies if output GeoElements of this command should get labels
     * @throws MyError in case command execution fails
     * @return Geos created by the command
     */
    final public GeoElement[] processCommand(Command c, boolean labelOutput)
        throws MyError {
    	
    	if (cmdTable == null) {
    		initCmdTable();
    	}    	        

        // switch on macro mode to avoid labeling of output if desired
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        if (!labelOutput)
            cons.setSuppressLabelCreation(true);
        
        // cmdName
        String cmdName = c.getName();
        CommandProcessor cmdProc;
        
        // MACRO: is there a macro with this command name?        
        Macro macro = kernel.getMacro(cmdName);
        if (macro != null) {    
        	c.setMacro(macro);
        	cmdProc = macroProc;
        } 
        // STANDARD CASE
        else {
        	// get CommandProcessor object for command name from command table
        	cmdProc = (CommandProcessor) cmdTable.get(cmdName);    
        	
        	if (cmdProc == null)
        		cmdProc = internalCmdTable.get(cmdName);
        }
                
        GeoElement[] ret = null;
        try {            
	        ret = cmdProc.process(c);	                       	        	        
        } 
        catch (MyError e) {
        	cons.setSuppressLabelCreation(oldMacroMode);
            throw e;
        } catch (Exception e) {        	  
            cons.setSuppressLabelCreation(oldMacroMode);        	  
            e.printStackTrace();
            if(cmdProc == null)
            	throw new MyError(app, app.getError("UnknownCommand") + " : " + 
            		app.getCommand(c.getName()));
            else 
            	throw new MyError(app, app.getError("CAS.GeneralErrorMessage"));
        }
        
        // remember macro command used:
        // this is needed when a single tool A[] is exported to find
        // all other tools that are needed for A[]
        if (macro != null)
        	cons.addUsedMacro(macro);
        
              		
        cons.setSuppressLabelCreation(oldMacroMode);        
        
        return ret;
    }
    
    /**
     * Fills the string-command map
     */
    protected void initCmdTable() {    	 
    	macroProc = new MacroProcessor(kernel);    	    	
    	
    	// external commands: visible to users    
    	cmdTable = new HashMap<String,CommandProcessor>(500);
    	
    	cmdSubTable = new HashMap[tableCount];
    	for(int i = 0; i<tableCount; i++)
    		cmdSubTable[i] = new HashMap<String,CommandProcessor>(500);
    
    	


       	//=================================================================
       	// Algebra & Numbers
    	//=============================================================
       	cmdTable.put("Mod", new CmdMod(kernel));
       	cmdTable.put("Div", new CmdDiv(kernel));
       	cmdTable.put("Min", new CmdMin(kernel));
       	cmdTable.put("Max", new CmdMax(kernel));
    	cmdTable.put("LCM", new CmdLCM(kernel));
    	cmdTable.put("GCD", new CmdGCD(kernel));
    	cmdTable.put("Product", new CmdProduct(kernel));
    	cmdTable.put("Expand", new CmdExpand(kernel));
    	cmdTable.put("Factor", new CmdFactor(kernel));
    	cmdTable.put("Simplify", new CmdSimplify(kernel));   
    	cmdTable.put("PrimeFactors", new CmdPrimeFactors(kernel));     	    
    	
    	cmdSubTable[TABLE_ALGEBRA].putAll(cmdTable);
    	cmdTable.clear();
      	
    	
      	//=================================================================
      	// Geometry
    	//=============================================================
       	cmdTable.put("Line", new CmdLine(kernel));	   
     	cmdTable.put("Ray", new CmdRay(kernel));	   
    	cmdTable.put("AngularBisector", new CmdAngularBisector(kernel));
    	cmdTable.put("OrthogonalLine", new CmdOrthogonalLine(kernel));
    	cmdTable.put("Tangent", new CmdTangent(kernel));
    	cmdTable.put("Segment", new CmdSegment(kernel));
    	cmdTable.put("Slope", new CmdSlope(kernel));	
    	cmdTable.put("Angle", new CmdAngle(kernel));
    	cmdTable.put("Direction", new CmdDirection(kernel));
    	
    	cmdTable.put("Point", new CmdPoint(kernel));	
    	cmdTable.put("Midpoint", new CmdMidpoint(kernel));	
    	cmdTable.put("LineBisector", new CmdLineBisector(kernel));	 
    	cmdTable.put("Intersect", new CmdIntersect(kernel));
    	
    	cmdTable.put("Distance", new CmdDistance(kernel));	   
    	cmdTable.put("Length", new CmdLength(kernel));	  
    	   
    	cmdTable.put("Radius", new CmdRadius(kernel));	
    	cmdTable.put("CircleArc", new CmdCircleArc(kernel));	
    	cmdTable.put("Arc", new CmdArc(kernel));
    	cmdTable.put("Sector", new CmdSector(kernel));
    	cmdTable.put("CircleSector", new CmdCircleSector(kernel));	   
    	cmdTable.put("CircumcircleSector", new CmdCircumcircleSector(kernel));	     
    	cmdTable.put("CircumcircleArc", new CmdCircumcircleArc(kernel));	   
    		   
    	cmdTable.put("Polygon", new CmdPolygon(kernel));
    	cmdTable.put("RigidPolygon", new CmdRigidPolygon(kernel));	   
    	cmdTable.put("Area", new CmdArea(kernel));	
    		   
    	// Philipp Weissenbacher 10-04-2007
    	cmdTable.put("Circumference", new CmdCircumference(kernel));
    	cmdTable.put("Perimeter", new CmdPerimeter(kernel));
    	// Philipp Weissenbacher 10-04-2007
    	   
    	cmdTable.put("Locus", new CmdLocus(kernel));	   
    	cmdTable.put("Centroid", new CmdCentroid(kernel));	   
    	cmdTable.put("Vertex", new CmdVertex(kernel));	
    	
    	cmdTable.put("PolyLine", new CmdPolyLine(kernel));	   
    		
    	//Mathieu Blossier
    	cmdTable.put("PointIn", new CmdPointIn(kernel));   
    	
    	cmdSubTable[TABLE_GEOMETRY].putAll(cmdTable);
    	cmdTable.clear();
    	
    	
    	
      	//=============================================================
      	// text
    	//=============================================================
      	cmdTable.put("Text", new CmdText(kernel));    	
      	cmdTable.put("LaTeX", new CmdLaTeX(kernel));
      	cmdTable.put("LetterToUnicode", new CmdLetterToUnicode(kernel));    	
      	cmdTable.put("TextToUnicode", new CmdTextToUnicode(kernel));    	
      	cmdTable.put("UnicodeToText", new CmdUnicodeToText(kernel));    
      	cmdTable.put("UnicodeToLetter", new CmdUnicodeToLetter(kernel));    
      	cmdTable.put("FractionText", new CmdFractionText(kernel));   
      	cmdTable.put("TableText", new CmdTableText(kernel)); 
      	cmdTable.put("VerticalText", new CmdVerticalText(kernel));	   
    	cmdTable.put("RotateText", new CmdRotateText(kernel));	   
      	
    	cmdSubTable[TABLE_TEXT].putAll(cmdTable);
    	cmdTable.clear();
    	
    	
      	//=============================================================
      	// logical	
    	//=============================================================
      	cmdTable.put("If", new CmdIf(kernel));
      	cmdTable.put("CountIf", new CmdCountIf(kernel));   
      	cmdTable.put("IsInteger", new CmdIsInteger(kernel));
      	cmdTable.put("KeepIf", new CmdKeepIf(kernel));  
      	cmdTable.put("Relation", new CmdRelation(kernel));	 
      	cmdTable.put("Defined", new CmdDefined(kernel));
      	cmdTable.put("IsInRegion", new CmdIsInRegion(kernel));    

      	cmdSubTable[TABLE_LOGICAL].putAll(cmdTable);
    	cmdTable.clear();
      	
    	
    	
    	
    	//=============================================================
    	// functions & calculus
    	//=============================================================
    	cmdTable.put("Root", new CmdRoot(kernel));	
    	cmdTable.put("TurningPoint", new CmdTurningPoint(kernel));
    	cmdTable.put("Polynomial", new CmdPolynomial(kernel));	
    	cmdTable.put("Function", new CmdFunction(kernel));	   
    	cmdTable.put("Extremum", new CmdExtremum(kernel));	
    	cmdTable.put("CurveCartesian", new CmdCurveCartesian(kernel));
    	cmdTable.put("Derivative", new CmdDerivative(kernel));	
    	cmdTable.put("Integral", new CmdIntegral(kernel));	   
    	cmdTable.put("LowerSum", new CmdLowerSum(kernel));
    	cmdTable.put("TaylorSeries", new CmdTaylorSeries(kernel));	 
    	cmdTable.put("UpperSum", new CmdUpperSum(kernel));  
    	cmdTable.put("TrapezoidalSum", new CmdTrapezoidalSum(kernel)); 
    	cmdTable.put("Limit", new CmdLimit(kernel));   
    	cmdTable.put("LimitBelow", new CmdLimitBelow(kernel));   
    	cmdTable.put("LimitAbove", new CmdLimitAbove(kernel));   
    	cmdTable.put("Factors", new CmdFactors(kernel));
    	cmdTable.put("Degree", new CmdDegree(kernel));   
    	cmdTable.put("Coefficients", new CmdCoefficients(kernel));   
    	cmdTable.put("PartialFractions", new CmdPartialFractions(kernel));   
    	cmdTable.put("Numerator", new CmdNumerator(kernel));   
    	cmdTable.put("Denominator", new CmdDenominator(kernel)); 
    	cmdTable.put("ComplexRoot", new CmdComplexRoot(kernel));	   
    	cmdTable.put("SolveODE", new CmdSolveODE(kernel));	   
    	cmdTable.put("Iteration", new CmdIteration(kernel));
    	cmdTable.put("IterationList", new CmdIterationList(kernel));
    	cmdTable.put("PathParameter", new CmdPathParameter(kernel));     
    	
    	cmdSubTable[TABLE_FUNCTION].putAll(cmdTable);
    	cmdTable.clear();
    	
    	
    	//=============================================================
    	// conics
    	//=============================================================
    	cmdTable.put("Ellipse", new CmdEllipse(kernel));	   
    	cmdTable.put("Hyperbola", new CmdHyperbola(kernel));	   
    	cmdTable.put("SecondAxisLength", new CmdSecondAxisLength(kernel));	
    	cmdTable.put("SecondAxis", new CmdSecondAxis(kernel));	  
    	cmdTable.put("Directrix", new CmdDirectrix(kernel));	   
    	cmdTable.put("Diameter", new CmdDiameter(kernel));	   
    	cmdTable.put("Conic", new CmdConic(kernel));	   
    	cmdTable.put("FirstAxis", new CmdFirstAxis(kernel));	   
    	cmdTable.put("Circle", new CmdCircle(kernel));	 
    	cmdTable.put("Semicircle", new CmdSemicircle(kernel));	   
    	cmdTable.put("FirstAxisLength", new CmdFirstAxisLength(kernel));	   
    	cmdTable.put("Parabola", new CmdParabola(kernel));	 
    	cmdTable.put("Focus", new CmdFocus(kernel));	
    	cmdTable.put("Parameter", new CmdParameter(kernel));
    	cmdTable.put("Asymptote", new CmdAsymptote(kernel));
    	cmdTable.put("Center", new CmdCenter(kernel));	
    	cmdTable.put("Polar", new CmdPolar(kernel));	 
    	// linear eccentricity (used in Germany etc) LinearExcentricity[]
    	cmdTable.put("Excentricity", new CmdExcentricity(kernel));	  
    	// eccentricity
    	cmdTable.put("Eccentricity", new CmdEccentricity(kernel));	  
    	cmdTable.put("Axes", new CmdAxes(kernel));	  
    	
    	cmdSubTable[TABLE_CONIC].putAll(cmdTable);
    	cmdTable.clear();
    	

    	
    	//=============================================================
    	// lists
    	//=============================================================
    	cmdTable.put("Sort", new CmdSort(kernel));
    	cmdTable.put("First", new CmdFirst(kernel));
    	cmdTable.put("Last", new CmdLast(kernel));
    	cmdTable.put("Take", new CmdTake(kernel));
    	cmdTable.put("RemoveUndefined", new CmdRemoveUndefined(kernel));
    	cmdTable.put("Reverse", new CmdReverse(kernel));
    	cmdTable.put("Element", new CmdElement(kernel));
    	cmdTable.put("Append", new CmdAppend(kernel));   
    	cmdTable.put("Join", new CmdJoin(kernel));   
    	cmdTable.put("Insert", new CmdInsert(kernel));   
    	cmdTable.put("Union", new CmdUnion(kernel));   
    	cmdTable.put("Sequence", new CmdSequence(kernel));   
    	cmdTable.put("SelectedElement", new CmdSelectedElement(kernel));     	                  
    	cmdTable.put("SelectedIndex", new CmdSelectedIndex(kernel)); 
    	cmdTable.put("RandomElement", new CmdRandomElement(kernel));
    	
    	cmdSubTable[TABLE_LIST].putAll(cmdTable);
    	cmdTable.clear();
    	
    	
    	
    	//=============================================================
    	// charts
    	//=============================================================	
    	cmdTable.put("BarChart", new CmdBarChart(kernel));    	
    	cmdTable.put("BoxPlot", new CmdBoxPlot(kernel));    	
    	cmdTable.put("Histogram", new CmdHistogram(kernel)); 
    	cmdTable.put("DotPlot", new CmdDotPlot(kernel)); //G.Sturr 2010-8-10
    	cmdTable.put("StemPlot", new CmdStemPlot(kernel));    
    	
    	cmdSubTable[TABLE_CHARTS].putAll(cmdTable);
    	cmdTable.clear();
    	
    	
    	
    	
    	//=============================================================
    	// statistics
    	//=============================================================
    	cmdTable.put("Sum", new CmdSum(kernel));
    	cmdTable.put("Mean", new CmdMean(kernel));
    	cmdTable.put("Variance", new CmdVariance(kernel));
    	cmdTable.put("SD", new CmdSD(kernel));
    	cmdTable.put("SampleVariance", new CmdSampleVariance(kernel));
    	cmdTable.put("SampleSD", new CmdSampleSD(kernel));
    	cmdTable.put("Median", new CmdMedian(kernel));
    	cmdTable.put("Q1", new CmdQ1(kernel));
    	cmdTable.put("Q3", new CmdQ3(kernel));
    	cmdTable.put("Mode", new CmdMode(kernel));	
    	cmdTable.put("SigmaXX", new CmdSigmaXX(kernel));
    	cmdTable.put("SigmaXY", new CmdSigmaXY(kernel));
    	cmdTable.put("SigmaYY", new CmdSigmaYY(kernel));
    	cmdTable.put("Covariance", new CmdCovariance(kernel));
    	cmdTable.put("SXY", new CmdSXY(kernel));
    	cmdTable.put("SXX", new CmdSXX(kernel));
    	cmdTable.put("SYY", new CmdSYY(kernel));
    	cmdTable.put("MeanX", new CmdMeanX(kernel));
    	cmdTable.put("MeanY", new CmdMeanY(kernel));
    	cmdTable.put("PMCC", new CmdPMCC(kernel));
    	  	
    	cmdTable.put("FitLineY", new CmdFitLineY(kernel));
    	cmdTable.put("FitLineX", new CmdFitLineX(kernel));
    	cmdTable.put("FitPoly", new CmdFitPoly(kernel));
    	cmdTable.put("FitExp", new CmdFitExp(kernel));
    	cmdTable.put("FitLog", new CmdFitLog(kernel));
    	cmdTable.put("FitPow", new CmdFitPow(kernel));
    	cmdTable.put("Fit",new CmdFit(kernel));
    	cmdTable.put("FitGrowth",new CmdFitGrowth(kernel));
    	cmdTable.put("FitSin", new CmdFitSin(kernel));   
    	cmdTable.put("FitLogistic", new CmdFitLogistic(kernel));  
    	cmdTable.put("SumSquaredErrors",new CmdSumSquaredErrors(kernel));
    	cmdTable.put("RSquare",new CmdRSquare(kernel));
    	
    	
    	
    	cmdTable.put("Sample", new CmdSample(kernel));	  
    	cmdTable.put("Rank", new CmdRank(kernel));
    	cmdTable.put("Shuffle", new CmdShuffle(kernel));
    	
    	
    	cmdSubTable[TABLE_STATISTICS].putAll(cmdTable);
    	cmdTable.clear();
    	
    	
    	//=============================================================
    	// probability
    	//=============================================================
    	cmdTable.put("Random", new CmdRandom(kernel));   
    	cmdTable.put("RandomNormal", new CmdRandomNormal(kernel));
    	cmdTable.put("RandomUniform", new CmdRandomUniform(kernel));  
    	cmdTable.put("RandomBinomial", new CmdRandomBinomial(kernel));   
    	cmdTable.put("RandomPoisson", new CmdRandomPoisson(kernel)); 
    	
    	cmdTable.put("Normal", new CmdNormal(kernel));
    	cmdTable.put("InverseNormal", new CmdInverseNormal(kernel));
    	cmdTable.put("Binomial", new CmdBinomial(kernel));
    	cmdTable.put("TDistribution", new CmdTDistribution(kernel));  
    	cmdTable.put("InverseTDistribution", new CmdInverseTDistribution(kernel));  
    	cmdTable.put("FDistribution", new CmdFDistribution(kernel));  
    	cmdTable.put("InverseFDistribution", new CmdInverseFDistribution(kernel));     	
    	cmdTable.put("Gamma", new CmdGamma(kernel));  
    	cmdTable.put("InverseGamma", new CmdInverseGamma(kernel));  
    	cmdTable.put("Cauchy", new CmdCauchy(kernel));  
    	cmdTable.put("InverseCauchy", new CmdInverseCauchy(kernel));  
    	cmdTable.put("ChiSquared", new CmdChiSquared(kernel));  
    	cmdTable.put("InverseChiSquared", new CmdInverseChiSquared(kernel));  
    	cmdTable.put("Exponential", new CmdExponential(kernel));  
    	cmdTable.put("InverseExponential", new CmdInverseExponential(kernel));  
    	cmdTable.put("HyperGeometric", new CmdHyperGeometric(kernel));  
    	cmdTable.put("InverseHyperGeometric", new CmdInverseHyperGeometric(kernel));  
    	cmdTable.put("Pascal", new CmdPascal(kernel));  
    	cmdTable.put("InversePascal", new CmdInversePascal(kernel));  
    	cmdTable.put("Weibull", new CmdWeibull(kernel));  
    	cmdTable.put("InverseWeibull", new CmdInverseWeibull(kernel));
    	cmdTable.put("Zipf", new CmdZipf(kernel));  
    	cmdTable.put("InverseZipf", new CmdInverseZipf(kernel));
    	
    	cmdSubTable[TABLE_PROBABILITY].putAll(cmdTable);
    	cmdTable.clear();
    		
    	
    	//=============================================================
    	// vector & matrix
    	//=============================================================
    	cmdTable.put("UnitVector", new CmdUnitVector(kernel));	   
    	cmdTable.put("Vector", new CmdVector(kernel));	
    	cmdTable.put("UnitOrthogonalVector", new CmdUnitOrthogonalVector(kernel));	
    	cmdTable.put("OrthogonalVector", new CmdOrthogonalVector(kernel));
    	cmdTable.put("Invert", new CmdInvert(kernel));   
    	cmdTable.put("Transpose", new CmdTranspose(kernel));   
    	cmdTable.put("rref", new CmdReducedRowEchelonForm(kernel));   
    	cmdTable.put("Determinant", new CmdDeterminant(kernel));   
    	
    	cmdSubTable[TABLE_VECTOR].putAll(cmdTable);
    	cmdTable.clear();
    	
    	//=============================================================
    	// transformations
    	//=============================================================
    	cmdTable.put("Mirror", new CmdMirror(kernel));
    	cmdTable.put("Dilate", new CmdDilate(kernel));	
    	cmdTable.put("Rotate", new CmdRotate(kernel));	
    	cmdTable.put("Translate", new CmdTranslate(kernel));
    	cmdTable.put("Shear", new CmdShear(kernel));
    	cmdTable.put("Stretch", new CmdStretch(kernel));
    	
    	cmdSubTable[TABLE_TRANSFORMATION].putAll(cmdTable);
    	cmdTable.clear();
    	
    	
    	//=============================================================
    	// spreadsheet
    	//=============================================================
    	cmdTable.put("CellRange", new CmdCellRange(kernel));  // cell range for spreadsheet like A1:A5
    	cmdTable.put("Row", new CmdRow(kernel));    	
    	cmdTable.put("Column", new CmdColumn(kernel));  
    	cmdTable.put("ColumnName", new CmdColumnName(kernel)); 
    	cmdTable.put("FillRow", new CmdFillRow(kernel));
      	cmdTable.put("FillColumn", new CmdFillColumn(kernel));
      	cmdTable.put("FillCells", new CmdFillCells(kernel));   	
      	cmdTable.put("Cell", new CmdCell(kernel));
    	
      	cmdSubTable[TABLE_SPREADSHEET].putAll(cmdTable);
    	cmdTable.clear();

    	
      	//=============================================================	
      	// scripting
    	//=============================================================
      	cmdTable.put("CopyFreeObject", new CmdCopyFreeObject(kernel));
    	cmdTable.put("SetColor", new CmdSetColor(kernel));
    	cmdTable.put("SetDynamicColor", new CmdSetDynamicColor(kernel));
    	cmdTable.put("SetConditionToShowObject", new CmdSetConditionToShowObject(kernel));
    	cmdTable.put("SetFilling", new CmdSetFilling(kernel));
    	cmdTable.put("SetLineThickness", new CmdSetLineThickness(kernel));
    	cmdTable.put("SetLineStyle", new CmdLineStyle(kernel));
    	cmdTable.put("SetPointStyle", new CmdSetPointStyle(kernel));
    	cmdTable.put("SetPointSize", new CmdSetPointSize(kernel));
    	cmdTable.put("SetFixed", new CmdSetFixed(kernel));
    	cmdTable.put("Rename", new CmdRename(kernel));
    	cmdTable.put("HideLayer", new CmdHideLayer(kernel));
       	cmdTable.put("ShowLayer", new CmdShowLayer(kernel));
       	cmdTable.put("SetCoords", new CmdSetCoords(kernel));
       	cmdTable.put("Pan", new CmdPan(kernel));
       	cmdTable.put("ZoomIn", new CmdZoomIn(kernel));
       	cmdTable.put("ZoomOut", new CmdZoomOut(kernel));
       	cmdTable.put("SelectObjects", new CmdSelectObjects(kernel));
       	cmdTable.put("SetLayer", new CmdSetLayer(kernel));
       	cmdTable.put("SetCaption", new CmdSetCaption(kernel));
       	cmdTable.put("SetLabelMode", new CmdSetLabelMode(kernel));
       	cmdTable.put("SetTooltipMode", new CmdSetTooltipMode(kernel));
       	cmdTable.put("UpdateConstruction", new CmdUpdateConstruction(kernel));
       	cmdTable.put("SetValue", new CmdSetValue(kernel));
       	cmdTable.put("PlaySound", new CmdPlaySound(kernel));
       		
       	cmdSubTable[TABLE_SCRIPTING].putAll(cmdTable);
    	cmdTable.clear();
       	

    	
    	//=================================================================
      	// Other ???
    	//=============================================================
    	
    	// Victor Franco Espino 18-04-2007: New commands
    	cmdTable.put("AffineRatio", new CmdAffineRatio(kernel));
    	cmdTable.put("CrossRatio", new CmdCrossRatio(kernel));
    	cmdTable.put("CurvatureVector", new CmdCurvatureVector(kernel));
    	cmdTable.put("Curvature", new CmdCurvature(kernel));
    	cmdTable.put("OsculatingCircle", new CmdOsculatingCircle(kernel));
    	// Victor Franco Espino 18-04-2007: End new commands
    	
    	cmdTable.put("ConstructionStep", new CmdConstructionStep(kernel));
    	cmdTable.put("Object", new CmdObject(kernel));  
    	cmdTable.put("Name", new CmdName(kernel));

    	//cmdTable.put("Intersection", new CmdIntersection(kernel)); 
       	cmdTable.put("Intersection", new CmdIntersect(kernel)); 
    	
       	cmdTable.put("Delete", new CmdDelete(kernel));	
       	cmdTable.put("Corner", new CmdCorner(kernel));
    	cmdTable.put("AxisStepX", new CmdAxisStepX(kernel));   
    	cmdTable.put("AxisStepY", new CmdAxisStepY(kernel));   
    	
    	cmdTable.put("DynamicCoordinates", new CmdDynamicCoordinates(kernel));  
       	
    	cmdTable.put("PointList", new CmdPointList(kernel)); 
    	cmdTable.put("RootList", new CmdRootList(kernel));   
    	
    	cmdTable.put("ApplyMatrix", new CmdApplyMatrix(kernel)); 
    	cmdTable.put("SlowPlot", new CmdSlowPlot(kernel));	   
    	cmdTable.put("ToolImage", new CmdToolImage(kernel));	   
    	cmdTable.put("Voronoi", new CmdVoronoi(kernel));     	                  
    	cmdTable.put("Hull", new CmdHull(kernel));     	                  
    	cmdTable.put("ConvexHull", new CmdConvexHull(kernel));     	                  
    	cmdTable.put("MinimumSpanningTree", new CmdMinimumSpanningTree(kernel));     	                  
    	cmdTable.put("DelauneyTriangulation", new CmdDelauneyTriangulation(kernel));     	                  
    	cmdTable.put("TravelingSalesman", new CmdTravelingSalesman(kernel)); 
    	cmdTable.put("ShortestDistance", new CmdShortestDistance(kernel));     	                  
     	 	  
    	cmdTable.put("ParseToNumber", new CmdParseToNumber(kernel));
       	cmdTable.put("ParseToFunction", new CmdParseToFunction(kernel)); 
    	cmdTable.put("Slider", new CmdSlider(kernel));     
    	cmdTable.put("ClosestPoint", new CmdClosestPoint(kernel));     
    	
    	
    	cmdSubTable[TABLE_OTHER].putAll(cmdTable);
    	cmdTable.clear();
    	
    	
    	//=================================================================
      	// Put all of the sub Tables together to create cmdTable
    	
    	for(int i = 0; i < tableCount; i++)
    		cmdTable.putAll(cmdSubTable[i]);
    		
    	
    	// internal command table for commands that should not be visible to the user
    	internalCmdTable = new HashMap<String,CommandProcessor>();
    	// support parsing diff() results back from Maxima
    	internalCmdTable.put("diff", new CmdDerivative(kernel));
    }


}
