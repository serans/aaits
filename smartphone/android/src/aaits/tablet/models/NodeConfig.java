package aaits.tablet.models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NodeConfig {

	private Long id;
	private Long deviceUid;
	private Date creationDate;
	private String name;
	private int samplingPeriod;
	private String description;

	private List<SensorConfig> sensorConfigs;
	
	public NodeConfig() {
		sensorConfigs = new LinkedList<SensorConfig>();
	}
	
	public void addSensorConfig( SensorConfig sc ) { 
		sensorConfigs.add(sc);
		sc.setNodeConfig(this);
	}

	/**
	 * Converts the node configuration to Yaml, including the config. of its
	 * sensors
	 * 
	 * @return Yaml string
	 */
	public String toYaml() {
		StringBuilder sb = new StringBuilder();
		sb.append("device_name:" + name + "\n");
		if (description != null)
			sb.append("description:" + description + "\n");
		sb.append("sampling_period:" + samplingPeriod + "\n");
		
		if(sensorConfigs.size()>0) { sb.append("sensors:\n");
		  Iterator<SensorConfig> it = sensorConfigs.iterator();
		  while(it.hasNext()){ sb.append(it.next().toYaml()); } 
		}
		 
		return sb.toString();
	}
	
	public SensorConfig getSensorConfigbyInternalId( int id) {
		  SensorConfig sc;
		  Iterator<SensorConfig> it = sensorConfigs.iterator();
		  while(it.hasNext()) {
			   sc = it.next();
			   if(sc.getInternal_id()==id) {
				   return sc;
			   }
		  }
		  return null;
	 }
	
	public SensorConfig findSensorByListName(String listName){
		for( SensorConfig sc: sensorConfigs ) {
			if(sc.getListName().equals(listName)) return sc;
		}
		return null;
	}

	/* GETTERs / SETTERs */
	
	public List<SensorConfig> getSensorConfigs() {
		return sensorConfigs;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDeviceUid() {
		return deviceUid;
	}

	public void setDeviceUid(Long deviceUid) {
		this.deviceUid = deviceUid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSamplingPeriod() {
		return samplingPeriod;
	}

	public void setSamplingPeriod(int samplingPeriod) {
		this.samplingPeriod = samplingPeriod;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}