#include "Sensor.h"

prog_char p_id[]      PROGMEM = "id";
prog_char p_class[]   PROGMEM = "class";
prog_char p_name[]    PROGMEM = "name";
prog_char p_units[]   PROGMEM = "units";
prog_char p_steps[]   PROGMEM = "steps";
prog_char p_pin[]     PROGMEM = "pin";
prog_char p_indent[]  PROGMEM = "   ";
prog_char p_newitem[] PROGMEM = "  -";


Sensor::Sensor()  {
    ticks = 0;
    steps = 1;
    pin = 0;
    id=0;
    setName("generic_sensor");
    setUnits("no_units");
    paused = true;
}

Sensor::~Sensor() {
    
}

/** @brief Wrapper for getHeader */
void Sensor::writeDescriptionToSd( SdFile *file  ) {
  
    sdprintP(file, p_newitem);
    sdprintP(file, p_id); 
        file->print(":"); 
        file->println(id);
        
    sdprintP(file, p_indent);
    sdprintP(file, p_class); 
        file->print(":"); 
        file->println(getClassName());
        
    sdprintP(file, p_indent);
    sdprintP(file, p_name); 
        file->print(":"); 
        file->println(name);

    sdprintP(file, p_indent);
    sdprintP(file, p_units); 
        file->print(":"); 
        file->println(units);

    sdprintP(file, p_indent);
    sdprintP(file, p_steps); 
        file->print(":"); 
        file->println(steps);
  
}

/**
 * @brief counts the number of "Ticks" sot that the sensor so it can perform 
 * whatever action it's supposed to perform.
 * @returns true when a new measurement is made, false otherwise
 */
boolean Sensor::tick() {
  if(!paused) {
    if (ticks < steps) ticks++;  
    if ( ticks >= steps ) {
      ticks = 0;
      return action();
    }  
  }
  return false;
}

/**
 * @brief This function is called every "steps" times tick() is called (usually measuring will
 * take place here)
 * @return true if a measurement is made, false otherwise
 */
boolean Sensor::action(){
    
}

/**
 * @brief Sets a pin as the curent sensor's
 */
void Sensor::setPin(byte pin) {
    this->pin=pin;
    pinMode(pin,INPUT);
}

/**
 * @brief returns a data line, with time counted since base_millis
 */
void Sensor::getDataLine(char *line, long unsigned int base_millis) {
    sprintf(line,"%ld;%d;%d;",measured_millis-base_millis, id, measured_value);
}

void Sensor::getDataLine(char *line) {
    getDataLine(line, 0);
}

