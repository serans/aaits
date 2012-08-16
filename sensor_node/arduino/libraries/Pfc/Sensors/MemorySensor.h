#ifndef MEMORYSENSOR_H
#define MEMORYSENSOR_H

#include "../Libs/MemoryFree/MemoryFree.h"
#include "Sensor.h"

class MemorySensor: public Sensor {
    public:
      MemorySensor();
      boolean action();
      void writeDescriptionToSd( SdFile *file  );
      inline virtual char* getClassName() {return "Memory";};
};

#endif
