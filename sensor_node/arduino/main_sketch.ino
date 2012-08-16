/** @file pfc_babilonia.ino 
 *  @brief main program file
 *  @author Miguel Hermo Serans
 *
 *  This is the main file of the Sensor Node's firmware.<br/>
 *  In order to keep the project compatible with the arduino IDE a few hacks
 *  had to be introduced. Particulary, all file dependencies are to be included here
 *  because the IDE will only 
 */
#define _DEBUG_

#include <SdFat.h>
#include <QueueList.h>
#include <avr/pgmspace.h>
#include <MemoryFree.h>
#include <TimerOne.h>

#include "StringNew.h"
#include "YamlReader.h"

#include "Sensor.h"
#include "MemorySensor.h"

#include "Comm.h"
#include "BTComm.h"
#include "Config.h"


Config config;
SdFat sd;
SdFile data_file;
BTComm comm;
QueueList < Sensor * > sensors;
QueueList < StringNew > buffer;

/** @brief number of buffer items 
 *
 * when data configuration changes, a new file is created containing new headers
 * describing the current config. If there is data on the buffer when this change
 * happens, all old data must be written to the old file before creating a new one.<br/>
 * This counter contains the number of items in the buffer to be written to the old file.
 */
byte buffer_count;

/**
 * @brief indicates that the buffer contains items corresponding to a new configuration
 *
 * In this case, the number of "old" items on the buffer is given by buffer_count
 */
boolean new_buffer;

void setup() {

   buffer_count = 0;
   new_buffer   = false;
  
   Serial.begin(9600);
   
   #ifdef _DEBUG_
     Serial.println("\n\n#Beginning Program\n\n");
   #endif
   
   if(!sd.init(SPI_HALF_SPEED, SS_PIN)) Serial.println("SD card not found");
   comm.setConfig(&config);

   config.setBtHandler( &comm );
   config.setSensorList(&sensors);
   config.onDataConfigChanged = configChanged;
   config.onTick = tick;
   
   config.readFromFile();
   
}

void loop() {

   //send read chars to comm
   if(Serial.available()) {
     comm.readChar(Serial.read());
   }
   
   //write buffer to SD
   if(buffer.count()>0) {

       //check if a new data file has to be created
       if(new_buffer) {
           if(buffer_count>0) buffer_count--;
           else {
               new_buffer=false;
               /** @todo create file */
               createDataFile();
           }
       }
       if(data_file.isOpen()) {
         StringNew l = buffer.pop();

         data_file.write(l.getBuffer());
         data_file.write('\n');
         data_file.sync();
       }
   }
      
   config.loop();
   comm.loop();
}

/**
 * @brief function executed periodically to get measurements from sensors into the buffer
 */
void tick() {
   Sensor *s;
   sensors.goToFirst(); 
   while ( s = sensors.getNext() ) {
      if(s->tick()) {
          buffer.push(s->getDataLine().getBuffer());
      }
   }
}

/** @brief function executed when configuration is changed
 *
 * When the configuration of parameters affecting the measurements are changed, 
 * this function sets a counter (buffer_count) that will trigger the creation of a
 * new data file when the buffer corresponding to the old data is written to the SD.
 * @see buffer_count
 */
void configChanged() {
  Sensor *s; 
  buffer_count=buffer.count();
  new_buffer=true;
  
  sensors.goToFirst(); 
  while ( s = sensors.getNext() ) {
    s->start();
    Serial.println(s->getFullDescription().getBuffer());
  }
}

/**
 * @brief Creates a new data file
 */
void createDataFile() {
    byte file_number=0;
    StringNew file_name;
    
    if(data_file.isOpen()) data_file.close();
    
    //find filename
    do {
        file_name="";
        file_name.concat(file_number++);
        file_name.concat(".YML");
    } while(sd.exists(file_name.getBuffer()));

    data_file.open(file_name.getBuffer(), O_CREAT | O_WRITE | O_APPEND);
    if(!data_file.isOpen()) Serial.println("Could not open file");
    
    //write headers to file
    
}
