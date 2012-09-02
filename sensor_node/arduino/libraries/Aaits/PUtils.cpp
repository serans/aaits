#include "PUtils.h"

/*
prog_char p_id[]         PROGMEM = "id";
prog_char p_class[]      PROGMEM = "class";
prog_char p_name[]       PROGMEM = "name";
prog_char p_units[]      PROGMEM = "units";
prog_char p_steps[]      PROGMEM = "steps";
prog_char p_pin[]        PROGMEM = "pin";
prog_char p_ref[]        PROGMEM = "ref";
prog_char p_colon[]      PROGMEM = ":";
prog_char p_indent[]     PROGMEM = "   ";
prog_char p_newitem[]    PROGMEM = "  -";
*/

/**
 * @brief copies a string from progam to main memory
 * @param p string in program memory
 * @param dest string in RAM where p is to be copied
 */
void copyP( prog_char p[], char dest[]) {
    char b;
    byte i=0;
    while( (b = (char) pgm_read_byte(&p[i++])) != '\0') {
       dest[i] = b;
    }
}

/** @brief prints string from program memory to Serial 
 *  @param p string to be printed
*/
void printP( prog_char p[] ) {
    char b;
    byte i=0;
    
    while( (b = (char) pgm_read_byte(&p[i++])) != '\0') {
      Serial.print(b);
    } 
}

/** @brief saves string from program memory into a file
 *  @param p string to be saved
 *  @param f pointer to file where p will be saved
*/
void sdprintP( SdFile *f, prog_char p[] ) {
    char b;
    byte i=0;
  
    while( (b = (char) pgm_read_byte(&p[i++])) != '\0') {
        f->print(b);
    }
}

/** @brief compares string from ram to string in progmem
 *  @param ram string stored in SRAM
 *  @param prog string stored in PROGMEM
 *  @returns true when both are equal
 */
boolean strcmpP( char *ram, prog_char prog[] ) {
    char b;
    byte i=0;
    
    while( (b = (char) pgm_read_byte(&prog[i])) != '\0') {
      if(b!=ram[i]) return false;
      i++;
    }
    
    if(i!=strlen(ram)) return false;
    
    return true;
}
