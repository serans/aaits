package utils.filters;

public class SMAFilter implements Filter {

	private int index=0;
	private float[] values;
	
	public SMAFilter(){
	}

	public SMAFilter(int order) {
		values = new float[order-1];
	}

	@Override
	public void setParam(int order) {
		values = new float[order-1];
	}
	
	@Override
	public Float getNextValue(float value) {
		Float result = null;
		if(index>=values.length) {
			result =0.0F;
			for( float f: values) {
				result+=f/(values.length+1);
			}
			result+=value/(values.length+1);
		}
		values[index%values.length] = value;
		index++;
		return result;
	}

	@Override
	public Float getNextValue(long timestamp, float value) {
		return getNextValue(value);
	}
	
	@Override
	public String getName(){
		return "SMA("+(values.length+1)+")";
	}

}
