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
import java.text.ParseException;
import java.util.*;

import javax.imageio.ImageIO;

import utils.yaml.YamlInterpreter;
import views.html.*;

import org.jfree.chart.*;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.*;
import org.jfree.data.general.DefaultPieDataset;

public class Application extends Controller {

	public static Result index() {
		List<NodeConfig> nc = NodeConfig.find.all();
		return ok(views.html.index.render(nc));
	}
	
	public static Result viewNodeConfig( Long id ) {
		return TODO;
	}
	
	public static Result merda () throws IOException {
	       DefaultPieDataset data = new DefaultPieDataset();
	       data.setValue("Nata", 250);
	       data.setValue("Queixo", 500);
	       data.setValue("Limón", 10);
	       
	       JFreeChart chart = ChartFactory.createPieChart(
	               "Exemplo de tarta",
	               data,
	               true, // legend?
	               true, // tooltips?
	               false // URLs?
	       );
	       
	       BufferedImage bf = chart.createBufferedImage(500, 500);
	       ByteArrayOutputStream baos = new ByteArrayOutputStream();
	       ImageIO.write(bf, "png", baos);
	       baos.flush();
	       byte[] imageInByte = baos.toByteArray();
	       baos.close();
	       InputStream is = new ByteArrayInputStream(imageInByte);
	       return ok(is).as("image/png");
	}

	
	public static Result uploadDataFile() {
		MultipartFormData body = request().body().asMultipartFormData();
		File f = body.getFiles().get(0).getFile();
		YamlInterpreter yi = new YamlInterpreter();
		try {
			NodeConfig nc = yi.readDataFile(f.getAbsolutePath());
			nc.save();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		f.renameTo(new File(Play.application().path()+"test.txt"));
		System.out.println(f.getAbsolutePath());
		*/
		return redirect(routes.Application.index());
	}
}