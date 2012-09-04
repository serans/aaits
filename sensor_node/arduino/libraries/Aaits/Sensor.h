#ifndef SENSOR_H
#define SENSOR_H

#include <Arduino.h>
#include <SdFat.h>

#include "PUtils.h"
#include "TransformFunction.h"
/**
 * @brief repeats string "s" "n" times
 * @param s char *to repeat
 * @param n number of repetitions
 */
extern char *repeatStr(char *s, int n);

/** @brief generic Sensor class
 *
 */
class Sensor {

    protected:
    
        /** pin where the sensor is attached */
        byte pin;
        /** sensor's unique identifier */
        byte id;
        /** sensor name */
        char name[20];
        /** sensor ref */
        char ref[10];
        /** units in which the measurement is taken */
        char units[20];

        unsigned long measured_millis;
        int measured_value;
        
        byte steps;
        byte ticks;
        boolean paused;
        
        inline virtual boolean action();
        TransformFunction *transform;
        
    public:
        Sensor();
        virtual ~Sensor();
        
        virtual boolean tick();
        
        virtual void start() {paused = false;};
        virtual void pause() {paused = true;};
        
        virtual void writeDescriptionToSd(SdFile *file);
        
        //GETTER-SETTER
        virtual char *getClassName() {return "Generic";};
        
        void getDataLine(char *line);
        virtual void getDataLine(char *line, unsigned long base_millis);
        
        boolean isPaused()       {return paused;}
        byte   getId()           {return id;}; 
        char * getName()         {return name;};
        char * getUnits()        {return units;};
        byte   getPin()          {return pin;};
        unsigned int  getSteps() {return steps;};
        char * getRef()          {return ref;};
        int getMeasurement()     {return measured_value;};
        TransformFunction *getTransform() {return transform;};

        virtual void setPin(byte pin);
        void setId(byte id)            {this->id=id;}; 
        void setName(char *name)   {strcpy(this->name,name);};
        void setUnits(char *units) {strcpy(this->units,units);};
        void setSteps(unsigned int st) {this->steps=st;};
        void setRef(char * ref)    {strcpy(this->ref,ref);};
        void setTransform(TransformFunction *tf) { this->transform=tf; }
};



#endif
