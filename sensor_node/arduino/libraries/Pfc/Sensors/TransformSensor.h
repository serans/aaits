#ifndef TRANSFORM_H
#define TRANSFORM_H

#include "Sensor.h"
#include "QueueList.h"

class TransformSensor: public Sensor {
    public:
      QueueList<int> params;
      byte ref_sensor_id;
      Sensor *ref_sensor;
      
      TransformSensor();
      inline virtual char* getClassName() {return "Transform";};      
      boolean action();
      
      void resetParams()   { params.removeAll(); };
      void setParam(int p) { params.push(p);     };
};

#endif
