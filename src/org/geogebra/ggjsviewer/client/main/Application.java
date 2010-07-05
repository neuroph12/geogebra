package org.geogebra.ggjsviewer.client.main;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.geogebra.ggjsviewer.client.euclidian.EuclidianController;
import org.geogebra.ggjsviewer.client.euclidian.EuclidianView;
import org.geogebra.ggjsviewer.client.gui.Base64Form;
import org.geogebra.ggjsviewer.client.gui.GgjsViewerWrapper;
import org.geogebra.ggjsviewer.client.io.MyXMLHandler;
import org.geogebra.ggjsviewer.client.kernel.BaseApplication;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoLine;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.Kernel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class Application extends BaseApplication {
	
	private Kernel kernel;
	private EuclidianView euclidianview;
	private EuclidianController euclidiancontroller;
	private MyXMLHandler xmlhandler;
	//private Hashtable translateCommandTable;
	
	protected boolean showMenuBar = true;
	
	private ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();
	
	
	public Application() {
		super();
		initKernel();	
		initEuclidianView();
		initXmlHandler();
		xmlhandler.parseXml("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""+
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
		"<html \"xmlns=\"http://www.w3.org/1999/xhtml\">"+
		"<head>"+
		"<title>"+
		"</title>"+
		"<body>"+
		"<script type=\"text/javascript\">"+
		"loadBase64Unzipped('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPGdlb2dlYnJhIGZvcm1hdD0iMy4zIiB4c2k6bm9OYW1lc3BhY2VTY2hlbWFMb2NhdGlvbj0iaHR0cDovL3d3dy5nZW9nZWJyYS5vcmcvZ2diLnhzZCIgeG1sbnM9IiIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgPgo8Z3VpPgoJPHdpbmRvdyB3aWR0aD0iODAwIiBoZWlnaHQ9IjYwMCIgLz4JPHBlcnNwZWN0aXZlcz4KPHBlcnNwZWN0aXZlIGlkPSJ0bXAiPgoJPHBhbmVzPgoJCTxwYW5lIGxvY2F0aW9uPSIiIGRpdmlkZXI9IjAuMjM1IiBvcmllbnRhdGlvbj0iMSIgLz4KCTwvcGFuZXM+Cgk8dmlld3M+CgkJPHZpZXcgaWQ9IjEiIHZpc2libGU9InRydWUiIGluZnJhbWU9ImZhbHNlIiBsb2NhdGlvbj0iMSIgc2l6ZT0iNzk4IiB3aW5kb3c9IjEwMCwxMDAsNjAwLDQwMCIgLz4KCQk8dmlldyBpZD0iMiIgdmlzaWJsZT0iZmFsc2UiIGluZnJhbWU9ImZhbHNlIiBsb2NhdGlvbj0iMyIgc2l6ZT0iMjAwIiB3aW5kb3c9IjEwMCwxMDAsMjUwLDQwMCIgLz4KCQk8dmlldyBpZD0iNCIgdmlzaWJsZT0iZmFsc2UiIGluZnJhbWU9ImZhbHNlIiBsb2NhdGlvbj0iIiBzaXplPSIzMDAiIHdpbmRvdz0iMTAwLDEwMCw2MDAsNDAwIiAvPgoJCTx2aWV3IGlkPSI4IiB2aXNpYmxlPSJmYWxzZSIgaW5mcmFtZT0iZmFsc2UiIGxvY2F0aW9uPSIiIHNpemU9IjMwMCIgd2luZG93PSIxMDAsMTAwLDYwMCw0MDAiIC8+Cgk8L3ZpZXdzPgoJPHRvb2xiYXI+MCAzOSA1OSB8fCAxIDUwMSA1IDE5IHwgMiAxNSA0NSAxOCAsIDcgMzcgfCA0IDMgOCA5ICwgMTMgNDQgLCA1OCAsIDQ3IHx8IDE2IDUxIHwgMTAgMzQgNTMgMTEgLCAyNCAgMjAgMjIgLCAyMSAyMyB8IDU1IDU2IDU3ICwgMTIgfHwgMzYgNDYgLCAzOCA0OSA1MCB8IDMwIDI5IDU0IDMyIDMxIDMzIHwgMjUgNTIgNjAgNjEgLCA2MiAsIDYzICwgMTcgMjYgLCAxNCB8fCA0MCA0MSA0MiAsIDI3IDI4IDM1ICwgNjwvdG9vbGJhcj4KCTxzaG93IGF4ZXM9ImZhbHNlIiBncmlkPSJmYWxzZSIgLz4KCTxpbnB1dCBzaG93PSJ0cnVlIiBjbWQ9InRydWUiIHRvcD0iZmFsc2UiIC8+CjwvcGVyc3BlY3RpdmU+Cgk8L3BlcnNwZWN0aXZlcz4KPC9ndWk+CjxldWNsaWRpYW5WaWV3PgoJPHNpemUgIHdpZHRoPSI3OTgiIGhlaWdodD0iNDQzIi8+Cgk8Y29vcmRTeXN0ZW0geFplcm89IjIxNS4wIiB5WmVybz0iMzE1LjAiIHNjYWxlPSI1MC4wIiB5c2NhbGU9IjUwLjAiLz4KCTxldlNldHRpbmdzIGF4ZXM9ImZhbHNlIiBncmlkPSJmYWxzZSIgZ3JpZElzQm9sZD0iZmFsc2UiIHBvaW50Q2FwdHVyaW5nPSIzIiByaWdodEFuZ2xlU3R5bGU9IjEiIGNoZWNrYm94U2l6ZT0iMTMiIGdyaWRUeXBlPSIwIi8+Cgk8YmdDb2xvciByPSIyNTUiIGc9IjI1NSIgYj0iMjU1Ii8+Cgk8YXhlc0NvbG9yIHI9IjAiIGc9IjAiIGI9IjAiLz4KCTxncmlkQ29sb3Igcj0iMTkyIiBnPSIxOTIiIGI9IjE5MiIvPgoJPGxpbmVTdHlsZSBheGVzPSIxIiBncmlkPSIxMCIvPgoJPGF4aXMgaWQ9IjAiIHNob3c9ImZhbHNlIiBsYWJlbD0iIiB1bml0TGFiZWw9IiIgdGlja1N0eWxlPSIxIiBzaG93TnVtYmVycz0idHJ1ZSIvPgoJPGF4aXMgaWQ9IjEiIHNob3c9ImZhbHNlIiBsYWJlbD0iIiB1bml0TGFiZWw9IiIgdGlja1N0eWxlPSIxIiBzaG93TnVtYmVycz0idHJ1ZSIvPgo8L2V1Y2xpZGlhblZpZXc+CjxrZXJuZWw+Cgk8Y29udGludW91cyB2YWw9ImZhbHNlIi8+Cgk8ZGVjaW1hbHMgdmFsPSIyIi8+Cgk8YW5nbGVVbml0IHZhbD0iZGVncmVlIi8+Cgk8YWxnZWJyYVN0eWxlIHZhbD0iMCIvPgoJPGNvb3JkU3R5bGUgdmFsPSIwIi8+Cjwva2VybmVsPgo8Y29uc3RydWN0aW9uIHRpdGxlPSIiIGF1dGhvcj0iTWljaGFlbCBCb3JjaGVyZHMiIGRhdGU9IiI+CjxlbGVtZW50IHR5cGU9InBvaW50IiBsYWJlbD0iQSI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjAiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE9mZnNldCB4PSI0MCIgeT0iMzMiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8YW5pbWF0aW9uIHN0ZXA9IjAuMSIgc3BlZWQ9IjEuMCIgdHlwZT0iMCIgcGxheWluZz0iZmFsc2UiLz4KCTxjb29yZHMgeD0iNS41IiB5PSI0LjIyIiB6PSIxLjAiLz4KCTxwb2ludFNpemUgdmFsPSI1Ii8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8ZWxlbWVudCB0eXBlPSJwb2ludCIgbGFiZWw9IkIiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIwIiBnPSIwIiBiPSIwIiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGFuaW1hdGlvbiBzdGVwPSIwLjEiIHNwZWVkPSIxLjAiIHR5cGU9IjAiIHBsYXlpbmc9ImZhbHNlIi8+Cgk8Y29vcmRzIHg9Ii0yLjc2MDAwMDAwMDAwMDAwMDIiIHk9IjIuMjQiIHo9IjEuMCIvPgoJPHBvaW50U2l6ZSB2YWw9IjUiLz4KCTxwb2ludFN0eWxlIHZhbD0iMCIvPgo8L2VsZW1lbnQ+CjxlbGVtZW50IHR5cGU9InBvaW50IiBsYWJlbD0iQyI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjAiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8YW5pbWF0aW9uIHN0ZXA9IjAuMSIgc3BlZWQ9IjEuMCIgdHlwZT0iMCIgcGxheWluZz0iZmFsc2UiLz4KCTxjb29yZHMgeD0iNy4wMjAwMDAwMDAwMDAwMDA1IiB5PSItMC41NiIgej0iMS4wIi8+Cgk8cG9pbnRTaXplIHZhbD0iNSIvPgoJPHBvaW50U3R5bGUgdmFsPSIwIi8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iU2VnbWVudCI+Cgk8aW5wdXQgYTA9IkIiIGExPSJBIi8+Cgk8b3V0cHV0IGEwPSJhIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0ic2VnbWVudCIgbGFiZWw9ImEiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIwIiBnPSIwIiBiPSIwIiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSItMS45Nzk5OTk5OTk5OTk5OTk1IiB5PSI4LjI2IiB6PSItMjMuOTY3MiIvPgoJPGxpbmVTdHlsZSB0aGlja25lc3M9IjUiIHR5cGU9IjAiLz4KCTxlcW5TdHlsZSBzdHlsZT0iaW1wbGljaXQiLz4KCTxvdXRseWluZ0ludGVyc2VjdGlvbnMgdmFsPSJmYWxzZSIvPgoJPGtlZXBUeXBlT25UcmFuc2Zvcm0gdmFsPSJ0cnVlIi8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iU2VnbWVudCI+Cgk8aW5wdXQgYTA9IkEiIGExPSJDIi8+Cgk8b3V0cHV0IGEwPSJiIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0ic2VnbWVudCIgbGFiZWw9ImIiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIwIiBnPSIwIiBiPSIwIiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSI0Ljc3OTk5OTk5OTk5OTk5OSIgeT0iMS41MjAwMDAwMDAwMDAwMDA1IiB6PSItMzIuNzA0NCIvPgoJPGxpbmVTdHlsZSB0aGlja25lc3M9IjUiIHR5cGU9IjAiLz4KCTxlcW5TdHlsZSBzdHlsZT0iaW1wbGljaXQiLz4KCTxvdXRseWluZ0ludGVyc2VjdGlvbnMgdmFsPSJmYWxzZSIvPgoJPGtlZXBUeXBlT25UcmFuc2Zvcm0gdmFsPSJ0cnVlIi8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iU2VnbWVudCI+Cgk8aW5wdXQgYTA9IkMiIGExPSJCIi8+Cgk8b3V0cHV0IGEwPSJjIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0ic2VnbWVudCIgbGFiZWw9ImMiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIwIiBnPSIwIiBiPSIwIiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSItMi44MDAwMDAwMDAwMDAwMDAzIiB5PSItOS43ODAwMDAwMDAwMDAwMDEiIHo9IjE0LjE3OTIwMDAwMDAwMDAwMiIvPgoJPGxpbmVTdHlsZSB0aGlja25lc3M9IjUiIHR5cGU9IjAiLz4KCTxlcW5TdHlsZSBzdHlsZT0iaW1wbGljaXQiLz4KCTxvdXRseWluZ0ludGVyc2VjdGlvbnMgdmFsPSJmYWxzZSIvPgoJPGtlZXBUeXBlT25UcmFuc2Zvcm0gdmFsPSJ0cnVlIi8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iTGluZUJpc2VjdG9yIj4KCTxpbnB1dCBhMD0iYyIvPgoJPG91dHB1dCBhMD0iZSIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9ImxpbmUiIGxhYmVsPSJlIj4KCTxzaG93IG9iamVjdD0idHJ1ZSIgbGFiZWw9ImZhbHNlIi8+Cgk8b2JqQ29sb3Igcj0iMCIgZz0iMjU1IiBiPSIyNTUiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE9mZnNldCB4PSItODAiIHk9IjciLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9IjkuNzgwMDAwMDAwMDAwMDAxIiB5PSItMi44MDAwMDAwMDAwMDAwMDAzIiB6PSItMTguNDc5NDAwMDAwMDAwMDAyIi8+Cgk8bGluZVN0eWxlIHRoaWNrbmVzcz0iNSIgdHlwZT0iMCIvPgoJPGVxblN0eWxlIHN0eWxlPSJpbXBsaWNpdCIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9IkxpbmVCaXNlY3RvciI+Cgk8aW5wdXQgYTA9ImEiLz4KCTxvdXRwdXQgYTA9ImYiLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJsaW5lIiBsYWJlbD0iZiI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjI1NSIgYj0iMjU1IiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSItOC4yNiIgeT0iLTEuOTc5OTk5OTk5OTk5OTk5NSIgej0iMTcuNzExNTk5OTk5OTk5OTk3Ii8+Cgk8bGluZVN0eWxlIHRoaWNrbmVzcz0iNSIgdHlwZT0iMCIvPgoJPGVxblN0eWxlIHN0eWxlPSJpbXBsaWNpdCIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9IkxpbmVCaXNlY3RvciI+Cgk8aW5wdXQgYTA9ImIiLz4KCTxvdXRwdXQgYTA9ImciLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJsaW5lIiBsYWJlbD0iZyI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjI1NSIgYj0iMjU1IiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSItMS41MjAwMDAwMDAwMDAwMDA1IiB5PSI0Ljc3OTk5OTk5OTk5OTk5OSIgej0iMC43Njc4MDAwMDAwMDAwMDI5Ii8+Cgk8bGluZVN0eWxlIHRoaWNrbmVzcz0iNSIgdHlwZT0iMCIvPgoJPGVxblN0eWxlIHN0eWxlPSJpbXBsaWNpdCIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9IkludGVyc2VjdCI+Cgk8aW5wdXQgYTA9ImUiIGExPSJnIi8+Cgk8b3V0cHV0IGEwPSJEIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0icG9pbnQiIGxhYmVsPSJEIj4KCTxzaG93IG9iamVjdD0idHJ1ZSIgbGFiZWw9ImZhbHNlIi8+Cgk8b2JqQ29sb3Igcj0iMCIgZz0iMjU1IiBiPSIyNTUiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9Ijg2LjE4MTY5MTk5OTk5OTk4IiB5PSIyMC41Nzk2MDM5OTk5OTk5ODIiIHo9IjQyLjQ5MjM5OTk5OTk5OTk5NiIvPgoJPHBvaW50U2l6ZSB2YWw9IjkiLz4KCTxwb2ludFN0eWxlIHZhbD0iMCIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9Ik1pZHBvaW50Ij4KCTxpbnB1dCBhMD0iYSIvPgoJPG91dHB1dCBhMD0iRSIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9InBvaW50IiBsYWJlbD0iRSI+Cgk8c2hvdyBvYmplY3Q9ImZhbHNlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIwIiBnPSIyNTUiIGI9IjI1NSIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iMS4zNjk5OTk5OTk5OTk5OTk5IiB5PSIzLjIzIiB6PSIxLjAiLz4KCTxwb2ludFNpemUgdmFsPSIzIi8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJNaWRwb2ludCI+Cgk8aW5wdXQgYTA9ImIiLz4KCTxvdXRwdXQgYTA9IkYiLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJwb2ludCIgbGFiZWw9IkYiPgoJPHNob3cgb2JqZWN0PSJmYWxzZSIgbGFiZWw9ImZhbHNlIi8+Cgk8b2JqQ29sb3Igcj0iNjQiIGc9IjY0IiBiPSI2NCIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iNi4yNiIgeT0iMS44Mjk5OTk5OTk5OTk5OTk4IiB6PSIxLjAiLz4KCTxwb2ludFNpemUgdmFsPSIzIi8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJNaWRwb2ludCI+Cgk8aW5wdXQgYTA9ImMiLz4KCTxvdXRwdXQgYTA9IkciLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJwb2ludCIgbGFiZWw9IkciPgoJPHNob3cgb2JqZWN0PSJmYWxzZSIgbGFiZWw9ImZhbHNlIi8+Cgk8b2JqQ29sb3Igcj0iNjQiIGc9IjY0IiBiPSI2NCIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iMi4xMyIgeT0iMC44NDAwMDAwMDAwMDAwMDAxIiB6PSIxLjAiLz4KCTxwb2ludFNpemUgdmFsPSIzIi8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJTZWdtZW50Ij4KCTxpbnB1dCBhMD0iQiIgYTE9IkYiLz4KCTxvdXRwdXQgYTA9ImQiLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJzZWdtZW50IiBsYWJlbD0iZCI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjAiIGI9IjI1NSIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iMC40MTAwMDAwMDAwMDAwMDAzNiIgeT0iOS4wMiIgej0iLTE5LjA3MzIiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSI1IiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+Cgk8b3V0bHlpbmdJbnRlcnNlY3Rpb25zIHZhbD0iZmFsc2UiLz4KCTxrZWVwVHlwZU9uVHJhbnNmb3JtIHZhbD0idHJ1ZSIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9IlNlZ21lbnQiPgoJPGlucHV0IGEwPSJBIiBhMT0iRyIvPgoJPG91dHB1dCBhMD0iaCIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9InNlZ21lbnQiIGxhYmVsPSJoIj4KCTxzaG93IG9iamVjdD0idHJ1ZSIgbGFiZWw9ImZhbHNlIi8+Cgk8b2JqQ29sb3Igcj0iMCIgZz0iMCIgYj0iMjU1IiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSIzLjM4IiB5PSItMy4zNyIgej0iLTQuMzY4NTk5OTk5OTk5OTk4Ii8+Cgk8bGluZVN0eWxlIHRoaWNrbmVzcz0iNSIgdHlwZT0iMCIvPgoJPGVxblN0eWxlIHN0eWxlPSJpbXBsaWNpdCIvPgoJPG91dGx5aW5nSW50ZXJzZWN0aW9ucyB2YWw9ImZhbHNlIi8+Cgk8a2VlcFR5cGVPblRyYW5zZm9ybSB2YWw9InRydWUiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJTZWdtZW50Ij4KCTxpbnB1dCBhMD0iRSIgYTE9IkMiLz4KCTxvdXRwdXQgYTA9ImkiLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJzZWdtZW50IiBsYWJlbD0iaSI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjAiIGI9IjI1NSIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iMy43OSIgeT0iNS42NSIgej0iLTIzLjQ0MTgiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSI1IiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+Cgk8b3V0bHlpbmdJbnRlcnNlY3Rpb25zIHZhbD0iZmFsc2UiLz4KCTxrZWVwVHlwZU9uVHJhbnNmb3JtIHZhbD0idHJ1ZSIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9IkludGVyc2VjdCI+Cgk8aW5wdXQgYTA9ImgiIGExPSJkIi8+Cgk8b3V0cHV0IGEwPSJIIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0icG9pbnQiIGxhYmVsPSJIIj4KCTxzaG93IG9iamVjdD0idHJ1ZSIgbGFiZWw9ImZhbHNlIi8+Cgk8b2JqQ29sb3Igcj0iMCIgZz0iMCIgYj0iMjU1IiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSIxMDMuNjgxNDU1OTk5OTk5OTgiIHk9IjYyLjY3NjI5IiB6PSIzMS44NjkzIi8+Cgk8cG9pbnRTaXplIHZhbD0iOSIvPgoJPHBvaW50U3R5bGUgdmFsPSIwIi8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iQW5ndWxhckJpc2VjdG9yIj4KCTxpbnB1dCBhMD0iQSIgYTE9IkIiIGEyPSJDIi8+Cgk8b3V0cHV0IGEwPSJqIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0ibGluZSIgbGFiZWw9ImoiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIyNTUiIGc9IjAiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9IjAuMDIxNzgzMDQyNTkyNjgwNTMyIiB5PSIwLjk5OTc2MjcyMTM3NzEzMDQiIHo9Ii0yLjE3OTM0NzI5ODMyODk3NCIvPgoJPGxpbmVTdHlsZSB0aGlja25lc3M9IjUiIHR5cGU9IjAiLz4KCTxlcW5TdHlsZSBzdHlsZT0iaW1wbGljaXQiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJBbmd1bGFyQmlzZWN0b3IiPgoJPGlucHV0IGEwPSJCIiBhMT0iQyIgYTI9IkEiLz4KCTxvdXRwdXQgYTA9ImsiLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJsaW5lIiBsYWJlbD0iayI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjI1NSIgZz0iMCIgYj0iMCIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iLTAuNjk2NzY1MzAyNTc3NzQ5NSIgeT0iLTAuNzE3Mjk5MTc5NjQ4MDMwMSIgej0iNC40ODk2MDQ4ODM0OTI5MDUiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSI1IiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iQW5ndWxhckJpc2VjdG9yIj4KCTxpbnB1dCBhMD0iQyIgYTE9IkEiIGEyPSJCIi8+Cgk8b3V0cHV0IGEwPSJsIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0ibGluZSIgbGFiZWw9ImwiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIyNTUiIGc9IjAiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9IjAuODcwODcxODI2MjEzNDY1MyIgeT0iLTAuNDkxNTEwMTg1MzU0OTE2MjUiIHo9Ii0yLjcxNTYyMjA2MTk3NjMxMjMiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSI1IiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iSW50ZXJzZWN0Ij4KCTxpbnB1dCBhMD0iaiIgYTE9ImwiLz4KCTxvdXRwdXQgYTA9IkkiLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJwb2ludCIgbGFiZWw9IkkiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIyNTUiIGc9IjAiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9IjMuNzg2MTQ5MDk3NDY3NjIyNCIgeT0iMS44Mzg3Nzc2NTA2MDc0ODIyIiB6PSIwLjg4MTM3MTc3NDI0ODE2NzgiLz4KCTxwb2ludFNpemUgdmFsPSI5Ii8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJPcnRob2dvbmFsTGluZSI+Cgk8aW5wdXQgYTA9IkEiIGExPSJjIi8+Cgk8b3V0cHV0IGEwPSJtIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0ibGluZSIgbGFiZWw9Im0iPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0iZmFsc2UiLz4KCTxvYmpDb2xvciByPSIyNTUiIGc9IjAiIGI9IjIwNCIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iOS43ODAwMDAwMDAwMDAwMDEiIHk9Ii0yLjgwMDAwMDAwMDAwMDAwMDMiIHo9Ii00MS45NzQwMDAwMDAwMDAwMDQiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSI1IiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iT3J0aG9nb25hbExpbmUiPgoJPGlucHV0IGEwPSJCIiBhMT0iYiIvPgoJPG91dHB1dCBhMD0ibiIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9ImxpbmUiIGxhYmVsPSJuIj4KCTxzaG93IG9iamVjdD0idHJ1ZSIgbGFiZWw9ImZhbHNlIi8+Cgk8b2JqQ29sb3Igcj0iMjU1IiBnPSIwIiBiPSIyMDQiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9Ii0xLjUyMDAwMDAwMDAwMDAwMDUiIHk9IjQuNzc5OTk5OTk5OTk5OTk5IiB6PSItMTQuOTAyNDAwMDAwMDAwMDAyIi8+Cgk8bGluZVN0eWxlIHRoaWNrbmVzcz0iNSIgdHlwZT0iMCIvPgoJPGVxblN0eWxlIHN0eWxlPSJpbXBsaWNpdCIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9Ik9ydGhvZ29uYWxMaW5lIj4KCTxpbnB1dCBhMD0iQyIgYTE9ImEiLz4KCTxvdXRwdXQgYTA9InAiLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJsaW5lIiBsYWJlbD0icCI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjI1NSIgZz0iMCIgYj0iMjA0IiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSItOC4yNiIgeT0iLTEuOTc5OTk5OTk5OTk5OTk5NSIgej0iNTYuODc2NCIvPgoJPGxpbmVTdHlsZSB0aGlja25lc3M9IjUiIHR5cGU9IjAiLz4KCTxlcW5TdHlsZSBzdHlsZT0iaW1wbGljaXQiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJJbnRlcnNlY3QiPgoJPGlucHV0IGEwPSJuIiBhMT0icCIvPgoJPG91dHB1dCBhMD0iSiIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9InBvaW50IiBsYWJlbD0iSiI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjI1NSIgZz0iMCIgYj0iMjA0IiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSIyNDIuMzYyNDM5OTk5OTk5OTQiIHk9IjIwOS41NDU5NTIwMDAwMDAwMyIgej0iNDIuNDkyMzk5OTk5OTk5OTkiLz4KCTxwb2ludFNpemUgdmFsPSI5Ii8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJTZWdtZW50Ij4KCTxpbnB1dCBhMD0iRCIgYTE9IkoiLz4KCTxvdXRwdXQgYTA9IkV1bGVyTGluZSIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9InNlZ21lbnQiIGxhYmVsPSJFdWxlckxpbmUiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0idHJ1ZSIvPgoJPG9iakNvbG9yIHI9IjUxIiBnPSIyNTUiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE9mZnNldCB4PSItNzkiIHk9IjE0Ii8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSItNC40NDcwNjIyNTExMzE5NjkiIHk9IjMuNjc1NDk4Mzk1MDA3MTA3IiB6PSI3LjIzOTI5NTY4MDE2ODY5NCIvPgoJPGxpbmVTdHlsZSB0aGlja25lc3M9IjUiIHR5cGU9IjAiLz4KCTxlcW5TdHlsZSBzdHlsZT0iaW1wbGljaXQiLz4KCTxvdXRseWluZ0ludGVyc2VjdGlvbnMgdmFsPSJmYWxzZSIvPgoJPGtlZXBUeXBlT25UcmFuc2Zvcm0gdmFsPSJ0cnVlIi8+CjwvZWxlbWVudD4KPC9jb25zdHJ1Y3Rpb24+CjwvZ2VvZ2VicmE+');"+
		"</script>"+
		"</body>"+
		"</html>");
	}
	
	private void initKernel() {
		kernel = new Kernel(this);
	}
	
	private void initXmlHandler() {
		xmlhandler = kernel.newMyXMLHandler(kernel.getConstruction());
		Base64Form.setApplication(this);
	}
	
	private void initEuclidianView() {
		GgjsViewerWrapper wrapper = new GgjsViewerWrapper();
		RootPanel.get().add(wrapper);
		euclidiancontroller = new EuclidianController(kernel);
		euclidiancontroller.setApplication(this);
		euclidianview = wrapper.getEuclidianView();
		kernel.notifyAddAll(euclidianview);
		kernel.attach(euclidianview);
		euclidianview.setKernel(kernel);
		euclidianview.setApplication(this);
		euclidiancontroller.setEuclidianView(euclidianview);	
		euclidianview.setEuclidianController(euclidiancontroller);
		euclidianview.addMouseDownHandler(euclidiancontroller);
		euclidianview.addMouseUpHandler(euclidiancontroller);
		euclidianview.addMouseWheelHandler(euclidiancontroller);
		euclidianview.addMouseOverHandler(euclidiancontroller);
		euclidianview.addMouseOutHandler(euclidiancontroller);
		euclidianview.addMouseMoveHandler(euclidiancontroller);
		
	
		//CONNECTIONS MUST BE MADE
	}
	
	

	public boolean isLabelDragsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	

	final public void clearSelectedGeos() {
		clearSelectedGeos(true);
		updateSelection();
	}
	
	public MyXMLHandler getMyXmlHandler() {
		return xmlhandler;
	}

	public void clearSelectedGeos(boolean repaint) {
		int size = selectedGeos.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				GeoElement geo = (GeoElement) selectedGeos.get(i);
				geo.setSelected(false);
			}
			selectedGeos.clear();
			if (repaint)
				kernel.notifyRepaint();
		}
		updateSelection();
	}
	
	private void updateSelection() {
		if (!showMenuBar || !hasGuiManager())
			return;

		/*AG I don't know that we need this...getGuiManager().updateMenubarSelection();
		if (getEuclidianView().getMode() == EuclidianView.MODE_VISUAL_STYLE) {
			if (selectedGeos.size() > 0) {
				
				EuclidianController ec = getEuclidianView().getEuclidianController();
				
				for (int i = 0 ; i < selectedGeos.size() ; i++) {
					ec.setProperties(((GeoElement)(selectedGeos.get(i))));
				}
				
			}
		}*/
	}
	
	final public int selectedGeosSize() {
		return selectedGeos.size();
	}

	final public ArrayList getSelectedGeos() {
		return selectedGeos;
	}

	final public GeoElement getLastCreatedGeoElement() {
		return kernel.getConstruction().getLastGeoElement();
	}
	
	final public void toggleSelectedGeo(GeoElement geo) {
		toggleSelectedGeo(geo, true);
	}

	final public void toggleSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null)
			return;

		boolean contains = selectedGeos.contains(geo);
		if (contains) {
			selectedGeos.remove(geo);
			geo.setSelected(false);
		} else {
			selectedGeos.add(geo);
			geo.setSelected(true);
		}

		if (repaint)
			kernel.notifyRepaint();
		updateSelection();
	}

	public void addSelectedGeo(GeoElement geo) {
		// TODO Auto-generated method stub
		addSelectedGeo(geo, true);		
	}

	final public void addSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null || selectedGeos.contains(geo))
			return;

		selectedGeos.add(geo);
		geo.setSelected(true);
		if (repaint)
			kernel.notifyRepaint();
		updateSelection();
	}

	public String translateCommand(String localname) {
		if (localname == null)
			return null;
		else 
			return localname;
		/*AGif (translateCommandTable == null)
			return localname;

		// note: lookup lower case of command name!
		Object value = translateCommandTable.get(localname.toLowerCase());		
		if (value == null)
			return localname;
		else
			return (String) value;*/
	}

	public String getError(String message) {
		// TODO Auto-generated method stub
		return message+"must be implemented Application.java getError()";
	}

	public void showError(String message) {
		GWT.log(message);
		// TODO Auto-generated method stub
		
	}
	
	final public String getCommand(String key) {
		
		return key;
	}


}
