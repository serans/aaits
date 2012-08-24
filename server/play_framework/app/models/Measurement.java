package models;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.*;

import play.data.format.*;
import play.data.validation.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class Measurement extends Model {
	@Id
	public Long id;
	
	@Constraints.Required
	public Date timestamp;
	@Constraints.Required
	public Float value;
	
	public Float rawValue;
	
	public String toCSV(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
		return sdf.format(timestamp)+";"+String.valueOf(value)+";"+String.valueOf(rawValue);
	}
	
	public static Finder<Long,Measurement> find = new Finder(Long.class, Measurement.class);
	
	public static List<Measurement> getBySensorConf(Long sensorConfigId){
		return find.where().eq("sensor_config_id", sensorConfigId).findList();
	}
	
	static List<Measurement> getBySensorConf(Long sensorConfigId, Date startDate){
		find.where().eq("sensor_config_id", sensorConfigId).ge("timestamp", startDate).findList();
		return null;
	}
	
	public static List<Measurement> getBySensorConf(Long sensorConfigId, Date startDate, Date endDate){
		find.where().eq("sensor_config_id", sensorConfigId).ge("timestamp", startDate).findList();
		return null;
	}
}