#include <Arduino.h>
#include "MemorySensor.h"

MemorySensor::MemorySensor() {
    Sensor::Sensor();
    strcpy(name,"SRAM");
    strcpy(units,"bytes");
}
  
boolean MemorySensor::action() {

    measured_value = freeMemory();
    measured_millis = millis();
    return true;
    
}

/** @brief Wrapper for getHeader */
void MemorySensor::writeDescriptionToSd( SdFile *file  ) {
    Sensor::writeDescriptionToSd( file );
}
