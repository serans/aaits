package controllers;

import play.*;
import play.mvc.*;
import play.api.templates.Html;
import play.data.*;
import models.*;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;


import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import utils.filters.*;

public class Charts extends Controller {
	
	/**
	 * Stores an static map which associates request parameters with filter classes
	 */
	private static final Map<String, String> filterClassIndex;
	static {
		Map<String, String> fi = new HashMap<String,String>();
		fi.put("SMA", "utils.filters.SMAFilter");
		fi.put("CA", "utils.filters.CAFilter");
		fi.put("FHIDE", "");
		filterClassIndex = Collections.unmodifiableMap(fi);
	}
	
	/**
	 * Renders a plot window for the specified sensorConfig
	 * 
	 * @param sensorConfigId
	 * @return Html with the plot window
	 */
	public static String plotWindow( Long sensorConfigId) {
		Long startTimestamp = null;
		Long endTimestamp = null;
		
		SensorConfig sc = SensorConfig.find.byId(sensorConfigId);
		
		if(sc.measurements.size()>0) {
			startTimestamp = sc.getFirstMeasurementDate().getTime();  
			endTimestamp   = sc.getLastMeasurementDate().getTime();
		}
		
		return views.html.plot.simple_window.render(sensorConfigId, startTimestamp, endTimestamp).toString();
	}
	/**
	 * Renders a plot of the data from the specified sensorConfigId.
	 * 
	 * It automatically adjusts the x scale units to the most appropriate one
	 *  
	 * @param sensorConfigId
	 * @param startTimestampS String representing the timestamp of the first date to be shown 
	 * @param endTimestampS String representing the timestamp of the last date to be shown
	 * @param filterList list of filters to apply in the format "filter1,filter2:param1:param2"
	 * @param width of the image
	 * @param height of the image
	 * @return PNG image
	 * @throws IOException 
	 */
	public static Result viewPlot( Long sensorConfigId, String startTimestampS, String endTimestampS, 
								   String filterList, Integer width, Integer height) throws IOException {
		boolean showData = true;
		if(width==null) width=800;
		if(height==null) height=500;
		
		Color[] plotColors = {Color.DARK_GRAY, Color.blue, Color.cyan	};
		
		Long startTimestamp = null; 
		Long endTimestamp   = null;
		
		SensorConfig sc = SensorConfig.find.byId(sensorConfigId);
		List<Filter> filters = new ArrayList<Filter>();
		
		// Create filters
		if(filterList!=null) for(String c: filterList.split(",")) {
			String[] params = c.split(":");
			if (filterClassIndex.containsKey(params[0])) {
				if(params[0].equals("FHIDE")) {
					showData=false;
				} else
				try {
					Class<?> clase = Class.forName(filterClassIndex.get(params[0]));
					Filter filter = (Filter) clase.newInstance();
					
					for(int i=1; i<params.length; i++) {
						filter.setParam(Integer.parseInt(params[i]));
					}
					filters.add(filter);
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		}
		
		List<Measurement> mList;
		
		//Find start&end dates and get data from Database
		if(startTimestampS!=null) {
			startTimestamp = Long.parseLong(startTimestampS);
			if(endTimestampS == null) endTimestamp = new Date().getTime();
			else endTimestamp = Long.parseLong(endTimestampS);
			mList = Measurement.find.where()
						.eq("sensor_config_id", sensorConfigId)
						.gt("timestamp", new Timestamp(startTimestamp) )
						.lt("timestamp", new Timestamp(endTimestamp) ).findList();
		} else {
			mList = Measurement.getBySensorConf(sensorConfigId);
		}
		
		//initiate filters (if any)
		XYSeries dataSeries = new XYSeries(sc.name);
		Map<Filter,XYSeries> filterSeries = new HashMap<Filter,XYSeries>();
		for( Filter filter: filters) {
			filterSeries.put(filter, new XYSeries(filter.getName()));
		}
		
		//put data into dataseries
		for( Measurement m : mList) {
			if(showData){
				dataSeries.add(m.timestamp.getTime(),m.value);
			}
			
			for(Filter filter: filters) {
				Float val = filter.getNextValue(m.timestamp.getTime(),m.value);
				if(val!=null) filterSeries.get(filter).add(m.timestamp.getTime(),val);
			}
		}
		
		//add dataseries to collection
		XYSeriesCollection seriesCollect = new XYSeriesCollection();

		//for(Map.Entry<Filter, XYSeries> entry: filterSeries.entrySet()) {
		for ( Filter f: filters ) {
			seriesCollect.addSeries(filterSeries.get(f));
		}
		if(showData) seriesCollect.addSeries(dataSeries);
		
		//create chart
		JFreeChart chart = ChartFactory.createXYLineChart(
				sc.getScreenName(), 
				"time", 
				sc.units, 
				seriesCollect, PlotOrientation.VERTICAL, (filters.size()>0 || !showData), false, false);
		
		chart.getXYPlot().setRangeGridlinePaint(Color.DARK_GRAY);
		chart.getXYPlot().setDomainGridlinePaint(Color.DARK_GRAY);
		
		//Assign colors, offset makes sure that fist color is not assigned to a filter if data is not shown
		for( int i=0; i<seriesCollect.getSeriesCount(); i++) {
			int offset=0;
			if(!showData) offset=1;
			chart.getXYPlot().getRenderer().setSeriesPaint(i, plotColors[seriesCollect.getSeriesCount()-i-1+offset] );
		}
		
		if(startTimestamp != null && endTimestamp != null) {
			chart.getXYPlot().setDomainAxis(calculateTimeAxis(startTimestamp, endTimestamp));
		} else {
			chart.getXYPlot().setDomainAxis(calculateTimeAxis(mList.get(0).timestamp.getTime(),mList.get(mList.size()-1).timestamp.getTime()));
		}
		
	    return ok( renderChart(chart,width,height) ).as("image/png");
	}
	
	/**
	 * Calculates the appropriate axis units depending on the start and end date
	 * 
	 * @param start_timestamp
	 * @param end_timestamp
	 * @return
	 */
	private static DateAxis calculateTimeAxis(Long start_timestamp, Long end_timestamp){
		DateAxis axis = new DateAxis("time");
		DateFormat formatter;
		axis.setLowerMargin(0);
		axis.setUpperMargin(0);
		axis.setAutoRange(false);
		axis.setLowerBound(start_timestamp);
		axis.setUpperBound(end_timestamp);
		
		Long timeRange = end_timestamp-start_timestamp;
		
		if		(timeRange< 60000) 		 {formatter = new SimpleDateFormat("ss.S");axis.setLabel("time (seconds.milliseconds)");}
		else if (timeRange<3600000)		 {formatter = new SimpleDateFormat("mm''ss''''");axis.setLabel("time (minutes'seconds'')");}
		else if (timeRange<48*3600000)	 {formatter = new SimpleDateFormat("HH:mm");axis.setLabel("hour");}
		else if (timeRange<30*48*3600000){formatter = new SimpleDateFormat("dd'd' hh'h'");axis.setLabel("day & hour");}
		else 							 {formatter = new SimpleDateFormat("yyyy-MM-dd");axis.setLabel("date");};
		
		axis.setDateFormatOverride(formatter);
		return axis;
	}
	
	/**
	 * Renders chart as a PNG image with specified width and height
	 * 
	 * @param chart
	 * @param width
	 * @param height
	 * @return InputStream with chart rendered as a PNG image
	 * @throws IOException
	 */
	private static InputStream renderChart(JFreeChart chart, int width, int height) throws IOException{
		// Render Image
		BufferedImage bf = chart.createBufferedImage(width, height);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(bf, "png", baos);
	    baos.flush();
	    byte[] imageInByte = baos.toByteArray();
	    baos.close();
	    InputStream is = new ByteArrayInputStream(imageInByte);
	    return is;
	}
}
