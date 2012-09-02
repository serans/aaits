#ifndef ANALOGSENSOR_H
#define ANALOGSENSOR_H

#include "Sensor.h"

class AnalogSensor: public Sensor {
   private:
        virtual boolean action();
    
   public:
     AnalogSensor();
     
     char* getClassName() { return "Analog"; };

};

#endif
