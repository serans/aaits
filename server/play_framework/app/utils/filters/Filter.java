package utils.filters;

public interface Filter {
	
	public void setParam(int p);
	public Float getNextValue(float value);
	public Float getNextValue(long timestamp, float value);
	public String getName();
}
