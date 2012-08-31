package models;

import java.util.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;
import javax.persistence.*;

@Entity
public class SensorConfig extends Model {
	@Id
	public Long id;

	public Date creationDate;
	public int internal_id;
	public String name;
	public Integer pin;
	public int steps;

	@Constraints.Required
	public String className;
	
	public boolean isSample;
	
	@Constraints.Required
	public String units;
	public String description;
	
	@ManyToOne
	public NodeConfig nodeConfig;

	@OneToMany(cascade = { CascadeType.PERSIST })
	public List<Measurement> measurements;
	
	@ManyToOne
	@JoinColumn(name="ref")
	public SensorType type;
	
	//@ManyToOne
	//@JoinColumn(name="ref")
	//public String ref;

	public static Finder<Long, SensorConfig> find = new Finder(Long.class,
			SensorConfig.class);

	public static void deleteById(Long id) {
		find.ref(id).delete();
	}

	public void addMeasurement(Measurement m) {
		measurements.add(m);
	}

	public float getSamplingPeriod() {
		return (nodeConfig.samplingPeriod * steps / 1000);
	}

	public String toYaml() {
		StringBuilder sb = new StringBuilder();
		sb.append("  -id:" + internal_id + "\n");
		sb.append("   class:" + className + "\n");
		if (name != null)
			sb.append("   name:" + name + "\n");
		if (pin != null)
			sb.append("   pin:" + pin + "\n");
		if (type!=null)
			sb.append("   ref:"+type.ref+"\n");
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
/*		if (!ref.equals(sc.ref))
			return false;
*/
		return true;
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

	public Date getFirstMeasurementDate() {
		return getResultDateByOrder("ASC");
	}
	
	public Date getLastMeasurementDate() {
		return getResultDateByOrder("DESC");
	}
	
	private Date getResultDateByOrder( String order) {
		List<Measurement> m;
		// calculate dates for plot & navigation buttons
		m = Measurement.find
			.select("timestamp")
			.orderBy("timestamp "+order)
			.where()
				.eq("sensor_config_id", id)
			.findPagingList(1)
			.getAsList();
		if (m != null) return m.get(0).timestamp;
		
		return null;
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
}
