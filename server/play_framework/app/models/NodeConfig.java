package models;

import java.util.*;

import play.db.ebean.*;
import play.data.validation.Constraints.*;

import javax.persistence.*;

@Entity
public class NodeConfig extends Model {
    
  @Id
  public Long id;
  public Long deviceUid;
  public Date creationDate;
  public String name;
  public int samplingPeriod;
  public String description;
  
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
  
  /**
   * Converts the node configuration to Yaml, including the config. of its sensors
   * @return Yaml string
   */
  public String toYaml(){
	  StringBuilder sb = new StringBuilder();
	  sb.append("id:"+deviceUid+"\n");
	  sb.append("name:"+name+"\n");
	  if(description!=null) sb.append("description:"+description+"\n");
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
  
  @Override
  /**
   * Creates a "Node" if it doesn't already exist
   */
  public void save(){
	  Node n = Node.find.byId(this.deviceUid);
	  if(n==null) {
		  n = new Node();
		  n.id = deviceUid;
		  n.name = this.name;
		  n.description = this.description;
		  n.save();
	  }
	  n.nodeConfigs.add(this);
	  n.save();
  }
  
  public static List<NodeConfig> findByDeviceUid( int uid ) {
	  return find.where().ieq("deviceUid", String.valueOf(uid)).findList();
  }
  
  /**
   * Returns a configuration equivalent to "nc" if it already exists on the DB.
   * Useful for determining whether or not "nc" refers to a configuration already
   * existing on the database, so that the data from both configurations
   * can be merged.
   * @param nc
   */
  public static NodeConfig findConfiguration ( NodeConfig nc) {
	  List<NodeConfig> dbConfs = find.where()
			      .eq("device_uid", nc.deviceUid)
	  			  .eq("samplingPeriod", nc.samplingPeriod)
	  			  .eq("name", nc.name).findList();
	  
	  if(dbConfs.size()==0) return null;
	  
	  //for each config on the DB...
	  for ( NodeConfig dbConf: dbConfs) {
		  if(dbConf.sensorConfigs.size()!=nc.sensorConfigs.size()) continue;
		  
		  boolean hasSameSensors = true;
		  //check that each sensor...
		  for( SensorConfig dbSensorConf: dbConf.sensorConfigs) {
	          
			  //...is also present on nc 
			  boolean foundSensor = false;
			  for( SensorConfig ncSensorConf: nc.sensorConfigs) {
				  if( ncSensorConf.equals(dbSensorConf) ) {
					  foundSensor = true;
					  dbSensorConf.measurements.addAll(ncSensorConf.measurements);
					  break;
				  }
			  }
			  //otherwise no need to keep looking
			  if(foundSensor==false) {
				  hasSameSensors=false;
				  break;
			  }
		  }
		  if(hasSameSensors == true) {
			  return dbConf;
		  }
	  }
	  return null;
  }
  
}