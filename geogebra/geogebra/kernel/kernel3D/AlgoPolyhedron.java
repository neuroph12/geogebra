package geogebra.kernel.kernel3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.main.Application;

import java.util.ArrayList;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoPolyhedron extends AlgoElement3D {

	/** the polyhedron created */
	protected GeoPolyhedron polyhedron;
	
	/** all points of the polyhedron (needed for the faces description) */
	private GeoPoint3D[] inputPoints;
	
	/** lists describing the faces (for polyhedron of TYPE_NONE) */
	private GeoList faces = null;
	
	
	/** points generated as output (e.g. for prisms) */
	private GeoPoint3D[] outputPoints;
	
	
	private int type;
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	////////////////////////////////////////////
	
	private AlgoPolyhedron(Construction c){
		super(c);
		polyhedron = new GeoPolyhedron(c);
	}
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param label name
	 * @param points vertices
	 * @param type type of polyhedron
	 */
	/*
	public AlgoPolyhedron(Construction c, String label, GeoPoint3D[] points, int type) {
		this(c,points,type);
		polyhedron.setLabel(label);
	}
	*/
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param a_points vertices
	 * @param type type of polyhedron
	 */
	public AlgoPolyhedron(Construction c, String[] labels, GeoPoint3D[] a_points, int type) {
		this(c);
		
		this.type = type;
		
		
		
		int numPoints;
		
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID://construct a pyramid with last point as apex
			this.inputPoints = a_points;
			outputPoints = new GeoPoint3D[0];
			numPoints = inputPoints.length;
			
			
			//base of the pyramid
			polyhedron.startNewFace();
			//for (int i=numPoints-2; i>=0; i--)
			for (int i=0; i<numPoints-1; i++)
				polyhedron.addPointToCurrentFace(this.inputPoints[i]);
			polyhedron.endCurrentFace();
			
			//sides of the pyramid
			for (int i=0; i<numPoints-1; i++){
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(this.inputPoints[i]);
				polyhedron.addPointToCurrentFace(this.inputPoints[(i+1)%(numPoints-1)]);
				polyhedron.addPointToCurrentFace(this.inputPoints[numPoints-1]);//apex
				polyhedron.endCurrentFace();
			}
			

			
			break;
			
			/*
		case GeoPolyhedron.TYPE_PSEUDO_PRISM://construct a "pseudo-prismatic" polyhedron
			this.points = a_points;
			outputPointsIndex = a_points.length;
			numPoints = points.length /2;
			faces = new int[numPoints+2][];
			for (int i=0; i<numPoints; i++){
				faces[i]=new int[4];
				faces[i][0]=i;
				faces[i][1]=(i+1)%(numPoints);
				faces[i][2]=numPoints + ((i+1)%(numPoints));
				faces[i][3]=numPoints + i;
			}
			faces[numPoints]=new int[numPoints];
			for (int i=0; i<numPoints; i++)
				faces[numPoints][i]=numPoints-i-1;
			faces[numPoints+1]=new int[numPoints];
			for (int i=0; i<numPoints; i++)
				faces[numPoints+1][i]=numPoints+i;
			break;
			*/

		case GeoPolyhedron.TYPE_PRISM://construct a prism
			inputPoints = a_points;
			numPoints = a_points.length - 1;
			GeoPoint3D[] points = new GeoPoint3D[numPoints*2];
			outputPoints = new GeoPoint3D[numPoints-1];
			for(int i=0;i<numPoints+1;i++)
				points[i] = a_points[i];
			for(int i=0;i<numPoints-1;i++){
				outputPoints[i] = (GeoPoint3D) kernel.getManager3D().Point3D(null, 0, 0, 0);
				points[numPoints+1+i] = outputPoints[i];
			}
			
			//bottom of the prism
			polyhedron.startNewFace();
			//for (int i=numPoints-1; i>=0; i--)
			for (int i=0; i<numPoints; i++)
				polyhedron.addPointToCurrentFace(points[i]);
			polyhedron.endCurrentFace();
			
			//sides of the prism
			for (int i=0; i<numPoints; i++){
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(points[i]);
				polyhedron.addPointToCurrentFace(points[(i+1)%(numPoints)]);
				polyhedron.addPointToCurrentFace(points[numPoints + ((i+1)%(numPoints))]);
				polyhedron.addPointToCurrentFace(points[numPoints + i]);
				polyhedron.endCurrentFace();
			}
			

			
			//top of the prism
			polyhedron.startNewFace();
			for (int i=0; i<numPoints; i++)
				polyhedron.addPointToCurrentFace(points[numPoints+i]);
			polyhedron.endCurrentFace();
			
			break;
		}
		
		polyhedron.updateFaces();
		setInputOutput();
		polyhedron.initLabels(labels);
		compute();

		
	}
	
	
	
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF TYPE NONE
	////////////////////////////////////////////

	
	/** creates a polyhedron regarding faces description
	 * @param c construction 
	 * @param label name
	 * @param faces faces description
	 */
	public AlgoPolyhedron(Construction c, String[] labels, GeoList faces) {
		this(c,faces);
		polyhedron.initLabels(labels);
	}
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param faces faces description
	 */
	public AlgoPolyhedron(Construction c, GeoList faces) {
		this(c);
		this.type = GeoPolyhedron.TYPE_NONE;
		this.faces = faces;
		setFaces();

		outputPoints = new GeoPoint3D[0];
		
		setInputOutput();
		
	}
	
	
	/** send GeoList description of faces to polyhedron
	 * and update polyhedron polygons and segments
	 * 
	 */
	private void setFaces(){
		
		
		for(int i=0;i<faces.size();i++){ 
			polyhedron.startNewFace();
			GeoList list = (GeoList) faces.get(i);
			for (int j=0;j<list.size();j++){
				GeoPoint3D point = (GeoPoint3D) list.get(j);
				polyhedron.addPointToCurrentFace(point);
	
			}
			polyhedron.endCurrentFace();
		}
		
		polyhedron.updateFaces();

		
	}

	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	protected void setInputOutput(){
		
		// input : inputPoints or list of faces
		if(this.faces == null){
			input = inputPoints;
		}else{
			input = new GeoElement[1];
			input[0] = this.faces;
		}
		
		for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }

		
		updateOutput();
		for (int i=0;i<outputPoints.length;i++)
			outputPoints[i].setParentAlgorithm(this);
			
		
        polyhedron.setParentAlgorithm(this); 
       
        cons.addToAlgorithmList(this);  
		
	}
	
	
	
	
	
	private void updateOutput(){
						
		GeoSegment3D[] segments = polyhedron.getSegments();
		GeoPolygon3D[] polygons = polyhedron.getFaces();
		
		
		// output : polyhedron, polygons, segments, and outputPoints			
		output = new GeoElement[1+polygons.length+segments.length+outputPoints.length];	
		
		output[0] = polyhedron;
		for(int i=0; i<polygons.length; i++)
			output[1+i] = polygons[i];
		for(int i=0; i<segments.length; i++)
			output[1+polygons.length+i] = segments[i];
		for(int i=0; i<outputPoints.length; i++){
			output[1+polygons.length+segments.length+ i] = outputPoints[i];
		}
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

    

	
	
	protected void compute() {
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID:
			//TODO remove this and replace with tesselation
			GgbVector interiorPoint = new GgbVector(4);
			for (int i=0;i<inputPoints.length;i++){
				interiorPoint = (GgbVector) interiorPoint.add(inputPoints[i].getCoords());
			}
			interiorPoint = (GgbVector) interiorPoint.mul((double) 1/(inputPoints.length));
			polyhedron.setInteriorPoint(interiorPoint);
			//Application.debug("interior\n"+interiorPoint);
			break;
		case GeoPolyhedron.TYPE_PRISM:
			//translation from bottom to top
			GgbVector v = inputPoints[inputPoints.length-1].getCoords().sub(inputPoints[0].getCoords());
			//Application.debug("v=\n"+v);
			//translate all output points
			for (int i=0;i<outputPoints.length;i++){
				outputPoints[i].setCoords(inputPoints[i+1].getCoords().add(v));
				outputPoints[i].updateCoords();
				//outputPoints[i].update();
				//outputPoints[i].updateCascade();
				//Application.debug("point["+i+"]="+points[i]);
			}
			
			
			polyhedron.updatePolygonsAndSegmentsFromParentAlgorithms();
			//polyhedron.update();			
			
			//TODO remove this and replace with tesselation
			interiorPoint = new GgbVector(4);
			for (int i=0;i<inputPoints.length-1;i++){
				interiorPoint = (GgbVector) interiorPoint.add(inputPoints[i].getCoords());
			}
			interiorPoint = (GgbVector) interiorPoint.mul((double) 1/(inputPoints.length-1));
			polyhedron.setInteriorPoint((GgbVector) interiorPoint.add(v.mul(0.5)));
			
			break;
		case GeoPolyhedron.TYPE_NONE:
			//Application.printStacktrace("compute");
			polyhedron.restartFaces();
			setFaces();	
			updateOutput();
			break;
		default:
		}

	}



	public String getClassName() {
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID:
			return "AlgoPyramid";
		case GeoPolyhedron.TYPE_PRISM:
			return "AlgoPrism";
		default:
			return "AlgoPolyhedron";
		}
	}
	

}
