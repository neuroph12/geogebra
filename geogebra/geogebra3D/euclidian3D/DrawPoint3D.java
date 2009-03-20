package geogebra3D.euclidian3D;



import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.kernel3D.ConstructionDefaults3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Color;





public class DrawPoint3D extends Drawable3DSolid{
	
	
	
	
	
		
	
	public DrawPoint3D(EuclidianView3D a_view3D, GeoPoint3D a_point3D) {     
		
		super(a_view3D, a_point3D);
		
	}
	
	
	
	
	

	public void drawGeometry(EuclidianRenderer3D renderer) {
		GeoPoint3D l_point = (GeoPoint3D) getGeoElement(); 
		
		if (l_point.hasCoordDecoration()){
			renderer.setThickness(LINE3D_THICKNESS);
			//TODO use gui
			renderer.drawCoordSegments(ConstructionDefaults3D.colXAXIS,ConstructionDefaults3D.colYAXIS,ConstructionDefaults3D.colZAXIS); 
		}
		
		if (l_point.hasPath())
			renderer.drawSphere(POINT3D_RADIUS*POINT_ON_PATH_DILATATION*l_point.getPointSize()); //points on path are more visible 
		else
			renderer.drawSphere(POINT3D_RADIUS*l_point.getPointSize());
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		GeoPoint3D l_point = (GeoPoint3D) getGeoElement();
		renderer.drawSphere(POINT3D_RADIUS*PICKED_DILATATION*l_point.getPointSize());
		
	}


	

	
	public void drawGeometryHidden(EuclidianRenderer3D renderer){
		drawGeometry(renderer);
		
	}	
	

	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_0D;
	}	
	
	
	
	

}
