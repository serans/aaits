package aaits.tablet.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Measurement {

	public Long id;
	public Date timestamp;
	public Float value;
	public Float rawValue;
	
	public String toCSV(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
		return sdf.format(timestamp)+";"+String.valueOf(value)+";"+String.valueOf(rawValue);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Float getRawValue() {
		return rawValue;
	}

	public void setRawValue(Float rawValue) {
		this.rawValue = rawValue;
	}
	
	/* GETTERs / SETTERs */
	
}