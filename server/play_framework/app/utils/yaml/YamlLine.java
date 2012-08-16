package utils.yaml;

/**
 * Represents a line from a YAML file
 * @author Miguel Hermo Serans
 */
public class YamlLine {
	private String key;
	private String value;
	boolean isNew;
	private int indentation;

	public static YamlLine readLine( String line ) {
		return new YamlLine(line);
	}
	
	
	public YamlLine() {
		
	}
	
	public YamlLine(String line) {
		tokenize(line);
	}

	/**
	 * Reads a YAML line and tokenizes it
	 * @param line YAML line to be read
	 */
	public void tokenize(String line) {
		final int READING_ROOT = 0;
		final int READING_KEY = 1;
		final int READING_VALUE = 2;
		int state = 0;

		indentation = 0;
		isNew = false;
		key = "";
		value = "";

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (c == '\0' || c == '\n')
				return;

			if (c == '#')
				return;

			switch (state) {
			case READING_ROOT:
				if (c == ' ')
					indentation++;
				else if (c == '-') {
					indentation++;
					isNew = true;
				} else {
					key+=c;
					state = READING_KEY;
				}
				break;
			
			case READING_KEY:
				if(value.length()==0 &&  c == ' ') continue;
				if(c == ':') {
					state = READING_VALUE;
				} else {
					key+=c;
				}
				break;
			case READING_VALUE:
				if(value.length()==0 && c == ' ') continue;
				value+=c;
				break;
			}
		}
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean isNew() {
		return isNew;
	}

	public int getIndentation() {
		return indentation;
	}
	
	public String toString() {
		String out="";
		for(int i=0; i<getIndentation();i++) {
			out+=' ';
		}
		if(isNew()) out+="-";
		out+= getKey()+":"+getValue()+"\n";
		return out;
	}
}