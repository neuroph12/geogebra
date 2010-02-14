package geogebra.cas;

import geogebra.cas.view.CASView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;
import jasymca.GeoGebraJasymca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.mathpiper.interpreters.EvaluationResponse;
import org.mathpiper.interpreters.Interpreter;
import org.mathpiper.interpreters.Interpreters;

/**
 * This class provides an interface for GeoGebra to use the computer algebra
 * systems Jasymca and MathPiper.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS {
	
	final public String RB_GGB_TO_MathPiper = "/geogebra/cas/ggb2mathpiper";

	private Interpreter ggbMathPiper;
	private GeoGebraJasymca ggbJasymca;	
	private StringBuilder sbInsertSpecial, sbReplaceIndices, sbPolyCoeffs;
	private Application app;
	private Kernel kernel;
	private CASparser casParser;	
	private ResourceBundle ggb2MathPiper;

	public GeoGebraCAS(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();
		casParser = new CASparser(kernel);
		
		sbInsertSpecial = new StringBuilder(80);
		sbReplaceIndices = new StringBuilder(80);
		
		initCAS();
	}
	
	private void initCAS() {
		ggbMathPiper = null;
		getMathPiper();
		ggbJasymca = new GeoGebraJasymca();		
	}

	
    /** 
     * Evaluates a JASYMCA expression and returns the result as a string,
     * e.g. exp = "diff(x^2,x)" returns "2*x".
     * @return result string, null possible
     */ 
    final public String evaluateJASYMCA(String exp) {    
    	String result = ggbJasymca.evaluate(exp);      	
    	  
    	// to handle x(A) and x(B) they are converted
    	// to unicode strings in ExpressionNode, 
    	// we need to convert them back here
    	result = insertSpecialChars(result);
    	
    	//System.out.println("exp for JASYMCA: " + exp);  
    	//System.out.println("         result: " + result);  
    	        
        return result;
    }

    /**
	 * Evaluates a MathPiper expression and returns the result as a string in MathPiper syntax, 
	 * e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String exp) {
		try {
			String result;
						
			// MathPiper has problems with indices like a_3, b_{12}
			exp = replaceIndices(exp);
			
			final boolean debug = true;
			if (debug) Application.debug("Expression for mathPiper: "+exp);
			
			// evaluate the MathPiper expression
			Interpreter mathpiper = getMathPiper();
			response = mathpiper.evaluate(exp);
			
			if (response.isExceptionThrown())
			{
				System.err.println("evaluateMathPiper: "+exp+"\n  Exception: "+response.getExceptionMessage());
				return null;
			}
			result = response.getResult();
			
			if (debug) System.out.println("Result: "+result);
					
			// undo special character handling
			result = insertSpecialChars(result);

			return result;
		} catch (Throwable th) {
			//MathPiper.Evaluate("restart;");
			th.printStackTrace();
			return null;
		} 
	}
				
	/**
	 * Evaluates a MathPiper expression wrapped in a command and returns the result as a string, 
	 * e.g. wrapperCommand = "Factor", exp = "3*(a+b)" evaluates "Factor(3*(a+b)" and 
	 * returns "3*a+3*b".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String wrapperCommand, String exp) {
		StringBuilder sb = new StringBuilder(exp.length()+wrapperCommand.length()+2);
		sb.append(wrapperCommand);
		sb.append('(');
		sb.append(exp);				
		sb.append(')');
		return evaluateMathPiper(sb.toString());
	}	
	
	/**
	 * Returns the error message of the last MathPiper evaluation.
	 * @return null if last evaluation was successful.
	 */
	final synchronized public String getMathPiperError() {
		if (response != null)
			return response.getExceptionMessage();
		else 
			return null;
	}
	
	EvaluationResponse response ;	
	
	private synchronized Interpreter getMathPiper() {				
		if (ggbMathPiper == null) {

			ggbMathPiper = Interpreters.getSynchronousInterpreter();
			boolean success = initMyMathPiperFunctions();
			
			if (!success) {
				System.out.println("MathPiper creation failed with null scriptbase");
				// where to find MathPiper scripts
				//eg docBase = "jar:http://www.geogebra.org/webstart/alpha/geogebra_cas.jar!/";
				String scriptBase = "jar:" + app.getCodeBase().toString() + 
											Application.CAS_JAR_NAME + "!/";			
				ggbMathPiper = Interpreters.getSynchronousInterpreter(scriptBase);
				success = initMyMathPiperFunctions();
				if (!success)
					System.err.println("MathPiper creation failed again with scriptbase: " + scriptBase);
			}
		}
		
		return ggbMathPiper;
	}	
	
	/**
	 * Initialize special commands needed in our ggbMathPiper instance,e.g.
	 * getPolynomialCoeffs(exp,x).
	 */
	private synchronized boolean initMyMathPiperFunctions() {		
// Expand expression and get polynomial coefficients using MathPiper:
//		getPolynomialCoeffs(expr,x) :=
//			       If( CanBeUni(expr),
//			           [
//							Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));			           ],
//			           {};
//			      );
		String strGetPolynomialCoeffs = "getPolynomialCoeffs(expr,x) := If( CanBeUni(expr),[ Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));],{});";
		EvaluationResponse resp = ggbMathPiper.evaluate(strGetPolynomialCoeffs);
		if (resp.isExceptionThrown()) {
			return false;
		}
		
		// define constant for Degree
		response = ggbMathPiper.evaluate("Degree := 180/pi;");
		
		// set default numeric precision to 16 significant figures
		ggbMathPiper.evaluate("BuiltinPrecisionSet(16);");
		
		// Rules for equation manipulation
		// allow certain commands for equations
		ggbMathPiper.evaluate("Simplify(_x == _y)  <-- Simplify(x) == Simplify(y);");
		ggbMathPiper.evaluate("Factor(_x == _y)  <-- Factor(x) == Factor(y);");
		ggbMathPiper.evaluate("Expand(_x == _y)  <-- Expand(x) == Expand(y);");
		ggbMathPiper.evaluate("ExpandBrackets(_x == _y)  <-- ExpandBrackets(x) == ExpandBrackets(y);");
		ggbMathPiper.evaluate("Sqrt(_x == _y)  <-- Sqrt(x) == Sqrt(y);");
		ggbMathPiper.evaluate("Exp(_x == _y)  <-- Exp(x) == Exp(y);");
		ggbMathPiper.evaluate("Ln(_x == _y)  <-- Ln(x) == Ln(y);");
		
		// arithmetic for equations
		ggbMathPiper.evaluate("(_x == _y) + _z <-- Simplify(x + z == y + z);");
		ggbMathPiper.evaluate("_z + (_x == _y) <-- Simplify(z + x == z + y);");
		ggbMathPiper.evaluate("(_x == _y) - _z <-- Simplify(x - z == y - z);");
		ggbMathPiper.evaluate("_z - (_x == _y) <-- Simplify(z - x == z - y);");
		ggbMathPiper.evaluate("(_x == _y) * _z <-- Simplify(x * z == y * z);");
		ggbMathPiper.evaluate("_z * (_x == _y) <-- Simplify(z * x == z * y);");
		ggbMathPiper.evaluate("(_x == _y) / _z <-- Simplify(x / z == y / z);");
		ggbMathPiper.evaluate("_z / (_x == _y) <-- Simplify(z / x == z / y);");
		ggbMathPiper.evaluate("(_x == _y) ^ _z <-- Simplify(x ^ z == y ^ z);");
		ggbMathPiper.evaluate("_z ^ (_x == _y) <-- Simplify(z ^ x == z ^ y);");
		
		return true;
	}
	

	final public String simplifyMathPiper(String exp) {
		return evaluateMathPiper("Simplify", exp );
	}
	
	final public String factorMathPiper(String exp) {
		return evaluateMathPiper("Factor", exp );
	}

	final public String expandMathPiper(String exp) {
		return evaluateMathPiper("ExpandBrackets", exp );
	}
	
	private HashMap getPolynomialCoeffsCache = new HashMap(50);
	private StringBuilder getPolynomialCoeffsSB = new StringBuilder();
	
	/**
	 * Expands the given MathPiper expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(String MathPiperExp, String variable) {
		//return ggbJasymca.getPolynomialCoeffs(MathPiperExp, variable);
		
		getPolynomialCoeffsSB.setLength(0);
		getPolynomialCoeffsSB.append(MathPiperExp);
		getPolynomialCoeffsSB.append(':');
		getPolynomialCoeffsSB.append(variable);
		
		String result = (String)(getPolynomialCoeffsCache.get(getPolynomialCoeffsSB.toString()));
		
		if (result != null) {
			//Application.debug("using cached result: "+result);
			// remove { } to get "b, 0, 3*a"
			result = result.substring(1, result.length()-1);
			
			// split to get coefficients array ["b", "0", "3*a"]
			String [] coeffs = result.split(",");				    
	        return coeffs;	
		}
		
		
		if (sbPolyCoeffs == null)
			sbPolyCoeffs = new StringBuilder();
		else
			sbPolyCoeffs.setLength(0);
		
		
		/* replaced Michael Borcherds 2009-02-08
		 * doesn't seem to work properly polyCoeffsbug.ggb
		 */
		sbPolyCoeffs.append("getPolynomialCoeffs(");
		sbPolyCoeffs.append(MathPiperExp);
		sbPolyCoeffs.append(',');
		sbPolyCoeffs.append(variable);
		sbPolyCoeffs.append(')');
		

		// Expand expression and get polynomial coefficients using MathPiper:
		// Prog( Local(exp), 
		//   	 exp := ExpandBrackets( 3*a*x^2 + b ), 
		//		 Coef(exp, x, 0 .. Degree(exp, x)) 
		// )		
		//sbPolyCoeffs.append("Prog( Local(exp), exp := ExpandBrackets(");
		//sbPolyCoeffs.append(MathPiperExp);
		//sbPolyCoeffs.append("), Coef(exp, x, 0 .. Degree(exp, x)))");
			
		try {
			// expand expression and get coefficients of
			// "3*a*x^2 + b" in form "{ b, 0, 3*a }" 
			result = evaluateMathPiper(sbPolyCoeffs.toString());
			
			// empty list of coefficients -> return null
			if ("{}".equals(result) || "".equals(result) || result == null) 
				return null;
			
			// cache result
			//Application.debug("caching result: "+result);		
			getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(), result);

			//Application.debug(sbPolyCoeffs+"");
			//Application.debug(result+"");
			
			// remove { } to get "b, 0, 3*a"
			result = result.substring(1, result.length()-1);
			
			// split to get coefficients array ["b", "0", "3*a"]
			String [] coeffs = result.split(",");				    
            return coeffs;						
		} 
		catch(Exception e) {
			Application.debug("GeoGebraCAS.getPolynomialCoeffs(): " + e.getMessage());
			//e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * Converts all index characters ('_', '{', '}') in the given String
	 * to "unicode" + charactercode + DELIMITER Strings. This is needed because
	 * MathPiper does not handle indices correctly.
	 */
	private synchronized String replaceIndices(String str) {
		int len = str.length();
		sbReplaceIndices.setLength(0);
		
		boolean foundIndex = false;

		// convert every single character and append it to sb
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = (int) c;
			
			boolean replaceCharacter = false;			
			switch (c) {
				case '_': // start index
					foundIndex = true;
					replaceCharacter = true;
					break;
										
				case '{': 	
					if (foundIndex) {
						replaceCharacter = true;						
					}					
					break;					
					
				case '}':
					if (foundIndex) {
						replaceCharacter = true;
						foundIndex = false; // end of index
					}					
					break;
					
				default:
					replaceCharacter = false;
			}
			
			if (replaceCharacter) {
				sbReplaceIndices.append(ExpressionNode.UNICODE_PREFIX);
				sbReplaceIndices.append(code);
				sbReplaceIndices.append(ExpressionNode.UNICODE_DELIMITER);
			} else {
				sbReplaceIndices.append(c);
			}
		}
					
		return sbReplaceIndices.toString();
	}

	/**
	 * Reverse operation of removeSpecialChars().
	 * @see ExpressionNode.operationToString() for XCOORD, YCOORD
	 */
	private String insertSpecialChars(String str) {
		int len = str.length();
		sbInsertSpecial.setLength(0);

		// convert every single character and append it to sb
		char prefixStart = ExpressionNode.UNICODE_PREFIX.charAt(0);
		int prefixLen = ExpressionNode.UNICODE_PREFIX.length();
		boolean prefixFound;
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			prefixFound = false;

			// first character of prefix found
			if (c == prefixStart) {
				prefixFound = true;
				// check prefix
				int j = i;
				for (int k = 0; k < prefixLen; k++, j++) {
					if (ExpressionNode.UNICODE_PREFIX.charAt(k) != str
							.charAt(j)) {
						prefixFound = false;
						break;
					}
				}

				if (prefixFound) {
					// try to get the unicode
					int code = 0;
					char digit;
					while (j < len && Character.isDigit(digit = str.charAt(j))) {
						code = 10 * code + (digit - 48);
						j++;
					}

					if (code > 0 && code < 65536) { // valid unicode
						sbInsertSpecial.append((char) code);
						i = j;
					} else { // invalid
						sbInsertSpecial.append(ExpressionNode.UNICODE_PREFIX);
						i += prefixLen;
					}
				} else {
					sbInsertSpecial.append(c);
				}
			} else {
				sbInsertSpecial.append(c);
			}
		}
		return sbInsertSpecial.toString();
	}

	
	
	
	/**
	 * Processes the CAS input string and returns an evaluation result.
	 * @boolean doEvaluate: whether inputExp should be evaluated (i.e. simplified).
	 * @return result as String in GeoGebra syntax
	 */
	public synchronized String processCASInput(String inputExp, boolean useGeoGebraVariables) throws Throwable {
		// PARSE input to check if it's a valid expression
		ValidExpression inVE = parseGeoGebraCASInput(inputExp);
					
		// EVALUATE input expression with MathPiper
		String mathPiperResult = null;
		Throwable throwable = null;
		try {
			// evaluate input in MathPiper and convert result back to GeoGebra expression
			mathPiperResult = processCASInputMathPiper(inVE, useGeoGebraVariables);
		} catch (Throwable th1) {
			throwable = th1;
			System.err.println("mathPiper evaluation failed: " + inputExp + "\n error: " + th1.toString());
		}
		
		// check some things
		boolean assignment = inVE.getLabel() != null;
		boolean delete = inputExp.startsWith("Delete");
		boolean mathPiperSuccessful = mathPiperResult != null;
		boolean mathPiperResultContainsCommands = mathPiperResult != null && mathPiperResult.indexOf('[') > -1;
		
		// EVALUATE input expression in GeoGebra if we have
		// - an assignments (e.g. a := 5, f(x) := x^2)
		// - or Delete, e.g. Delete[a]
		// - or MathPiper was not successful
		// - or MathPiper result contains commands
		boolean evalInGeoGebra = useGeoGebraVariables && 
			(assignment || delete || !mathPiperSuccessful || mathPiperResultContainsCommands); 
		String ggbResult = null;
		if (evalInGeoGebra) {
			// EVALUATE inputExp in GeoGebra
			try {
				// process inputExp in GeoGebra
				ggbResult = processCASInputGeoGebra(inputExp);
			} catch (Throwable th2) {
				if (throwable == null) throwable = th2;
				System.err.println("GeoGebra evaluation failed: " + inputExp + "\n error: " + th2.toString());
			}
			
			// inputExp failed with GeoGebra
			// try to evaluate result of MathPiper
			if (ggbResult == null && mathPiperSuccessful) {
				// EVALUATE result of MathPiper
				try {
					// process mathPiperResult in GeoGebra
					ggbResult = processCASInputGeoGebra(mathPiperResult);
				} catch (Throwable th2) {
					if (throwable == null) throwable = th2;
					System.err.println("GeoGebra evaluation failed: " + mathPiperResult + "\n error: " + th2.toString());
				}
			}
		}
		
		// return result string:
		// use MathPiper if that worked, otherwise GeoGebra
		if (mathPiperSuccessful) {
			if (assignment && "true".equals(mathPiperResult)) {
				// MathPiper returned true: use ggbResult if we have one, otherwise return ""
				if (ggbResult != null) {
					return ggbResult;
				} else {
					return mathPiperResult;
				}
			} 
			else {
				// MathPiper evaluation worked
				return mathPiperResult;
			}	
		} 
		
		else if (ggbResult != null) {
			// GeoGebra evaluation worked
			return ggbResult;
		}
		
		else {
			// nothing worked
			throw throwable;
		}
	}
	
	/**
	 * Evaluates expression with MathPiper and returns the resulting String in GeoGebra notation.
	 * @param inputExpression
	 * @param useGeoGebraVariables: 
	 * @return
	 * @throws Throwable
	 */
	private synchronized String processCASInputMathPiper(ValidExpression casInput, boolean useGeoGebraVariables) throws Throwable {
		// convert parsed input to MathPiper string
		String MathPiperString = toMathPiperString(casInput, useGeoGebraVariables);
			
		// EVALUATE input in MathPiper 
		String result = evaluateMathPiper(MathPiperString);

		// convert MathPiper result back into GeoGebra syntax
		String ggbString = toGeoGebraString(result);
		
		// TODO: remove
		System.out.println("eval with MathPiper: " + MathPiperString);
		System.out.println("   result: " + result);
		System.out.println("   ggbString: " + ggbString);
		
		return ggbString;
	}
	
	/**
	 * Evaluates expression with GeoGebra and returns the resulting string.
	 */
	private synchronized String processCASInputGeoGebra(String casInput) throws Throwable {
		GeoElement [] ggbEval = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(casInput, false);

		if (ggbEval.length == 1) {
			return ggbEval[0].toValueString();
		} else {
			StringBuilder sb = new StringBuilder('{');
			for (int i=0; i<ggbEval.length; i++) {
				sb.append(ggbEval[i].toValueString());
				if (i < ggbEval.length - 1)
					sb.append(", ");
			}
			sb.append('}');
			return sb.toString();
		}
	}
	
	
	/**
	 * Parses the given GeoGebra CAS input and returns an ValidExpression object.
	 */
	public synchronized ValidExpression parseGeoGebraCASInput(String exp) throws Throwable {
		// parse input
		ValidExpression ve = casParser.parseGeoGebraCASInput(exp);
		return ve;		
	}
	
	/**
	 * Tries to convert an expression in GeoGebra syntax into a LaTeX string.
	 * 
	 * @return null if something went wrong or the resulting String doesn't contain
	 * any LaTeX commands (i.e. no \).
	 */
	public synchronized String convertGeoGebraToLaTeXString(String ggbExp) {
		if (ggbExp == null)
			return null;
		
		try {
			// parse input
			ValidExpression ve = casParser.parseGeoGebraCASInput(ggbExp);
			String latex = ve.toLaTeXString(true);
						
			for (int i=0; i < latex.length(); i++) {
				char ch = latex.charAt(i);
				switch (ch) {
					case '\\':
					case '^':
						return latex;
				}
			}
		} catch (Throwable e) {
			//e.printStackTrace();			
		}		
		
		// no real latex string: return null
		return null;
	}
	

	
	/**
	 * Evaluates the given ExpressionValue and returns the result in MathPiper syntax.
	 * 
	 * @param resolveVariables: states whether variables from the GeoGebra kernel 
	 *    should be used. Note that this changes the given ExpressionValue. 
	 */
	public synchronized String toMathPiperString(ValidExpression ve, boolean resolveVariables) {
		
		// resolve global variables
		if (resolveVariables) {				
			casParser.resolveVariablesForCAS(ve);
		}	
		
		// convert to MathPiper String
		String MathPiperStr = casParser.toMathPiperString(ve, resolveVariables);

		// handle assignments
		String veLabel = ve.getLabel();
		if (veLabel != null) {
			StringBuilder sb = new StringBuilder();
			
			if (ve instanceof Function) {
				// function, e.g. f(x) := 2*x
				Function fun = (Function) ve;
				sb.append(veLabel);
				sb.append("(" );
				sb.append(fun.getFunctionVariable());
				sb.append(") := ");
				
				// evaluate right hand side:
				// import for e.g. g(x) := Eval(D(x) x^2)
				sb.append("Eval(");
				sb.append(MathPiperStr);
				sb.append(")");
				MathPiperStr = sb.toString();
			} else {	
				// assignment, e.g. a := 5
				MathPiperStr = veLabel + " := " + MathPiperStr;
			}
		}
		
		// TODO: remove
		System.out.println("GeoGebraCAS.toMathPiperString: " + MathPiperStr);
		return MathPiperStr;
	}	
	
	/**
	 * Returns a function from GeoGebra as a MathPiper string. For example f(x) = a x^2 is
	 * returned as "f(x) := a * x^2"
	 */
	public String toMathPiperString(GeoFunction geo) {
		if (!geo.isDefined()) return null;
		
		StringBuilder sb = new StringBuilder();
		Function fun = geo.getFunction();
		
		// function, e.g. f(x) := 2*x
		sb.append(geo.getLabel());
		sb.append("(" );
		sb.append(fun.getFunctionVariable());
		sb.append(") := ");
		sb.append(casParser.toMathPiperString(fun, false));

		return sb.toString();
	}
	
	
	/**
	 * Tries to parse a given MathPiper string and returns a String in GeoGebra syntax.
	 */
	public synchronized String toGeoGebraString(String MathPiperString) throws Throwable {
		ValidExpression ve = casParser.parseMathPiper(MathPiperString);
		return casParser.toGeoGebraString(ve);
	}
	
	/**
	 * Returns the MathPiper command for the given key (from ggb2MathPiper.properties)
	 * and the given command arguments. 
	 * For example, getMathPiperCommand("Expand.0", {"3*(a+b)"}) returns "Expand( 3*(a+b) )"
	 */
	final synchronized public String getMathPiperCommand(String name, ArrayList args, boolean symbolic) {
		StringBuilder sbMathPiperCommand = new StringBuilder(80);
				
		// build command key as name + "." + args.size()
		sbMathPiperCommand.setLength(0);
		sbMathPiperCommand.append(name);
		sbMathPiperCommand.append('.');
		sbMathPiperCommand.append(args.size());
		
		// get translation ggb -> MathPiper
		String translation = getMathPiperCommand(sbMathPiperCommand.toString());
		sbMathPiperCommand.setLength(0);		
		
		// no translation found: 
		// use key as command name
		if (translation == null) {			
			sbMathPiperCommand.append(name);
			sbMathPiperCommand.append('(');
			for (int i=0; i < args.size(); i++) {
				ExpressionValue ev = (ExpressionValue) args.get(i);				
				if (symbolic)
					sbMathPiperCommand.append(ev.toString());
				else
					sbMathPiperCommand.append(ev.toValueString());
				sbMathPiperCommand.append(',');
			}
			sbMathPiperCommand.setCharAt(sbMathPiperCommand.length()-1, ')');
		}
		
		// translation found: 
		// replace %0, %1, etc. in translation by command arguments
		else {
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ExpressionValue ev = (ExpressionValue) args.get(pos);				
						if (symbolic)
							sbMathPiperCommand.append(ev.toString());
						else
							sbMathPiperCommand.append(ev.toValueString());
					} else {
						// failed
						sbMathPiperCommand.append(ch);
					}
				} else {
					sbMathPiperCommand.append(ch);
				}
			}
		}

		return sbMathPiperCommand.toString();
	}
	
	
	/**
	 * Returns the MathPiper command for the given key (from ggb2MathPiper.properties)
	 */ 
	private synchronized String getMathPiperCommand(String key) {
		if (ggb2MathPiper == null) {
			ggb2MathPiper = MyResourceBundle.loadSingleBundleFile(RB_GGB_TO_MathPiper);
		}
		
		String ret;
		try {
			ret =  ggb2MathPiper.getString(key);
		} catch (MissingResourceException e) {
			ret = null;
		}

		return ret;
	}
	
}