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
	  public Integer pin;
	  public int steps;
	  public String className;
	  public boolean isSample;
	  public String units;

	  @ManyToOne
	  public NodeConfig nodeConfig;
	  
	  @OneToMany(cascade={CascadeType.PERSIST})
	  public List<Measurement> measurements;
	  
	  public static Finder<Long,SensorConfig> find = new Finder(Long.class, SensorConfig.class);
	  
	  public static void deleteById(Long id) {
		find.ref(id).delete();
	  }
	  
	  public void addMeasurement( Measurement m) {
		  measurements.add(m);
	  }
	  
	  public float getSamplingPeriod(){
		  return (nodeConfig.samplingPeriod *  steps / 1000);
	  }
	  
	  public String toYaml(){
		  StringBuilder sb = new StringBuilder();
		  sb.append("  -id:"+internal_id+"\n");
		  sb.append("   class:"+className+"\n");
		  if(name!=null) sb.append("   name:"+name+"\n");
		  if(pin!=0)     sb.append("   pin:"+pin+"\n");
		  sb.append("   units:"+units+"\n");
		  sb.append("   steps:"+steps+"\n");
		  return sb.toString();
	  }
	  
	  @Override
	  public boolean equals(Object o) {
		  if(o.getClass()!=this.getClass()) return false;
		  SensorConfig sc = (SensorConfig) o;
		  
		  if(sensorTypeId!=sc.sensorTypeId) return false;
		  if(internal_id!=sc.internal_id) return false;
		  if(name==null) {
			  if(sc.name!=null) return false;
		  } else {
			  if(!name.equals(sc.name)) return false;
		  }
		  if(pin!=sc.pin) return false;
		  if(steps!=sc.steps) return false;
		  if(!className.equals(sc.className)) return false;
	      if(!units.equals(sc.units)) return false;
		  
		  return true;
	  }
	  
	  /**
	   * As "name" can be null, this functions returns a user-friendly name
	   * for the SensorConfig
	   * @return user friendly name
	   */
	  public String getScreenName() {
		  if(name==null) return className;
		  else return name;
	  }
}
