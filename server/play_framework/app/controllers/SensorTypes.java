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

public class SensorTypes extends Controller {
	
	public static Result view( String ref) {
		return ok(views.html.sensor.view_type.render( SensorType.find.byId(ref) ));
	}
	
	public static Result modify(){
		Form<SensorType> stForm = form(SensorType.class).bindFromRequest();
		
		if(stForm.hasErrors()) return badRequest();
		
		if(SensorType.find.byId(stForm.get().ref)!=null) {
			stForm.get().update();
		} else {
			stForm.get().save();
		}
		return redirect(routes.SensorTypes.list());
	}
	
	public static Result add() {
		return edit(null);
	}
	
	public static Result edit(String id){
		Form<SensorType> stForm;
		if(id!=null)
			stForm = form(SensorType.class).fill(SensorType.find.byId(id));
		else
			stForm = form(SensorType.class);
		
		return ok(views.html.sensor.edit_type.render(id, stForm));
	}
	
	public static Result list(){
		List<SensorType> typesList = SensorType.find.all();
		return ok(views.html.sensor.list_types.render(typesList));
	}
		
}
