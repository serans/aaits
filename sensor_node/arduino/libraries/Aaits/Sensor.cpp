#include "Sensor.h"
#include "PUtils.h" 

prog_char p_id[]         PROGMEM = "id";
prog_char p_class[]      PROGMEM = "class";
prog_char p_name[]       PROGMEM = "name";
prog_char p_units[]      PROGMEM = "units";
prog_char p_steps[]      PROGMEM = "steps";
prog_char p_pin[]        PROGMEM = "pin";
prog_char p_ref[]        PROGMEM = "ref";
prog_char p_trans_func[] PROGMEM = "trans_func";
prog_char p_trans_param[] PROGMEM = "trans_param";
prog_char p_colon[]      PROGMEM = ":";
prog_char p_indent[]     PROGMEM = "   ";
prog_char p_newitem[]    PROGMEM = "  -";

Sensor::Sensor()  {
    ticks = 0;
    steps = 1;
    pin   = 0;
    id    = 0;
    setName("generic_sensor");
    setUnits("no_units");
    paused = true;
    transform = NULL;
}

Sensor::~Sensor() {
//    delete( transform );
}

/** @brief writes the description in YAML of the sensor to the specified file 
* @param file
*/
void Sensor::writeDescriptionToSd( SdFile *file  ) {

    sdprintP(file, p_newitem);
    sdprintP(file, p_id); 
    sdprintP(file, p_colon); 
    file->println(id);
        
    sdprintP(file, p_indent);
    sdprintP(file, p_class); 
    sdprintP(file, p_colon); 
    file->println(getClassName());
    
    sdprintP(file, p_indent);
    sdprintP(file, p_ref); 
    sdprintP(file, p_colon); 
    file->println(ref);
        
    sdprintP(file, p_indent);
    sdprintP(file, p_name); 
    sdprintP(file, p_colon); 
    file->println(name);

    sdprintP(file, p_indent);
    sdprintP(file, p_units); 
    sdprintP(file, p_colon); 
    file->println(units);

    sdprintP(file, p_indent);
    sdprintP(file, p_steps); 
    sdprintP(file, p_colon); 
    file->println(steps);

    if(pin!=0) {
      sdprintP(file, p_indent);
      sdprintP(file, p_pin);
      sdprintP(file, p_colon);
      file->println(pin);
    }
    
    #ifdef USE_TRANS
    if(transform != NULL) {
      sdprintP(file, p_indent);
      sdprintP(file, p_trans_func); 
      sdprintP(file, p_colon);
      file->println(transform->getClassName());
      if(transform->numParams()>0) {
          sdprintP(file, p_indent);
          sdprintP(file, p_trans_param);
          sdprintP(file, p_colon);
          file->println();
          for(byte i=0; i<transform->numParams(); i++) {
              sdprintP(file, p_indent);
              sdprintP(file, p_newitem);
              file->println(transform->getParam(i));
          }
      }
    }
    #endif

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
    
    #ifdef USE_TRANS
    if(transform!=NULL) {
        char fval[8];
        
        dtostrf(transform->convert(measured_value), 1,2, &fval[0]);
        
        sprintf(line,"%ld;%d;%s;%d",measured_millis-base_millis, id, fval , measured_value);
    } else 
    #endif
    {
        sprintf(line,"%ld;%d;%d;",measured_millis-base_millis, id, measured_value);
    }
}

void Sensor::getDataLine(char *line) {
    getDataLine(line, 0);
}

