package geogebra3D.euclidian3D;

import geogebra.kernel.GeoElement;
import geogebra3D.euclidian3D.opengl.Renderer;


/**
 * 
 * @author ggb3D
 *	
 *	for "solid" drawables, like lines, segments, etc.
 *	these are drawable that are not to become transparent
 *
 */


public abstract class Drawable3DCurves extends Drawable3D {


	/** amplitude for blinking highlighting */
	private static int HIGHLIGHTING_AMPLITUDE = 64;
	
	/**
	 * common constructor
	 * @param a_view3d
	 * @param a_geo
	 */
	public Drawable3DCurves(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
	}

	
	/**
	 * constructor for previewables
	 * @param a_view3d
	 */
	public Drawable3DCurves(EuclidianView3D a_view3d) {
		super(a_view3d);
	}

	public void draw(Renderer renderer) {
		
		if(!isVisible())
			return;	
			
		setHighlightingColor(HIGHLIGHTING_AMPLITUDE, 1f);
		//renderer.setDash(Renderer.DASH_NONE); 
		//renderer.setMatrix(getMatrix());
		
		drawGeometry(renderer);
		
		

	}

	
	
	
	public void drawHidden(Renderer renderer){
		
		if(!isVisible())
			return;
		
		setHighlightingColor(HIGHLIGHTING_AMPLITUDE, 1f);
		
		//renderer.setDash(getGeoElement().getLineType()); 
		renderer.getTextures().setDashFromLineType(getGeoElement().getLineType()); 
		//renderer.setMatrix(getMatrix());		
		drawGeometryHidden(renderer);		
		


	} 
	
	
	
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	

	
	public void drawGeometryPicked(Renderer renderer){
		drawGeometry(renderer);
	}
	
	public void drawGeometryHidden(Renderer renderer){
		
		drawGeometry(renderer);
	} 
	


	
	// methods not used for solid drawables
	public void drawHiding(Renderer renderer) {}
	public void drawTransp(Renderer renderer) {}

	public boolean isTransparent() {
		return false;
	}
	
	
	public int getType(){
		return DRAW_TYPE_CURVES;
	}
	
	

}
