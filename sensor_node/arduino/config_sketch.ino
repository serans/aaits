/**
 * This sketch may be used to initialice or reset the
 * EEPROM parameters of a sensor node:
 *    - Device UID: number which can unequivocally identify the sensor node
 *    - file counter: internal number used to keep track of data file names used.
 */
#include <EEPROM.h>

void setup () {
  Serial.begin(9600);
  int val;

  Serial.println("Current Configuration:");
  Serial.print("  Device UID:");
  Serial.println(EEPROM.read(0));
  Serial.print("  File counter:");
  Serial.println(EEPROM.read(1));
 
  Serial.println("\nChange config? (y/n)");
  while(!Serial.available()) ;
  if(Serial.read()=='y') {
 
      Serial.println("Insert Device UID (0-255):");
      do {
        while(!Serial.available()) ;
        val = serReadInt();
        if(val<0 || val>255) {
          Serial.println("Out of range. Enter an integer between 0 and 255");
        }
      }while( val<0 || val>255);
      EEPROM.write(0,val);  
    
      Serial.println("Reset file counter? (y/n):");
      while(!Serial.available()) ;
      if(Serial.read()=='y') {
          Serial.println("File counter reset");
          EEPROM.write(1,0);
      }
  }
  Serial.println("Configuration Done");
}

void loop() {
}
   
   
/**
 * @brief Reads an integer from Serial port input
 * @returns the Serial input converted to int, or -1 if
 *          the input is not a number
 */
int serReadInt()
{
 int i, serAva;
 char inputBytes [7];
 char * inputBytesPtr = &inputBytes[0];
     
 if (Serial.available()>0)  {
   delay(5); // Delay for terminal to finish transmitted
   serAva = Serial.available();
   for (i=0; i<serAva; i++)
     inputBytes[i] = Serial.read();
   inputBytes[i] =  '\0';  
   return atoi(inputBytesPtr);
 }
 else
   return -1;
}

