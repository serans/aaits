package utils.filters;

public class CAFilter implements Filter {

	private float average = 0;
	private int count = 0;
	
	public CAFilter() {
	}
	
	@Override
	public Float getNextValue(float value) {
		average = (value + count*average) / (count+1);
		count++;
		return average;
	}

	@Override
	public Float getNextValue(long timestamp, float value) {
		return getNextValue(value);
	}
	
	@Override
	public String getName(){
		return "CA";
	}

	@Override
	public void setParam(int p) {
		// TODO Auto-generated method stub	
	}
}
