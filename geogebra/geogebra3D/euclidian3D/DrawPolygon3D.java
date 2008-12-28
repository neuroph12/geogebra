package geogebra3D.euclidian3D;




import geogebra.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoTriangle3D;




public class DrawPolygon3D extends Drawable3DTransparent {


	
	
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoTriangle3D a_triangle3D){
		
		super(a_view3D, a_triangle3D);
	}
	

	
	//drawing

	public void drawGeometry(EuclidianRenderer3D renderer) {
		renderer.drawTriangle();
	}
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		renderer.drawTriangle();
	}
	
	public void drawGeometryHidden(EuclidianRenderer3D renderer){};
	
	
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}	
	
	

	
	

	

}
