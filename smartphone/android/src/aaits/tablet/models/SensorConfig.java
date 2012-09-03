package aaits.tablet.models;

import java.util.*;

public class SensorConfig {
	
	private Long id;

	private Date creationDate;
	private int internal_id;
	private String name;
	private Integer pin;
	private int steps;
	private String className;
	private boolean isSample;
	private String units;
	private String description;
	private String ref;
	private NodeConfig nodeConfig;
	private List<Measurement> measurements;
	
	public String toYaml() {
		StringBuilder sb = new StringBuilder();
		sb.append("  -id:" + internal_id + "\n");
		sb.append("   class:" + className + "\n");
		if (name != null)
			sb.append("   name:" + name + "\n");
		if (pin != null)
			sb.append("   pin:" + pin + "\n");
		if (ref!=null)
			sb.append("   ref:"+ref+"\n");
		sb.append("   units:" + units + "\n");
		sb.append("   steps:" + steps + "\n");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass() != this.getClass())
			return false;
		SensorConfig sc = (SensorConfig) o;

		if (internal_id != sc.internal_id)
			return false;
		if (name == null) {
			if (sc.name != null)
				return false;
		} else {
			if (!name.equals(sc.name))
				return false;
		}
		if (pin != sc.pin)
			return false;
		if (steps != sc.steps)
			return false;
		if (!className.equals(sc.className))
			return false;
		if (!units.equals(sc.units))
			return false;
		return true;
	}

		
	/**
	 * @return a map containing all the possible classes for a SensorConfig
	 */
	public static Map<String,String> classes() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        options.put("Analog", "Analogic Sensor");
        options.put("Memory", "Memory Sensor");
        options.put("OneWire", "1-Wire Sensor");
        options.put("Boolean", "Boolean (on/off) Sensor");
        return options;
    }
	
	/* Custom Get - Set */
	public void addMeasurement(Measurement m) {
		measurements.add(m);
	}
	
	public List<Measurement> getMeasurements() { return measurements; }

	public float getSamplingPeriod() {
		return (nodeConfig.getSamplingPeriod() * steps / 1000);
	}
	
	/**
	 * As "name" can be null, this functions returns a user-friendly name for
	 * the SensorConfig
	 * 
	 * @return user friendly name
	 */
	public String getScreenName() {
		if (name == null)
			return className;
		else
			return name;
	}
	
	/**
	 * Returns a name to be shown on lists 
	 */
	public String getListName() {
		if (name == null) {
			return internal_id+":"+className+"("+ref+")";
		} else {
			return internal_id+":"+name+"("+ref+")";
		}
	}
	
	/* GETTERs/SETTERs */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getInternal_id() {
		return internal_id;
	}

	public void setInternal_id(int internal_id) {
		this.internal_id = internal_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPin() {
		return pin;
	}

	public void setPin(Integer pin) {
		this.pin = pin;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isSample() {
		return isSample;
	}

	public void setSample(boolean isSample) {
		this.isSample = isSample;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public NodeConfig getNodeConfig() {
		return nodeConfig;
	}

	public void setNodeConfig(NodeConfig nodeConfig) {
		this.nodeConfig = nodeConfig;
	}
	
	
}
