package geogebra.kernel;

import geogebra.kernel.arithmetic.MyStringBuffer;
import geogebra.util.Util;

import java.awt.Font;

public class GeoText extends GeoElement
implements Locateable, AbsoluteScreenLocateable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String str; 	
	private GeoPoint startPoint; // location of Text on screen
	private boolean isLaTeX; // text is a LaTeX formula
	
	// font options
	private boolean serifFont = false;
	private int fontStyle = Font.PLAIN;
	private int fontSize = 0; // size relative to default font size
	private int printDecimals = -1;
	
	// for absolute screen location
	boolean hasAbsoluteScreenLocation = false;
	
	public GeoText(Construction c) {
		super(c);
		// don't show in algebra view	
		setAlgebraVisible(false); 	
	}  
	
	public GeoText(Construction c, String label, String value) {
		this(c);
		setTextString(value);
		setLabel(label);
	}  
	
	/**
	 * Copy constructor
	 */
	public GeoText(GeoText text) {
		this(text.cons);
		set(text);
	}

	public GeoElement copy() {
		return new GeoText(this);
	}

	public void set(GeoElement geo) {
		GeoText gt = (GeoText) geo;
		str = gt.str;
		
		try {
			if (gt.startPoint != null) {
				if (gt.hasAbsoluteLocation()) {
					//	create new location point	
					setStartPoint(new GeoPoint(gt.startPoint));
				} else {
					//	take existing location point	
					setStartPoint(gt.startPoint);
				}
			}
		}
		catch (CircularDefinitionException e) {
			System.err.println("set GeoText: CircularDefinitionException");
		}
	}
	
	final public void setTextString(String text) {
		if (isLaTeX) {
			//TODO: check greek letters of latex string
			str = Util.toLaTeXString(text, false);
		} else {
			str = text;
		}		
	}
	
	final public String getTextString() {
		return str;
	}
	
	public void setStartPoint(GeoPoint p, int number)  throws CircularDefinitionException {
		setStartPoint(p);
	}
	
	public void removeStartPoint(GeoPoint p) {    
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch(Exception e) {}
		}
	}
			
	public void setStartPoint(GeoPoint p)  throws CircularDefinitionException {    
		// check for circular definition
		if (isParentOf(p))
			throw new CircularDefinitionException();		
		
		// remove old dependencies
		if (startPoint != null) startPoint.unregisterLocateable(this);	
		
		// set new location	
		if (p == null) {
			if (startPoint != null) // copy old startPoint			
				startPoint = new GeoPoint(startPoint);
			else 
				startPoint = null; 
			labelOffsetX = 0;
			labelOffsetY = 0;					
		} else {
			startPoint = p;
			//	add new dependencies
			startPoint.registerLocateable(this);
			
			// absolute screen position should be deactivated
			setAbsoluteScreenLocActive(false);
		}											
	}
	
	void doRemove() {
		super.doRemove();
		// tell startPoint	
		if (startPoint != null) startPoint.unregisterLocateable(this);
	}
	
	public GeoPoint getStartPoint() {
		return startPoint;
	}
	
	public boolean hasAbsoluteLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}
	
	public void setWaitForStartPoint() {
		// this can be ignored for a text 
		// as the position of its startpoint
		// is irrelevant for the rest of the construction
	}
	
	public void setMode(int mode) {
	}

	public int getMode() {
		return 0;
	}

	/**
	 * allways returns true
	*/
	public boolean isDefined() {
		return str != null && (startPoint == null || startPoint.isDefined());
	}

	/**
	 * doesn't do anything
 	*/
	public void setUndefined() {
		str = null;
	}

	public String toValueString() {		
		return str;		
	}
	
	public String toString() {		
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append('\"');
		if (str != null)
			sbToString.append(str);
		sbToString.append('\"');	
		return sbToString.toString();
	}
	private StringBuffer sbToString = new StringBuffer(80);

	boolean showInAlgebraView() {
		return false;
	}

	boolean showInEuclidianView() {		
		return isDefined();
	}

	String getClassName() {
		return "GeoText";
	}
	
    String getTypeString() {
		return "Text";
	}
	
	public boolean isMoveable() {
		return !isFixed();
	}
	
	public boolean isFixable() {
		return true;
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
		return true;
	}
	
	public boolean isGeoText() {
		return true;
	}
	
	public MyStringBuffer getText() {
		return new MyStringBuffer(str);
	}	
		
	/**
	   * save object in xml format
	   */ 
	  public final String getXML() {
		 StringBuffer sb = new StringBuffer();
	 
		 // an independent text needs to add
		 // its expression itself
		 // e.g. text0 = "Circle"
		 if (isIndependent()) {
			sb.append("<expression");
				sb.append(" label=\"");
				sb.append(Util.encodeXML(label));
				sb.append("\" exp=\"");
				sb.append("&quot;");
				if (str != null)
					sb.append(Util.encodeXML(str));
				sb.append("&quot;");
				// expression   
			sb.append("\"/>\n");
		 }
  		  
		  sb.append("<element"); 
			  sb.append(" type=\"text\"");
			  sb.append(" label=\"");
			  sb.append(Util.encodeXML(label));
		  sb.append("\">\n");
		  sb.append(getXMLtags());
		  sb.append("</element>\n");
	  	  
		  return sb.toString();
	  }

	/**
	* returns all class-specific xml tags for getXML
	*/
   	String getXMLtags() {   	
	   	StringBuffer sb = new StringBuffer();
	   	sb.append(getXMLvisualTags(false));			
		
		if (isFixed()) {
			sb.append("\t<fixed val=\"true\"/>\n");	
		}
		
		if (isLaTeX) {
			sb.append("\t<isLaTeX val=\"true\"/>\n");	
		}
		
		// font settings
		if (serifFont || fontSize != 0 || fontStyle != 0) {
			sb.append("\t<font serif=\"");
			sb.append(serifFont);
			sb.append("\" size=\"");
			sb.append(fontSize);
			sb.append("\" style=\"");
			sb.append(fontStyle);
			sb.append("\"/>\n");
		}
		
		// print decimals
		if (printDecimals >= 0) {
			sb.append("\t<decimals val=\"");
			sb.append(printDecimals);
			sb.append("\"/>\n");
		}
						
		sb.append(getBreakpointXML());

		// store location of text (and possible labelOffset)
		sb.append(getXMLlocation());			
			
	   return sb.toString();   
   	}
   	
   	/**
   	 * Returns startPoint of this text in XML notation.  	 
   	 */
   	private String getXMLlocation() {   		
   		StringBuffer sb = new StringBuffer();   				
   		
   		if (hasAbsoluteScreenLocation) {
   			sb.append("\t<absoluteScreenLocation ");			
   				sb.append(" x=\""); 	sb.append( labelOffsetX ); 	sb.append("\"");
   				sb.append(" y=\""); 	sb.append( labelOffsetY ); 	sb.append("\"");
			sb.append("/>\n");	
   		} 
   		else {   			
			// location of text
			if (startPoint != null) {
				sb.append(startPoint.getStartPointXML());
	
				if (labelOffsetX != 0 || labelOffsetY != 0) {
					sb.append("\t<labelOffset");			
						sb.append(" x=\""); 	sb.append( labelOffsetX ); 	sb.append("\"");
						sb.append(" y=\""); 	sb.append( labelOffsetY ); 	sb.append("\"");
					sb.append("/>\n");	  	
				}	
			}
   		}
   		return sb.toString();
   	}

	public void setAllVisualProperties(GeoElement geo) {
		super.setAllVisualProperties(geo);
		
		// start point of text
		if (geo instanceof GeoText) {
			GeoText text = (GeoText) geo;			
			isLaTeX = text.isLaTeX;				
			setSameLocation(text);
		}		
	}	
	
	private void setSameLocation(GeoText text) {
		if (text.hasAbsoluteScreenLocation) {
			setAbsoluteScreenLocActive(true);
			setAbsoluteScreenLoc(text.getAbsoluteScreenLocX(), 
								 text.getAbsoluteScreenLocY());
		} 
		else {
			try {
				setStartPoint(text.getStartPoint());
			} catch (Exception e) {				
			}
		}						
	}


	public boolean isLaTeX() {
		return isLaTeX;
	}

	public void setLaTeX(boolean b, boolean updateParentAlgo) {
		isLaTeX = b;
		
		if (updateParentAlgo) {
			updateCascadeParentAlgo();
		}
	}		

	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;		
	}

	public int getAbsoluteScreenLocX() {	
		return labelOffsetX;
	}

	public int getAbsoluteScreenLocY() {		
		return labelOffsetY;
	}
	
	public double getRealWorldLocX() {
		if (startPoint == null)
			return 0;
		else
			return startPoint.inhomX;
	}
	
	public double getRealWorldLocY() {
		if (startPoint == null)
			return 0;
		else
			return startPoint.inhomY;
	}
	
	public void setRealWorldLoc(double x, double y) {
		GeoPoint loc = getStartPoint();
		if (loc == null) {
			loc = new GeoPoint(cons);	
			try {setStartPoint(loc); }
			catch(Exception e){}
		}
		loc.setCoords(x, y, 1.0);
		labelOffsetX = 0;
		labelOffsetY = 0;	
	}

	public void setAbsoluteScreenLocActive(boolean flag) {
		if (flag == hasAbsoluteScreenLocation) return;
		
		hasAbsoluteScreenLocation = flag;			
		if (flag) {
			// remove startpoint
			if (startPoint != null) {
				startPoint.unregisterLocateable(this);	
				startPoint = null;
			}			
		} else {
			labelOffsetX = 0;
			labelOffsetY = 0;
		}
	}

	public boolean isAbsoluteScreenLocActive() {
		return hasAbsoluteScreenLocation;
	}
	
	public boolean isAbsoluteScreenLocSetable() {
		return true;
	}

	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int size) {
		fontSize = size;
	}
	public int getFontStyle() {
		return fontStyle;
	}
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
	final public int getPrintDecimals() {
		return printDecimals;
	}
	public void setPrintDecimals(int printDecimals) {		
		AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			this.printDecimals = printDecimals;
			algo.update();
		}			
	}
	public boolean isSerifFont() {
		return serifFont;
	}
	public void setSerifFont(boolean serifFont) {
		this.serifFont = serifFont;
	}
}
