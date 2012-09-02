/** @file YamlReader.h 
 *
 *  @author Miguel Hermo Serans
 */
#ifndef YAMLREADER_H
#define YAMLREADER_H

/**
 * @brief Stores a tokenized YAML line
 */
struct YamlLine {
    boolean newitem;
    byte indent_level;
    byte indent_spaces;
    char key[30];
    char value[30];
};

/**
 * @brief Yaml Reader
 *
 * Reads a line formatted in YAML one character at a time, tokenizes it and stores it
 * in YamlReader::line until a new char is received
 */
class YamlReader {
    private:
        
        boolean reading_key;
        boolean reading_value;
        boolean reading_comment;
        boolean line_complete;
        
        byte level;
        byte nspaces;
        
        byte char_index;
        
        void resetValues() {
            reading_key = reading_value = reading_comment = false;
            line.newitem = false;
            line.indent_level = 0;
            line.indent_spaces = 0;
            line.key[0] = '\0';
            line.value[0] = '\0';
            line_complete=false; 
            
            char_index=0;
        };
    public:
        /** contains the tokenized content of the current line */
        YamlLine line;
        
        YamlReader() { start(); }
        
        /** resets the reader to accept a new YAML file */
        void start() {
           nspaces = level = 0 ; 
           resetValues();
        }
        
        /**
         * @brief Interprets character c.
         *
         * Reads character <b>c</b> corresponding to a YAML file. <br/>
         * The input is tokenized and stored in YamlReader::line.
         * @param c a character
         * @returns true when a new line is read
         */
        boolean readChar(char c) {

            if(line_complete) resetValues();
            
            // EOL / EOF
            if(c=='\n' || c=='\0') {
                //empty lines do not count towards indent level
                if(reading_key || reading_value) {
                  
                  if(reading_key) line.key[char_index]='\0';
                  if(reading_value) line.value[char_index]='\0';
                  
                  if(line.indent_spaces == nspaces)      line.indent_level = level;
                  else if (line.indent_spaces > nspaces) line.indent_level = ++level;
                  else if (line.indent_spaces < nspaces) {
                      if(level==0) line.indent_level = level = 0;
                      else level = line.indent_level = ceil(line.indent_spaces / (nspaces/level));
                  }
                  nspaces = line.indent_spaces;
                  
                }
                line_complete = true;
                return true;
            }
            
            // comments            
            if(c=='#') {reading_comment=true; return false; }
            if(reading_comment) return false;
            
            //indentation
            if(!reading_key && !reading_value) {
                if(c==' ') line.indent_spaces++;
                else if(c=='-') {
                  line.indent_spaces++;
                  line.newitem=true;
                } else {
                  line.key[char_index++]=c;
                  reading_key = true;
                }
                return false;
            }
            
            //key
            if(reading_key) {
                if(c==':') {
                    reading_key=false;
                    reading_value=true;
                    line.key[char_index]='\0';
                    char_index=0;
                }
                else if(c!=' ') {line.key[char_index++] = c;} //do not count whitespaces or tabs
                return false;
            }
            
            //value
            if(reading_value) {
                if(c!=' ') line.value[char_index++] = c;
                return false;
            }
        };
        
};

#endif
