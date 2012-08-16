#include "TransformSensor.h"

/** @brief calculates the polynomial transform of another sensor's measure */
boolean TransformSensor::action() {
    measured_value = 0;
    
    int param = 0;
    byte power= 0;
    
    params.goToFirst();
    
    while( param = params.getNext() ) {
        measured_value += param * pow(ref_sensor->getMeasurement(),power++);
    }
    
    return true;
}
