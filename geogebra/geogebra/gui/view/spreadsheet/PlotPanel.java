package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JPanel;

/**
 * 
 * Creates a JPanel with an enclosed instance of EuclidianView and methods for 
 * creating geos in the panel.
 * 
 * @author gsturr 2010-6-30
 *
 */
public class PlotPanel extends JPanel implements ComponentListener {
	
	// ggb 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	
	private ArrayList<GeoElement> plotGeoCollection;
	private GeoElement plotGeo;
	//private GeoList boundList, freqList;
	
	private boolean isAutoRemoveGeos = true;
	
	private double xMinData, xMaxData;
	private double xMinEV, xMaxEV, yMinEV, yMaxEV;
	private boolean showYAxis = false;
	private boolean showArrows = false;
	private boolean forceXAxisBuffer = false;
	
	
	// new EuclidianView instance 
	private myEV ev;
	private EuclidianController ec;
	
	
	
	/*************************************************
	 * Construct the panel
	 */
	public PlotPanel(Application app){
		
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		
		plotGeoCollection = new ArrayList<GeoElement>();
		
		// create an instance of EuclideanView
		ec = new EuclidianController(kernel);
		boolean[] showAxes = { true, true };
		boolean showGrid = false;
		ev = new myEV(ec, showAxes, showGrid);
		ev.setAntialiasing(true);
		ev.updateFonts();
		ev.setPreferredSize(new Dimension(300,200));
		ev.setSize(new Dimension(300,200));
		ev.updateSize();
		
		this.setLayout(new BorderLayout());
		this.add(ev, BorderLayout.CENTER);
		
		this.addComponentListener(this);
		
		
	}

	
	public void removeGeos(){
	//	Application.debug("=========== remove plot geos");
		
		if(plotGeo != null){
			plotGeo.remove();
			plotGeo = null;
		}
		
		for(GeoElement geo : plotGeoCollection){
			if(geo != null)
				geo.remove();
		}
		plotGeoCollection.clear();
		
	}
	
	public void setAutoRemoveGeos(boolean isAutoRemoveGeos){
		this.isAutoRemoveGeos = isAutoRemoveGeos;
	}
	
	
	//=================================================
	//      Euclidian View
	//=================================================
	

	private class myEV extends EuclidianView {

		public myEV(EuclidianController ec, boolean[] showAxes, boolean showGrid) {
			super(ec, showAxes, showGrid);
			this.removeMouseListener(ec);
			this.removeMouseMotionListener(ec);
			this.removeMouseWheelListener(ec);
			this.setAxesCornerCoordsVisible(false);
		}
		
		// restore the old coord system after a resize
		// this will keep our plots centered and scaled to the new window 
		
		public void updateSize(){
			
			double xminTemp = getXmin();
			double xmaxTemp = getXmax();
			double yminTemp = getYmin();
			double ymaxTemp = getYmax();				
			super.updateSize();		
			setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
		}	
		
		
	}
	
	public EuclidianView getMyEuclidianView(){
		return ev;
	}
	
	public void attachView(){
		ev.attachView();
	}

	
	public void setEVParams(){
		
		ev.setShowAxis(EuclidianView.AXIS_Y, showYAxis, false);
		
		if(showArrows){
			ev.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		}else{
			ev.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_FULL);
		}
		
		
		// ensure that the axis labels are shown
		// by forcing a fixed pixel height below the x-axis
		
		if(forceXAxisBuffer){
			double pixelOffset = 30 * app.getSmallFont().getSize()/12.0;
			double pixelHeight = this.getHeight(); 
			yMinEV = - pixelOffset * yMaxEV / (pixelHeight + pixelOffset);
		}

		ev.setRealWorldCoordSystem(xMinEV, xMaxEV, yMinEV, yMaxEV);
	
		
	}
	
	public void setCoordSystem(double xMin, double xMax, double yMin, double yMax){
		xMinEV = xMin;
		xMaxEV = xMax;
		yMinEV = yMin;
		yMaxEV = yMax;
	}

	
	public void detachView(){
		kernel.detach(ev);
	}
	
	
	


	
	//=================================================
	//       Create GeoElement
	//=================================================

	/* 
	public void createGeoFromString(String text ){
		GeoElement tempGeo = null;
		createGeoFromString(tempGeo, text);
	}
	*/
	
	public GeoElement createGeoFromString(String text ){
		
		//Application.debug("geo creation text: " + text);
		
		if(isAutoRemoveGeos){
			removeGeos();
		}
			
		try {
				
			//boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();			
			//cons.setSuppressLabelCreation(true);
			
			GeoElement[] geos = kernel.getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(text, false);
			
			
			//geos[0].setAlgebraVisible(false);		
			//cons.setSuppressLabelCreation(oldSuppressLabelMode);
			
			geos[0].setLabel("plotGeo");
			
			// add the geo to our view and remove it from EV		
			geos[0].addView(ev);
			ev.add(geos[0]);
			geos[0].removeView(app.getEuclidianView());
			app.getEuclidianView().remove(geos[0]);
				
			// set visibility
			geos[0].setEuclidianVisible(true);
			geos[0].setAuxiliaryObject(true);
		
			plotGeoCollection.add(geos[0]);
			return geos[0];
				
				
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	

	
	//=================================================
	//       Plots
	//=================================================
	
	
	private void setXMinMax(GeoList dataList){
	
		double x;
		GeoNumeric geo;	
		xMinData = Double.MAX_VALUE;
		xMaxData = Double.MIN_VALUE;
		
		for (int i = 0; i < dataList.size(); ++i){
			geo = (GeoNumeric) ((GeoList)dataList).get(i);
			x = geo.getDouble();
			if(xMinData > x) xMinData = x;
			if(xMaxData < x) xMaxData = x;
		}	
		
		// Application.debug(xMinData + "  " + xMaxData);	
		
		/*
		String label = dataList.getLabel();	
		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Min[" + label + "]", false);		
		xMinData = nv.getDouble();
		
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Max[" + label + "]", false);		
		xMaxData = nv.getDouble();
		*/
		
	}
	
	
	
	public GeoElement createPDF(String expr, double xMin, double xMax, double yMin, double yMax){
		
		// set view parameters	
		xMinEV = xMin;
		xMaxEV = xMax;
		yMinEV = yMin;
		yMaxEV = yMax;
		showYAxis = false;
		forceXAxisBuffer = true;
		
		setEVParams();
		ev.addMouseListener(ec);
		ev.addMouseMotionListener(ec);
		ev.addMouseWheelListener(ec);
		
		// create function
		String text = expr;
		return createGeoFromString(text);
	
	}
	
	
	
	
	public void updateHistogram(GeoList dataList, int numClasses, boolean doCreate){
		
		setXMinMax(dataList);
		
		//Application.debug(xMinData + "  " + xMaxData);	
		//Application.debug(dataList.toDefinedValueString());	
		
		String label = dataList.getLabel();	
		String text = "";
		
		
		// Set view parameters
		double barWidth = (xMaxData - xMinData)/(numClasses - 1);  
		double freqMax = getFrequencyTableMax(dataList, barWidth);
		double buffer = .25*(xMaxData - xMinData);
		xMinEV = xMinData - buffer;  
		xMaxEV = xMaxData + buffer;
		yMinEV = -1.0;
		yMaxEV = 1.1 * freqMax;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();
		
		//System.out.println(yMaxEV + "," + freqMax + ", "  + barWidth + "," + xMinData);
		
		// Create histogram	
	//	if(doCreate){
			text = "BarChart[" + label + "," + Double.toString(barWidth) + "]";
			plotGeo = createGeoFromString(text);
	//	}
				
		//System.out.println(text);
	}
	

	public void updateBoxPlot(GeoList dataList, boolean doCreate){

		setXMinMax(dataList);
		String label = dataList.getLabel();	
		String text = "";
		
		// Set view parameters	
		double buffer = .25*(xMaxData - xMinData);
		xMinEV = xMinData - buffer;
		xMaxEV = xMaxData + buffer;
		yMinEV = -1.0;
		yMaxEV = 2;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();
		
		// create boxplot
		if(doCreate){
			text = "BoxPlot[1,0.5," + label + "]";
			createGeoFromString(text);
		}
				
	}
	
	
	public void updateDotPlot(GeoList dataList, boolean doCreate){

		setXMinMax(dataList);
		String label = dataList.getLabel();	
		String text = "";
	
		double buffer = .25*(xMaxData - xMinData);	
		
		
		// create dotplot text
		// note: the data list must be sorted low to high
		int maxCount = 1;
		text = "{";
		if(dataList.size()>0){
			text += "(" + ((GeoNumeric)dataList.get(0)).getDouble() + ", 1)";
			int k = 1;
			for (int i = 1; i < dataList.size(); ++i){
				System.out.println(((GeoNumeric)dataList.get(i)).getDouble()); 
				if(((GeoNumeric)dataList.get(i-1)).getDouble() == ((GeoNumeric)dataList.get(i)).getDouble() ) 
					++k;
				else
					k = 1;

				text += ",(" + ((GeoNumeric)dataList.get(i)).getDouble() + "," + k + ")";
				maxCount = maxCount < k ? k : maxCount;
			}
		}
		text += "}";	
		
		
/*
		// create dotplot text
		HashMap<Double,Integer> map = new HashMap<Double,Integer>();
		
		int maxCount = 1;
		double d;
		StringBuilder sb = new StringBuilder("{");
		if(dataList.size()>0){
			int k = 1;
			for (int i = 1; i < dataList.size(); ++i){
				d = ((GeoNumeric)dataList.get(i)).getDouble();
				if( map.containsKey(d)){
					map.put(d, map.get(d)+1);
				}else{
					map.put(d, 1);
				}
				sb.append( "(" + d + "," + map.get(d) + "),");
				maxCount = maxCount < map.get(d) ? map.get(d) : maxCount;
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");	
		map.clear();	

		//System.out.println(sb.toString());
*/
		 
		 
		// set view parameters		
		xMinEV = xMinData - buffer;
		xMaxEV = xMaxData + buffer;
		yMinEV = -1.0;
		yMaxEV = maxCount + 1;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();	
		
		// create geo
		//if(doCreate){
			plotGeo = createGeoFromString(text);
	//	}
			plotGeo.update();
	
		
	}
	
	
	
	
	//=================================================
	//       Frequency Table 
	//=================================================
	
	// get frequency table max
	// edited version of code in BarChart algo
	// TODO -- maybe we need a FrequencyTable[] command? ... then this is not needed
	
	private double getFrequencyTableMax(GeoList list1, double n){

		//Application.debug(list1.toDefinedValueString());
			
		double [] yval; // y value (= min) in interval 0 <= i < N
		double [] leftBorder; // leftBorder (x val) of interval 0 <= i < N
		GeoElement geo;	

		double mini = Double.MAX_VALUE;
		double maxi = Double.MIN_VALUE;
		int minIndex = -1;
		int maxIndex = -1;

		double step = n ;   //n.getDouble();
		int rawDataSize = list1.size();

		if (step < 0 || Kernel.isZero(step) || rawDataSize < 2)
		{
			return 0;
		}


		// find max and min
		for (int i = 0; i < rawDataSize; i++) {
			geo = list1.get(i);
			if (!geo.isGeoNumeric()) {
				return 0;
			}
			double val = ((GeoNumeric)geo).getDouble();

			if (val > maxi) {
				maxi = val;
				maxIndex = i;
			}
			if (val < mini) {
				mini = val;
				minIndex = i;
			}
		}

		if (maxi == mini || maxIndex == -1 || minIndex == -1) {
			return 0;
		}

		double totalWidth = maxi - mini;
		double noOfBars = totalWidth / n;    //n.getDouble();
		double gap = 0;

		int N = (int)noOfBars + 2;
		gap = ((N-1) * step - totalWidth) / 2.0;
		
		/*
		System.out.println("==========================");
		System.out.println("N " + N);
		System.out.println("n " + n);
		System.out.println("step " + step);
		System.out.println("gap " + gap);
		*/
		
		
		NumberValue a = (new GeoNumeric(cons,mini - gap));
		NumberValue b = (new GeoNumeric(cons,maxi + gap));

		yval = new double[N];
		leftBorder = new double[N];


		// fill in class boundaries
		//double width = (maxi-mini)/(double)(N-2);
		for (int i=0; i < N; i++) {
			leftBorder[i] = mini - gap + step * i;
		}
		
		
		// zero frequencies
		for (int i=0; i < N; i++) yval[i] = 0; 	

		// work out frequencies in each class
		double datum;

		for (int i=0; i < list1.size() ; i++) {
			geo = list1.get(i);
			if (geo.isGeoNumeric())	datum = ((GeoNumeric)geo).getDouble(); 
			else {  return 0; }

			// fudge to make the last boundary eg 10 <= x <= 20
			// all others are 10 <= x < 20
			double oldMaxBorder = leftBorder[N-1];
			leftBorder[N-1] += Math.abs(leftBorder[N-1] / 100000000);

			// check which class this datum is in
			for (int j=1; j < N; j++) {
				//System.out.println("left border " +leftBorder[j]);
				if (datum < leftBorder[j]) 
				{
					//System.out.println(datum + " " + j);
					yval[j-1]++;
					break;
				}
			}

			leftBorder[N-1] = oldMaxBorder;
		}

		double freqMax = 0.0;
		for(int k = 0; k < yval.length; ++k){
			//System.out.println(leftBorder[k] + "  : " + yval[k]);
				
			if(yval[k] > freqMax)
				freqMax = yval[k];
		}
		
		//System.out.println(freqMax);
		
		return freqMax;
		
	}


	
	
	//==================================================
	//       Component Listener  (for resizing our EV)
	//=================================================
	
	public void componentHidden(ComponentEvent arg0) {	
	}
	public void componentMoved(ComponentEvent arg0) {
	}
	public void componentResized(ComponentEvent arg0) {
		// make sure that we force a pixel buffer under the x-axis 
		setEVParams();
	}
	public void componentShown(ComponentEvent arg0) {
	}



}
