#include "PUtils.h"

/** @brief prints string from program memory to Serial 
 *  @param p string to be printed
*/
void printP( prog_char p[] ) {
    char b;
    byte i=0;
    do {
      b = (char) pgm_read_byte(&p[i++]);
      Serial.print(b);
    } while (b!='\0');
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
    boolean equals=true;
    
    while( b = (char) pgm_read_byte(&prog[i])) {
      if(b!=ram[i]) equals=false;
      i++;
    } while (b!='\0');
    
    if(i!=strlen(ram)) return false;
    
    return equals;
}
