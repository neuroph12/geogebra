package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdPolygon;
import geogebra.kernel.kernel3D.GeoPoint3D;
import geogebra.kernel.kernel3D.Kernel3D;
import geogebra.main.MyError;



/*
 * Polygon[ <GeoPoint3D>, <GeoPoint3D>, ... ] or CmdPolygon
 */
public class CmdPolygon3D extends CmdPolygon {
	

	public CmdPolygon3D(Kernel kernel) {
		super(kernel);
				
	}
	
	
	public GeoElement[] process(Command c) throws MyError {	
		
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		//check if each arguments are 3D args
		boolean ok3D = true;
		for(int i=0;i<n;i++)
			ok3D = ok3D && (arg[i].isGeoElement3D());
		
		//Application.debug("CmdPolygon3D - "+ok3D);
		//Application.printStacktrace("");
		
		if (ok3D){
			// polygon for given points
			GeoPoint3D[] points = new GeoPoint3D[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				else {
					points[i] = (GeoPoint3D) arg[i];
				}
			}
			// everything ok
			/*
			String s="";
			for (int i=0;i<c.getLabels().length;i++)
				s+=c.getLabels()[i]+", ";
			Application.debug("labels = "+s);
			*/
			return kernel.getManager3D().Polygon3D(c.getLabels(), points);
		}
 
		return super.process(c);
	}

}
