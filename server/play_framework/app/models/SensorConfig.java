package models;

import java.util.*;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import play.data.validation.Constraints.*;

import javax.persistence.*;

@Entity
public class SensorConfig extends Model {
	  @Id
	  public Long id;
	  
	  public Date creationDate;
	  
	  //oneToMany
	  public int sensorTypeId;
	  
	  public int internal_id;
	  public String name;
	  public int pin;
	  public int steps;
	  public String className;
	  public boolean isSample;
	  public String units;

	  @ManyToOne
	  public NodeConfig nodeConfig;
	  
	  @OneToMany(cascade={CascadeType.PERSIST})
	  public List<Measurement> measurements;
	  
	  public static Finder<Long,SensorConfig> find = new Finder(Long.class, SensorConfig.class);
	  
	  public void addMeasurement( Measurement m) {
		  measurements.add(m);
	  }
	  
	  public String toYaml(){
		  StringBuilder sb = new StringBuilder();
		  sb.append("  -id:"+internal_id+"\n");
		  sb.append("   class:"+className+"\n");
		  sb.append("   name:"+name+"\n");
		  sb.append("   pin:"+pin+"\n");
		  sb.append("   units:"+units+"\n");
		  sb.append("   steps:"+steps+"\n");
		  return sb.toString();
	  }
	  
	  public static void deleteById(Long id) {
	    find.ref(id).delete();
	  }
}
