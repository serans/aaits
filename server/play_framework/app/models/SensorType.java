package models;

import java.util.*;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import play.data.validation.Constraints.*;

import javax.persistence.*;


@Entity
public class SensorType extends Model {
	@Id
	public String ref;
	public String description;
	
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy="type" )
	public List<SensorConfig> sensorConfigs;
	
	public static Finder<String, SensorType> find = new Finder(String.class, SensorType.class);
	
	public List<SensorConfig> viewConfigurations() {
		return viewConfigurations(false);
	}
	
	public List<SensorConfig> viewSamples() {
		return viewConfigurations(true);
	}
	
	public List<SensorConfig> viewConfigurations(boolean isSample) {
		return SensorConfig.find.where().eq("ref", this.ref).eq("isSample", isSample).findList();
	}
	
}
