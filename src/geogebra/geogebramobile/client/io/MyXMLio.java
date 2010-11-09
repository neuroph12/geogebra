package geogebra.geogebramobile.client.io;

import geogebra.geogebramobile.client.GeoGebraMobile;
import geogebra.geogebramobile.client.kernel.Construction;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.main.Application;

public class MyXMLio {
	
	private Application app;
	private Kernel kernel;
	private Construction cons;
	
	public MyXMLio(Kernel kernel, Construction cons) {
		this.kernel = kernel;
		this.cons = cons;	
		app = kernel.getApplication();

	}
	
	private final static void addXMLHeader(StringBuilder sb) {
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
	}
	
	private final static void addGeoGebraHeader(StringBuilder sb, boolean isMacro) {
		sb.append("<geogebra format=\"" + GeoGebraMobile.XML_FILE_FORMAT + "\"");
		sb.append(" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/");
		if (isMacro)
			sb.append(GeoGebraMobile.GGT_XSD_FILENAME); //eg	ggt.xsd
		else
			sb.append(GeoGebraMobile.GGB_XSD_FILENAME); //eg	ggb.xsd
		sb.append("\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");
	}

	public String getFullXML() {
		StringBuilder sb = new StringBuilder();
		addXMLHeader(sb);
		addGeoGebraHeader(sb, false);
		//sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		//sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\"");
		//sb.append(" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/");
		//sb.append(GeoGebra.GGB_XSD_FILENAME); //eg	ggb.xsd
		//sb.append("\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(false));		

		// save construction
		cons.getConstructionXML(sb);
		
		// save cas session
		if (app.hasFullGui() && app.getGuiManager().hasCasView()) {
			app.getGuiManager().getCasView().getSessionXML(sb);
		}

		sb.append("</geogebra>");
		return sb.toString();
	}

}
