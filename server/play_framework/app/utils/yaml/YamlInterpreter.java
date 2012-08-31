package utils.yaml;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import models.*;

public class YamlInterpreter {
	
	NodeConfig nc;
	
	Date StartTime;
	
	int state;
	
	final int YML_ROOT=0;
	final int YML_SENSOR=1;
	final int YML_PARAM=2;
	final int YML_INIT=3;
	final int YML_ACTION=4;
	final int CSV_DATA=5;
	
	public NodeConfig readDataFile( String url) throws FileNotFoundException, ParseException {
		reset();
		
		FileInputStream fstream = new FileInputStream(url);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		try {
			while ((strLine = br.readLine()) != null)   {
				interpretateLine(strLine);
			}
			in.close();
		} catch (IOException ioe) {
			return null;
		}
		
	    return nc;
	}
	
	private void reset(){
		nc = new NodeConfig();
		state = YML_ROOT;
		YamlLine.startNewFile();
	}
	
	
	
	private void interpretateLine(String line) throws ParseException {
		
		YamlLine yl = YamlLine.readNextLine(line);
		
		if(yl.getLevel()==0 && state!=CSV_DATA) state=YML_ROOT;
	    if(yl.getLevel()==1) if(state==YML_PARAM || state==YML_INIT || state==YML_ACTION ) state=YML_SENSOR;
	    
	    switch(state) {
	        case YML_ROOT:
	            readRoot(yl);
	            if ( yl.getKey().equals("sensors") )   { state = YML_SENSOR;}
	            if ( yl.getKey().equals("data") )   { state = CSV_DATA;}
	            break;
	              
	        case YML_SENSOR:
	            readSensor(yl);
	            if( yl.getKey().equals("trans_param") ) { state = YML_PARAM; }
	            break;
	            
	        case YML_PARAM:
	        	readParams(yl);
	        	break;
	              
	        case CSV_DATA:
	       	    readCSVData(line);
	      }
	}
	
	private void readParams (YamlLine yl) {
		//System.out.println(yl);
	}
	
	private void readCSVData(String line) {
		
		StringTokenizer csvLine = new StringTokenizer(line, ";");
		if(csvLine.countTokens()!=3) return;
		
		Measurement m = new Measurement();
		
		//first: millis since start
		Calendar c1 = Calendar.getInstance();
		
		c1.setTimeInMillis(StartTime.getTime()+Integer.parseInt(csvLine.nextToken()));
		m.timestamp = c1.getTime();
		
		//second: id
		(nc.getSensorConfigbyInternalId(Integer.parseInt(csvLine.nextToken()))).measurements.add(m);
		
		//third: measured value
		m.value = Float.parseFloat(csvLine.nextToken());
	}
	
	private void readRoot(YamlLine yl) throws ParseException {
		if ( yl.getKey().equals("device_name") ) {
	        nc.name = yl.getValue();
	    }else if( yl.getKey().equals("sampling_period") ) { 
	    	nc.samplingPeriod = Integer.parseInt(yl.getValue());
	    }else if( yl.getKey().equals("description") ) { 
	    	nc.description = yl.getValue();
	    }else if( yl.getKey().equals("device_uid") ) { 
	    	nc.deviceUid = Long.parseLong(yl.getValue());
	    }else if( yl.getKey().equals("start_time")) {
	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	this.StartTime = df.parse(yl.getValue());
	    }
	}
	
	private void readSensor(YamlLine yl) {
		if(yl.isNew()) {
			nc.addSensorConfig( new SensorConfig());
		}
		
		SensorConfig sc = nc.sensorConfigs.get(nc.sensorConfigs.size()-1);
		
		if( yl.getKey().equals("id") ) {
	        sc.internal_id = Integer.parseInt(yl.getValue());
	    }else if( yl.getKey().equals("class") ) { 
	    	sc.className = yl.getValue();
	    }else if( yl.getKey().equals("name") ) { 
	    	sc.name = yl.getValue();
	    }else if( yl.getKey().equals("units") ) { 
	    	sc.units = yl.getValue();
	    }else if( yl.getKey().equals("pin") ) { 
	    	sc.pin = Integer.parseInt(yl.getValue());
	    }else if( yl.getKey().equals("steps") ) { 
	    	sc.steps = Integer.parseInt(yl.getValue());
	    }else if( yl.getKey().equals("ref") ) {
	    	sc.type = new SensorType();
	    	sc.type.ref = yl.getValue();
	    }
	}
}
