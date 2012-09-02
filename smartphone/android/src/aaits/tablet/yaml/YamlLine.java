package aaits.tablet.yaml;

/**
 * Represents a line from a YAML file
 * @author Miguel Hermo Serans
 */
public class YamlLine {
	private String key;
	private String value;
	boolean isNew;
	private int indentation;
	private int level;
	
	private static 	int curr_indent=0;
	private static  int curr_level=0;

	/**
	 * Resets the values of the internal counters so that a new file
	 * can be read without resulting in erroneous level readings when
	 * using readNextLine
	 * @see readNextLine
	 */
	public static void startNewFile() {
		YamlLine.curr_indent=0;
		YamlLine.curr_level=0;
	}
	
	/**
	 * Reads the next file of a yaml file.
	 * 
	 * 
	 * @param line yaml line
	 * @see startNewFile
	 * @return
	 */
	public static YamlLine readNextLine( String line ) {
		YamlLine yl = new YamlLine(line);
		
		if(curr_indent == yl.indentation) {
			yl.level=curr_level;
		}
		else if(curr_indent < yl.indentation) {
			++curr_level;
			curr_indent=yl.indentation;
			yl.level=curr_level;
		}
		else {
			curr_level = (int) Math.ceil( (curr_level*yl.indentation)/curr_indent);
			curr_indent = yl.indentation;
			yl.level = curr_level;
		}
		
		return yl;
	}
	
	public YamlLine(String line) {
		tokenize(line);
	}
	
	public YamlLine() {
		
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

	public int getLevel() {
		return level;
	}
	
	public String toString() {
		String out="";
		for(int i=0; i<indentation;i++) {
			out+=' ';
		}
		if(isNew()) out+="-";
		out+= getKey()+":"+getValue()+"\n";
		return out;
	}
}