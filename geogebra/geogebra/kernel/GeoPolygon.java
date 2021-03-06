/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;
import geogebra.util.MyMath;

import java.awt.Color;
import java.util.HashSet;

/**
 * Polygon through given points
 * 
 * @author Markus Hohenwarter
 */
public class GeoPolygon extends GeoElement implements NumberValue, Path, Region, Traceable {
	
	private static final long serialVersionUID = 1L;

	public static final int POLYGON_MAX_POINTS = 100;
	
	protected GeoPointND [] points;
	protected GeoSegmentND [] segments;
	
	/** view direction of the polygon (TODO generalize) */
	private GgbVector viewDirection = new GgbVector(0, 0, 1, 0);
	
	/** first point for region coord sys */
	protected GeoPoint p0;
	/** second point for region coord sys */
	protected GeoPoint p1;
	/** third point for region coord sys */
	protected GeoPoint p2;
	/** number of points in coord sys */
	protected int numCS = 0;
	

	
	protected double area;
	private boolean defined = false;		
	private boolean initLabelsCalled = false;
	
	/** says if the polygon had created its segments itself (used for 3D) */
	private boolean createSegments = true;
	
	/** common constructor for 2D.
	 * @param c the construction
	 * @param points vertices 
	 */
	public GeoPolygon(Construction c, GeoPointND[] points) {
		this(c,points,null,true);
	}
	
	/** common constructor for 3D.
	 * @param c the construction
	 * @param points vertices 
	 * @param cs for 3D stuff : 2D coord sys
	 * @param createSegments says if the polygon has to creates its edges
	 */	
	public GeoPolygon(Construction c, GeoPointND[] points, GgbCoordSys cs, boolean createSegments) {
		this(c);
		//Application.printStacktrace("poly");
		this.createSegments=createSegments;
		setPoints(points, cs, createSegments);
		setLabelVisible(false);
		setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
	}
	
	public GeoPolygon(Construction cons) {
		super(cons);
	}

	/** for 3D stuff (unused here)
	 * @param cs GeoCoordSys2D
	 */
	public void setCoordSys(GgbCoordSys cs) {
		
	}
	
	
	

	public String getClassName() {
		return "GeoPolygon";
	}
	
    protected String getTypeString() {
    	if (points == null) 
    		return "Polygon";
    	
    	switch (points.length) {
    		case 3:
    			return "Triangle";
    			
    		case 4:
    			return "Quadrilateral";
    			
    		case 5:
    			return "Pentagon";
    		
    		case 6:    			
    			return "Hexagon";
    		
    		default:
    			return "Polygon";    	
    	}    	    			
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POLYGON;
    }
	
    
    
    
    /**
     * set the vertices to points
     * @param points the vertices
     */
    public void setPoints(GeoPointND [] points) {
    	setPoints(points,null,true);
    }

    	
    /**
     * set the vertices to points (cs is only used for 3D stuff)
     * @param points the vertices
     * @param cs used for 3D stuff
     * @param createSegments says if the polygon has to creates its edges
     */
    public void setPoints(GeoPointND [] points, GgbCoordSys cs, boolean createSegments) {
		this.points = points;
		setCoordSys(cs);
		
		if (createSegments)
			updateSegments();
		
//		if (points != null) {
//		Application.debug("*** " + this + " *****************");
//        Application.debug("POINTS: " + points.length);
//        for (int i=0; i < points.length; i++) {
//			Application.debug(" " + i + ": " + points[i]);		     	        	     	
//		}   
//        Application.debug("SEGMENTS: " + segments.length);
//        for (int i=0; i < segments.length; i++) {
//			Application.debug(" " + i + ": " + segments[i]);		     	        	     	
//		}  
//        Application.debug("********************");
//		}
	}    
    
    
    
    ///////////////////////////////
    // ggb3D 2009-03-08 - start
    
	/** return number for points
	 * @return number for points
	 */
	public int getPointsLength(){
		return points.length;
	}
	
	
	/**
	 * return the x-coordinate of the i-th vertex
	 * @param i number of vertex
	 * @return the x-coordinate 
	 */
	public double getPointX(int i){
		return getPoint(i).inhomX;
	}
	
	/**
	 * return the y-coordinate of the i-th vertex
	 * @param i number of vertex
	 * @return the y-coordinate 
	 */
	public double getPointY(int i){
		return getPoint(i).inhomY;
	}

	
    // ggb3D 2009-03-08 - end
    ///////////////////////////////
	


    /**
     * Inits the labels of this polygon, its segments and its points.
     * labels[0] for polygon itself, labels[1..n] for segments,
     * labels[n+1..2n-2] for points (only used for regular polygon)
     * @param labels
     */
    void initLabels(String [] labels) {       	 
    	initLabelsCalled = true;
    	// Application.debug("INIT LABELS");
    	
    	// label polygon
    	if (labels == null || labels.length == 0) {    		
        	// Application.debug("no labels given");
        	
             setLabel(null);
             if (segments != null) {
            	 defaultSegmentLabels();
             }
             return;
    	}    	    	
    	
    	// label polygon              
        // first label for polygon itself
        setLabel(labels[0]);    

    	// label segments and points
    	if (points != null && segments != null) {
    		
    		// additional labels for the polygon's segments
    		// poly + segments + points - 2 for AlgoPolygonRegular
    		if (labels.length == 1 + segments.length + points.length - 2) {
    			//Application.debug("labels for segments and points");
    			
	            int i=1;
    			for (int k=0; k < segments.length; k++, i++) {
	               segments[k].setLabel(labels[i]);    				
	             }		            
    			for (int k=2; k < points.length; k++, i++) {    				
	                points[k].setLabel(labels[i]);
	            }
	        } 
    		    		
    		// additional labels for the polygon's segments
    		// poly + segments for AlgoPolygon
    		else if (labels.length == 1 + segments.length) {
    			//Application.debug("labels for segments");
    			
            	int i=1;
    			for (int k=0; k < segments.length; k++, i++) {
	                segments[k].setLabel(labels[i]);
	            }		            	           	            
	        } 
    		
	        else { 
	        	//Application.debug("label for polygon (autoset segment labels)");     	
	        	defaultSegmentLabels();
	        }
    	}    	
    	

    }
    
    /**
     * Returns whether the method initLabels() was called for this polygon.
     * This is important to know whether the segments have gotten labels.
     */
    final public boolean wasInitLabelsCalled() {
    	return initLabelsCalled;
    }
    
    private void defaultSegmentLabels() {
    	//  no labels for segments specified
        //  set labels of segments according to point names
        if (points.length == 3) {    
        	
        	// make sure segment opposite C is called c not a_1
        	if (getParentAlgorithm() instanceof AlgoPolygonRegular)
        		points[2].setLabel(null);
        	
           setLabel(segments[0], points[2]);
           setLabel(segments[1], points[0]);
           setLabel(segments[2], points[1]); 
        } else {
           for (int i=0; i < points.length; i++) {
               setLabel(segments[i], points[i]);
           }
        }       
    }
    
    /**
     * Sets label of segment to lower case label of point.
     * If the point has no label, a default label is used for the segment.
     * If the lower case label of the point is already used, an indexed label
     * is created.
     */
    private void setLabel(GeoSegmentND s, GeoPointND p) {
        if (!p.isLabelSet() || p.getLabel() == null) {
        	s.setLabel(null);
        } else {
        	// use lower case of point label as segment label
        	String lowerCaseLabel = ((GeoElement)p).getFreeLabel(p.getLabel().toLowerCase());
        	s.setLabel(lowerCaseLabel);
        }
    }
	
    /**
     * Updates all segments of this polygon for its point array.
     * Note that the point array may be changed: this method makes
     * sure that segments are reused if possible.
     */
	 private void updateSegments() {  	
		 if (points == null) return;
		 
		GeoSegmentND [] oldSegments = segments;				
		segments = new GeoSegmentND[points.length]; // new segments
				
		if (oldSegments != null) {
			// reuse or remove old segments
			for (int i=0; i < oldSegments.length; i++) {        	
	        	if (i < segments.length &&
	        		oldSegments[i].getStartPointAsGeoElement() == points[i] && 
	        		oldSegments[i].getEndPointAsGeoElement() == points[(i+1) % points.length]) 
	        	{		
        			// reuse old segment
        			segments[i] = oldSegments[i];        		
         		} 
	        	else {
        			// remove old segment
        			//((AlgoJoinPointsSegment) oldSegments[i].getParentAlgorithm()).removeSegmentOnly();
        			removeSegment(oldSegments[i]);
	        	}	        	
			}
		}			
		
		// make sure segments created visible if appropriate
		setDefined();
		
		// create missing segments
        for (int i=0; i < segments.length; i++) {
        	GeoPointND startPoint = points[i];
        	GeoPointND endPoint = points[(i+1) % points.length];
        	
        	if (segments[i] == null) {
         		segments[i] = createSegment(startPoint, endPoint, i == 0 ? isEuclidianVisible() : segments[0].isEuclidianVisible());
        	}     
        }         
        
    }
	 
	 

	 /**
	  * remove an old segment
	  * @param oldSegment the old segment 
	  */
	 public void removeSegment(GeoSegmentND oldSegment){
		 ((AlgoJoinPointsSegment) ((GeoSegment) oldSegment).getParentAlgorithm()).removeSegmentOnly();
	 }
	 
	 
	 
	 /**
	  * return a segment joining startPoint and endPoint
	  * @param startPoint the start point
	  * @param endPoint the end point
	  * @return the segment
	  */
	 public GeoSegmentND createSegment(GeoPointND startPoint, GeoPointND endPoint, boolean euclidianVisible){
		 GeoSegmentND segment;

		 AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons, (GeoPoint) startPoint, (GeoPoint) endPoint, this);            
		 cons.removeFromConstructionList(algoSegment);               

		 segment = algoSegment.getSegment(); 
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 
         segment.setLineThickness(getLineThickness()); 
         segment.setEuclidianVisible(euclidianVisible);
         //segment.setAuxiliaryObject(true);

		 return segment;
	 }

	 

	/**
	 * The copy of a polygon is a number (!) with
	 * its value set to the polygons current area
	 */      
	public GeoElement copy() {
		return new GeoNumeric(cons, getArea());        
	}    
	
	public GeoElement copyInternal(Construction cons) {						
		GeoPolygon ret = new GeoPolygon(cons, null); 
		ret.points = GeoElement.copyPoints(cons, (GeoPoint[]) points);		
		ret.set(this);
				
		return ret;		
	} 		
	
	protected GeoPointND newGeoPoint(){
		return new GeoPoint(cons);
	}
	
	public void set(GeoElement geo) {
		GeoPolygon poly = (GeoPolygon) geo;		
		area = poly.area;
		
		
		// make sure both arrays have same size
		if (points.length != poly.points.length) {
			GeoPointND [] tempPoints = new GeoPointND[poly.points.length];
			for (int i=0; i < tempPoints.length; i++) {
				tempPoints[i] = i < points.length ? points[i] : newGeoPoint();
			}
			points = tempPoints;
		}
		
		for (int i=0; i < points.length; i++) {				
			((GeoElement) points[i]).set((GeoElement) poly.points[i]);
		}	
		
		setCoordSys(null);
		updateSegments();
		defined = poly.defined;	
	}
	
	
	/**
	 * Returns the i-th point of this polygon.
	 * Note that this array may change dynamically.
	 * @param i number of point
	 * @return the i-th point
	 */
	public GeoPoint getPoint(int i) {
		return (GeoPoint) points[i];
	}

	/**
	 * Returns the points of this polygon.
	 * Note that this array may change dynamically.
	 */
	public GeoPoint [] getPoints() {
		return (GeoPoint []) points;
	}
	
	public GeoPointND [] getPointsND() {
		return points;
	}
	
	public GeoPointND getPointND(int i) {
		return points[i];
	}	
	
	/**
	 * Returns the segments of this polygon.
	 * Note that this array may change dynamically.
	 */
	public GeoSegmentND [] getSegments() {
		return segments;
	}
	
	/** sets the segments (used by GeoPolyhedron)
	 * @param segments the segments
	 */
	public void setSegments(GeoSegmentND [] segments){
		this.segments = segments;
	}

	public boolean isFillable() {
		return true;
	}
	
//	Michael Borcherds 2008-01-26 BEGIN
	/** 
	 * Calculates this polygon's area . This method should only be called by
	 * its parent algorithm of type AlgoPolygon
	 */
	public void calcArea() {
		area = calcAreaWithSign(getPoints());	
		defined = !(Double.isNaN(area) || Double.isInfinite(area));
	}
	
	public double getArea() {
		if (defined)
			return Math.abs(area);				        
		else 
			return Double.NaN;			        	
	}
	
	public double getDirection() { // clockwise=-1/anticlockwise=+1/no area=0
		if (defined)
			return MyMath.sgn(kernel, area);				        
		else 
			return Double.NaN;			        	
	}	

	/**
	 * Returns the area of a polygon given by points P
	 */	
	final static public double calcArea(GeoPoint [] P) {
	    return Math.abs(calcAreaWithSign(P));
	}
	/**
	 * Returns the area of a polygon given by points P, negative if clockwise
	 * changed name from calcArea as we need the sign when calculating the centroid Michael Borcherds 2008-01-26
	 * TODO Does not work if polygon is self-entrant
	 */	
	final static public double calcAreaWithSign(GeoPoint[] points2) {
		if (points2 == null || points2.length < 2)
			return Double.NaN;
		
	   int i = 0;   
	   for (; i < points2.length; i++) {
		   if (points2[i].isInfinite())
			return Double.NaN;
	   }
    
	   // area = 1/2 | det(P[i], P[i+1]) |
	   int last = points2.length - 1;
	   double sum = 0;                     
	   for (i=0; i < last; i++) {
			sum += GeoPoint.det(points2[i], points2[i+1]);
	   }
	   sum += GeoPoint.det(points2[last], points2[0]);
	   return sum / 2.0;  // positive (anticlockwise points) or negative (clockwise)
   }   
	
	/**
	 * Calculates the centroid of this polygon and writes
	 * the result to the given point.
	 * algorithm at http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * TODO Does not work if polygon is self-entrant
	 */
	public void calcCentroid(GeoPoint centroid) {
		if (!defined) {
			centroid.setUndefined();
			return;
		}
	
		double xsum = 0;
		double ysum = 0;
		double factor=0;
		for (int i=0; i < points.length; i++) {
			factor=pointsClosedX(i)*pointsClosedY(i+1)-pointsClosedX(i+1)*pointsClosedY(i);
			xsum+=(pointsClosedX(i)+pointsClosedX(i+1)) * factor;
			ysum+=(pointsClosedY(i)+pointsClosedY(i+1)) * factor;
		}
		centroid.setCoords(xsum, ysum, 6.0*getAreaWithSign()); // getArea includes the +/- to compensate for clockwise/anticlockwise
	}
	
	private double pointsClosedX(int i)
	{
		// pretend array has last element==first element
		if (i==points.length) //return points[0].inhomX; else return points[i].inhomX;
			return getPointX(0); else return getPointX(i);
	}
	 	
	private double pointsClosedY(int i)
	{
		// pretend array has last element==first element
		if (i==points.length) //return points[0].inhomY; else return points[i].inhomY;
			return getPointY(0); else return getPointY(i);
	}
	 	
	public double getAreaWithSign() {
		if (defined)
			return area;				        
		else 
			return Double.NaN;			        	
	}	
//	Michael Borcherds 2008-01-26 END

	/*
	 * overwrite methods
	 */
	public boolean isDefined() {
		return defined;
   }	
   
   public void setDefined() {
   		defined = true;
   }
   
   public void setUndefined() {
		   defined = false;
	}
        
   public final boolean showInAlgebraView() {	   
	   //return defined;
	   return true;
   }
   
   
   /** 
	* Yields true if the area of this polygon is equal to the
	* area of polygon p.
	*/
	// Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type
		if (geo.isGeoPolygon()) return kernel.isEqual(getArea(), ((GeoPolygon)geo).getArea());
		else return false;
	}

   public void setEuclidianVisible(boolean visible) {

	   setEuclidianVisible(visible,true);
	
   }  
   
   public void setEuclidianVisible(boolean visible, boolean updateSegments) {
		super.setEuclidianVisible(visible);
		if (updateSegments && segments != null) {
			for (int i=0; i < segments.length; i++) {
				segments[i].setEuclidianVisible(visible);			
				segments[i].update();
			}
		}		
  }  


   public void setObjColor(Color color) {
   		super.setObjColor(color);
   		if (segments != null && createSegments) {
   			for (int i=0; i < segments.length; i++) {
   				segments[i].setObjColor(color);
   				segments[i].update();
   			}
   		}	
   }
   
   public void setLineType(int type) {
	   setLineType(type,true);
   }
 
   
   /**
    * set the line type (and eventually the segments)
    * @param type
    * @param updateSegments
    */
   public void setLineType(int type, boolean updateSegments) {
	   super.setLineType(type);
	   if (updateSegments)
		   if (segments != null) {
			   for (int i=0; i < segments.length; i++) {
				   segments[i].setLineType(type);	
				   segments[i].update();
			   }
		   }	
   }
   
   public void setLineTypeHidden(int type) {
	   setLineTypeHidden(type, true);
   }

   /** set the hidden line type (and eventually the segments)
    * @param type
    * @param updateSegments
    */
   public void setLineTypeHidden(int type, boolean updateSegments) {
	   super.setLineTypeHidden(type);
		if (updateSegments)
			if (segments != null) {
				for (int i=0; i < segments.length; i++) {
					((GeoElement) segments[i]).setLineTypeHidden(type);	
					segments[i].update();
				}
			}	
  }
   
   public void setLineThickness(int th) {
	   setLineThickness(th,true);
   }

   /** set the line thickness (and eventually the segments)
    * @param th
    * @param updateSegments
    */
   public void setLineThickness(int th, boolean updateSegments) {
	   super.setLineThickness(th);

		if (updateSegments)
			if (segments != null) {
				for (int i=0; i < segments.length; i++) {
					segments[i].setLineThickness(th);
					segments[i].update();
				}
			}	
   }
	
   final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format( getArea() ));
	    return sbToString.toString();
   }      
   private StringBuilder sbToString = new StringBuilder(50);

   final public String toValueString() {
	   return kernel.format(getArea());
   }

	
	 /**
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getArea() );
    }     
    
    final public double getDouble() {
        return getArea();
    }   
        
    final public boolean isConstant() {
        return false;
    }
    
    final public boolean isLeaf() {
        return true;
    }
    
    final public HashSet getVariables() {
        HashSet varset = new HashSet();        
        varset.add(this);        
        return varset;          
    }                   
    
    final public ExpressionValue evaluate() { return this; }	

	public void setMode(int mode) {
		// dummy		
	}

	public int getMode() {
		// dummy
		return 0;
	}

	protected boolean showInEuclidianView() {
		return defined;
	}    
	
	public boolean isNumberValue() {
		return true;
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
	
	public boolean isGeoPolygon() {
		return true;
	}
	
	/*
	 * Path interface implementation
	 */
	
	public boolean isPath() {
		return true;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public double getMaxParameter() {
		return segments.length;
	}

	public double getMinParameter() {		
		return 0;
	}

	public boolean isClosedPath() {
		return true;
	}

	public boolean isOnPath(GeoPointND PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		// check if P is on one of the segments
		for (int i=0; i < segments.length; i++) {
			if (segments[i].isOnPath(P, eps))
				return true;
		}				
		return false;
	}

	public void pathChanged(GeoPointND PI) {		
		
		GeoPoint P = (GeoPoint) PI;
		
		// parameter is between 0 and segment.length,
		// i.e. floor(parameter) gives the segment index
		
		PathParameter pp = P.getPathParameter();
		pp.t = pp.t % segments.length;
		if (pp.t < 0) 
			pp.t += segments.length;
		int index = (int) Math.floor(pp.t) ;		
		GeoSegmentND seg = segments[index];
		double segParameter = pp.t - index;
		
		// calc point for given parameter
		/*
		P.x = seg.startPoint.inhomX + segParameter * seg.y;
		P.y = seg.startPoint.inhomY - segParameter * seg.x;
		*/
		P.x = seg.getPointX(segParameter);
		P.y = seg.getPointY(segParameter);
		P.z = 1.0;	
	}

	public void pointChanged(GeoPointND PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		double qx = P.x/P.z;
		double qy = P.y/P.z;
		double minDist = Double.POSITIVE_INFINITY;
		double resx=0, resy=0, resz=0, param=0;
		
		// find closest point on each segment
		PathParameter pp = P.getPathParameter();
		for (int i=0; i < segments.length; i++) {
			P.x = qx;
			P.y = qy;
			P.z = 1;
			segments[i].pointChanged(P);
			
			double x = P.x/P.z - qx; 
			double y = P.y/P.z - qy;
			double dist = x*x + y*y;			
			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				resx = P.x;
				resy = P.y;
				resz = P.z;
				param = i + pp.t;
			}
		}				
			
		P.x = resx;
		P.y = resy;
		P.z = resz;
		pp.t = param;	
	}	
	
	
	
	
	
	/*
	 * Region interface implementation
	 */
	
	public boolean isRegion() {
		return true;
	}
	
	protected boolean isInRegion(GeoPointND PI, boolean update){
		return isInRegion(PI);
	}
	
	public boolean isInRegion(GeoPointND PI){
		
		GeoPoint P = (GeoPoint) PI;
		double x0 = P.x/P.z;
		double y0 = P.y/P.z;
				
		return isInRegion(x0,y0);

	}
	
	/** says if the point (x0,y0) is in the region
	 * @param x0 x-coord of the point
	 * @param y0 y-coord of the point
	 * @return true if the point (x0,y0) is in the region
	 */
	public boolean isInRegion(double x0, double y0){
		
		double x1,y1,x2,y2;
		int numPoints = points.length;
		x1=getPointX(numPoints-1)-x0;
		y1=getPointY(numPoints-1)-y0;
		
		boolean ret=false;
		for (int i=0;i<numPoints;i++){
			x2=getPointX(i)-x0;
			y2=getPointY(i)-y0;
			ret = ret ^ intersectOx(x1, y1, x2, y2);
			x1=x2;
			y1=y2;
		}
			
		return ret;
	}
	
	public void regionChanged(GeoPointND P){
		

		
		//GeoPoint P = (GeoPoint) PI;
		RegionParameters rp = P.getRegionParameters();
		
		if (rp.isOnPath())
			pathChanged(P);
		else{
			//pointChangedForRegion(P);
			double xu = p1.inhomX - p0.inhomX;
			double yu = p1.inhomY - p0.inhomY;				
			double xv = p2.inhomX - p0.inhomX;
			double yv = p2.inhomY - p0.inhomY;
			
			//Application.debug("xu="+xu+", rp.getT1()="+ rp.getT1());
			setRegionChanged(P,
					p0.inhomX + rp.getT1()*xu + rp.getT2()*xv,
					p0.inhomY + rp.getT1()*yu + rp.getT2()*yv);
			
			if (!isInRegion(P,false)){
				pointChanged(P);
				rp.setIsOnPath(true);
			}	
			
		}
	}
	
	
	/** set region coords (x,y) to point PI
	 * @param PI point
	 * @param x x-coord
	 * @param y y-coord
	 */
	public void setRegionChanged(GeoPointND PI, double x, double y){
		GeoPoint P = (GeoPoint) PI;
		P.x = x;
		P.y = y;
		P.z = 1;
				
	}
	
	
	public void pointChangedForRegion(GeoPointND P){
		
		P.updateCoords2D();
		
		RegionParameters rp = P.getRegionParameters();
		
		//Application.debug("isInRegion : "+isInRegion(P));
		
		if (!isInRegion(P)){
			pointChanged(P);
			rp.setIsOnPath(true);
		}else{
			if(numCS!=3){ //if the coord sys is not defined by 3 independent points, then the point lies on the path
				pointChanged(P);
				rp.setIsOnPath(true);
			}else{
				rp.setIsOnPath(false);
				double xu = p1.inhomX - p0.inhomX;
				double yu = p1.inhomY - p0.inhomY;				
				double xv = p2.inhomX - p0.inhomX;
				double yv = p2.inhomY - p0.inhomY;				
				double x = P.getX2D() - p0.inhomX;
				double y = P.getY2D() - p0.inhomY;
				rp.setT1((xv*y-x*yv)/(xv*yu-xu*yv));
				rp.setT2((x*yu-xu*y)/(xv*yu-xu*yv));
				
				P.updateCoordsFrom2D(false);
			}
		}
	}
	

	

	
	
	/**
	 * update the coord sys used for region parameters
	 */
	public void updateRegionCS() {
		// TODO add condition to calculate it		
		if(p2==null || GeoPoint.collinear(p0, p1, p2)){
			p0 = getPoint(0);	
			numCS = 1;
			//Application.debug(" p0 = "+p0.inhomX+","+p0.inhomY);

			int secondPoint = -1;
			boolean secondPointFound = false;
			for (secondPoint=1; secondPoint<getPoints().length && !secondPointFound; secondPoint++){
				p1=getPoint(secondPoint);
				//Application.debug(" p1 ("+secondPoint+") = "+p1.inhomX+","+p1.inhomY);
				if (!Kernel.isEqual(p0.inhomX, p1.inhomX, Kernel.STANDARD_PRECISION))
					secondPointFound = true;
				else if (!Kernel.isEqual(p0.inhomY, p1.inhomY, Kernel.STANDARD_PRECISION))
					secondPointFound = true;
				//Application.debug(" secondPointFound = "+secondPointFound);
			}

			int thirdPoint = -1;
			if (secondPointFound){
				numCS++;
				secondPoint--;
				boolean thirdPointFound = false;
				for (thirdPoint=getPoints().length-1; thirdPoint>secondPoint && !thirdPointFound; thirdPoint--){
					p2=getPoint(thirdPoint);
					if (!GeoPoint.collinear(p0, p1, p2)){
						thirdPointFound = true;
						numCS++;
					}
				}			
			}

			//thirdPoint++;
			//Application.debug(" secondPoint = "+secondPoint+"\n thirdPoint = "+thirdPoint);
			//Application.debug(" p0 = "+p0.getLabel()+"\n p1 = "+p1.getLabel()+"\n p2 = "+p2.getLabel());
		}
			
			
	}
	
	/** returns true if the segment ((x1,y1),(x2,y2)) intersects [Ox) */
	private boolean intersectOx(double x1, double y1, double x2, double y2){
		if (y1*y2>0) //segment totally above or under
			return false;
		else{ 
			if (y1>y2){ //first point under (Ox)
				double y=y1; y1=y2; y2=y;
				double x=x1; x1=x2; x2=x;
			}
			if (y2==0) //half-plane
				return false;
			else if ((x1<0) && (x2<0)) //segment totally on the left
				return false;
			else if ((x1>0) && (x2>0)) //segment totally on the right
				return true;
			else if (x2*y1<=x1*y2) //angle >= 0�
				return true;
			else
				return false;
		}
	}
	
	
	/**
	 * returns all class-specific xml tags for getXML
	 * GeoGebra File Format
	 */
	protected void getXMLtags(StringBuilder sb) {
		getLineStyleXML(sb);
		getXMLvisualTags(sb);
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);		
	}
	
	
	

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals etc)
	 */
	public int getMinimumLineThickness() {		
		return 0;
	}

   public boolean trace;


	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
	
	
	
	/////////////////////
	// 3D stuff
	/////////////////////

	public GgbVector getViewDirection(){
		return viewDirection;
	}

	/**
	 * Returns the i-th 3D point of this polygon.
	 * @param i number of point
	 * @return the i-th point
	 */	 
	public GgbVector getPoint3D(int i){
		return getPoint(i).getCoordsInD(3);
	}
	

	/** if this is a part of a closed surface
	 * @return if this is a part of a closed surface
	 */
	public boolean isPartOfClosedSurface(){
		return false; //TODO
	}


	public boolean hasDrawable3D() {
		return true;
	}	
	
	public GgbVector getLabelPosition(){
		double x = 0;
		double y = 0;
		double z = 0;
		for (int i=0; i<getPointsLength(); i++){
			GgbVector coords = getPoint3D(i);
			x+=coords.getX();
			y+=coords.getY();
			z+=coords.getZ();
		}
		return new GgbVector(x/getPointsLength(), y/getPointsLength(), z/getPointsLength(), 1);
	}

}
