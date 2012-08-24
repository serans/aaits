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
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.sun.java.swing.plaf.gtk.GTKConstants.Orientation;

public class Application extends Controller {

	public static Result index() {
		return ok(views.html.index.render());
	}
	

	
	public static Result uploadDataFile() {
		MultipartFormData body = request().body().asMultipartFormData();
		File f = body.getFiles().get(0).getFile();
		YamlInterpreter yi = new YamlInterpreter();
		try {
			NodeConfig nc = yi.readDataFile(f.getAbsolutePath());
			NodeConfig ncDB = NodeConfig.findConfiguration(nc);
			if(ncDB != null) {
				ncDB.save();
			} else {
				nc.save();
			}
			//return ok(nc.toYaml());
			//nc.save();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return redirect(routes.Nodes.listNodeConfig());
	}
}