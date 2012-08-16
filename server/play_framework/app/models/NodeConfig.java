package models;

import java.util.*;

import play.db.ebean.*;
import play.data.validation.Constraints.*;

import javax.persistence.*;

@Entity
public class NodeConfig extends Model {
    
  @Id
  public Long id;
  public int deviceUid;
  public Date creationDate;
  public String name;
  public int samplingPeriod;
  public String description;
  
  //@ManyToOne
  //public Sensor sensor;
  
  @OneToMany(mappedBy = "nodeConfig", cascade = {CascadeType.ALL})
  public List<SensorConfig> sensorConfigs;
  
  public static Finder<Long,NodeConfig> find = new Finder(Long.class, NodeConfig.class);
  
  public void addSensorConfig( SensorConfig sc ) {
	  sensorConfigs.add(sc);
  }
  
  public SensorConfig getSensorConfigbyInternalId( int id) {
	  SensorConfig sc;
	  Iterator<SensorConfig> it = sensorConfigs.iterator();
	  while(it.hasNext()) {
		   sc = it.next();
		   if(sc.internal_id==id) {
			   return sc;
		   }
	  }
	  return null;
  }
  
  public String toYaml(){
	  StringBuilder sb = new StringBuilder();
	  sb.append("id:"+deviceUid+"\n");
	  sb.append("name:"+name+"\n");
	  sb.append("description:"+description+"\n");
	  sb.append("sampling_period:"+samplingPeriod+"\n");
	  if(sensorConfigs.size()>0) {
		  sb.append("sensors:\n");
		  Iterator<SensorConfig> it = sensorConfigs.iterator();
		  while(it.hasNext()){
			  sb.append(it.next().toYaml());
		  }
	  }
	  return sb.toString();
  }
  
  public static List<NodeConfig> findByDeviceUid( int uid ) {
	  return find.where().ieq("deviceUid", String.valueOf(uid)).findList();
  }
  
  public static void deleteById(Long id) {
    find.ref(id).delete();
  }
}