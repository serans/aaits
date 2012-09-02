package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.data.*;
import models.*;

import java.awt.Color;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;

import utils.yaml.YamlInterpreter;
import views.html.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import com.sun.java.swing.plaf.gtk.GTKConstants.Orientation;

public class SensorConfigs extends Controller {
	
	/**
	 * Performs the modifications to the database by either adding or
	 * modifying a SensorConfig, according to the received form
	 * @return
	 */
	public static Result modify(){
		Form<SensorConfig> scForm = form(SensorConfig.class).bindFromRequest();
		
		if(scForm.hasErrors()) return badRequest();
		
		if(scForm.get().id != null ) {
			scForm.get().update();
		} else {
			scForm.get().save();
		}
		
		System.out.println(scForm.get().type.ref);
		return redirect(routes.SensorTypes.view(scForm.get().type.ref));
	}
	
	/**
	 * Adds a new sensor configuration to the database
	 * @return
	 */
	public static Result add(String ref) {
		Form<SensorConfig> scForm;
		scForm = form(SensorConfig.class);
		return ok(views.html.sensor.edit_config.render(null, ref, scForm));
	}
	
	/**
	 * Edits a sensor configuration
	 * @return
	 */
	public static Result edit(Long id){
		Form<SensorConfig> stForm;
		if(id!=null)
			stForm = form(SensorConfig.class).fill(SensorConfig.find.byId(id));
		else
			stForm = form(SensorConfig.class);
		
		return ok(views.html.sensor.edit_config.render(id, null,  stForm));
	}
	
	/**
	 * Shows details of a sensor configuration, including plots
	 * @param id of the sensor config
	 * @return HTML code
	 */
	public static Result view( Long sensorConfigId ) {
		SensorConfig sc 	= SensorConfig.find.byId(sensorConfigId);
		return ok(views.html.sensor.view_config.render(sc, Charts.plotWindow(sc.id) ));
	}
	
	/**
	 * Returns a CSV file 
	 * @param sensorConfigId
	 * @return
	 */
	public static Result getCSV(Long sensorConfigId, String startDate, String endDate) {
		Long startTimestamp;
		Long endTimestamp; 
		
		if(startDate==null || endDate==null) {
			SensorConfig sc = SensorConfig.find.byId(sensorConfigId);
			startTimestamp  = sc.getFirstMeasurementDate().getTime();  
			endTimestamp    = sc.getLastMeasurementDate().getTime();
		} else {
			startTimestamp = Long.parseLong(startDate);
			endTimestamp = Long.parseLong(endDate);
		}
		
		List<Measurement> mList = Measurement.find
				.orderBy("timestamp")
				.where()
					.eq("sensor_config_id",sensorConfigId)
					.ge("timestamp", new Timestamp(startTimestamp) )
					.le("timestamp", new Timestamp(endTimestamp) )
				.findList();
		
		Iterator<Measurement> mIter = mList.iterator();
		StringBuilder csv = new StringBuilder();
		while(mIter.hasNext()){
			Measurement m = mIter.next();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
			csv.append(sdf.format(m.timestamp));
			csv.append(";");
			csv.append(String.valueOf(m.value));
			csv.append(";");
			if(m.rawValue!=null) csv.append(String.valueOf(m.rawValue));
			csv.append("\n");
		}
		return ok(csv.toString());
	}
	
	/**
	 * Returns the sensorConfig with the specified id in YAML
	 * @param id
	 */
	public static Result getYaml( Long id ) {
		return ok("sensors:\n"+SensorConfig.find.byId(id).toYaml());
	}
	
}
