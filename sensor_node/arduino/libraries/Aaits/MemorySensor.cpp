#include "MemorySensor.h"
#include <Arduino.h>

MemorySensor::MemorySensor():Sensor() {
    strcpy(name,"SRAM");
    strcpy(units,"bytes");
}
  
boolean MemorySensor::action() {

    measured_value = freeMemory();
    measured_millis = millis();
    return true;
    
}

void MemorySensor::writeDescriptionToSd( SdFile *file  ) {
    Sensor::writeDescriptionToSd( file );
}
