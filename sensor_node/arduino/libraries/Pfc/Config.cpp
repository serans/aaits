#include <avr/pgmspace.h>
#include <TimerOne.h>
#include "Config.h"
#include "PUtils.h"
#include "TransformFunction.h"

/** @brief starts reading config. from the default SD file */
void Config::readFromFile() {
   config_file.open("CONFIG.YML", O_READ);         
   awaiting_bt_response = false;
   sensors_id.removeAll();
   yr.start();
   yml_state = YML_ROOT;
};

/** @brief code executed every main loop
 *
 * When reading configuration, this functions feeds readConfigChar
 * with characters from the config file.
 */
void Config::loop() {
   if(config_file.isOpen() && !awaiting_bt_response) {
      char c = config_file.read();
      if( c<0) {
          config_file.close();
          c='\n'; //we add a \n so that yamlReader recognizes the end of file
          readConfigChar(c); //read the last line
          finishConfiguration();
      } else {
          readConfigChar(c);
      }
   }
 }

/**
 * @brief finishes configuration of the device
 *
 * When the end of the configuration file is reached, we check if
 * there have been changes that need a new data file to be opened.
 * Also, as there's no explicit "remove sensor" command, we check if
 * there's any sensor on sensor_list which was not mentioned on the
 * configuration and delete them accordingly.
 * @see sensors_id
 */
void Config::finishConfiguration() {
    //if any sensor is paused, that means configuration has changed
    sensor_list->goToFirst();
    Sensor *s;

    while( s = sensor_list->getNext() ) {
        int i;
        boolean remove_sensor=true;
        
        //remove the sensor if not present on the configuration file
        sensors_id.goToFirst();
        while(i = sensors_id.getNext()){
            if(s->getId()==i) remove_sensor=false;
        }
        
        if(remove_sensor) {
            sensor_list->removeItem(s);
        } else {
            data_config_changed = data_config_changed || s->isPaused();
        }
    }
      
    if(data_config_changed && onDataConfigChanged!=NULL) onDataConfigChanged();
}
 
/**
 * @defgroup CONFIG_FUNCTIONS Configuration functions
 * @{
 */
 
/**
 * @brief handles level 0 parameters from YAML config file
 * 
 * The root (level 0) YAML node contains information about the
 * device itself, such as device_name and sampling_period.<br/>
 * When sampling period changes, all sensors are paused to prevent their
 * output from being written to a file containing the wrong sampling period
 * in the header.
 */
void Config::configureRoot() {
    //processing
    if ( strcmp(yr.line.key,"device_name")==0 ) {
        if(device_name != yr.line.value) {
            free(device_name);
            device_name=(char*) malloc(sizeof(char)* (strlen(yr.line.value)+1));
            strcpy(device_name,yr.line.value); 
            data_config_changed=true; 
        }
    }else if( strcmp(yr.line.key,"sampling_period")==0 ) { 

      if(sampling_period != atoi(yr.line.value) ) {

            Sensor *s;

            //pause all sensors
            sensor_list->goToFirst();
            while( s = sensor_list->getNext() ) s->pause();

            //new sampling period
            
            sampling_period = atoi(yr.line.value);
            
            //sampling period in milis, timer1 in micros
            Timer1.initialize(sampling_period*1000);
             if(onTick!=NULL) {

              Timer1.attachInterrupt(onTick);
            }
            
            //mark configuration as changed
            data_config_changed = true;
        }
    }
}

/**
 * @brief handles bluetooth configuration
 *
 * When a BT parameter is modified, Config waits until the BT device responds
 * @see awaiting_bt_response
 */
void Config::configureBT() {
    if ( strcmp(yr.line.key,"device_name")==0) {
        if( strcmp(yr.line.value,bt_device->device_name)!=0 ) {
            bt_device->setDeviceName(yr.line.value); 
            awaiting_bt_response = true; 
        }
    }
    if ( strcmp(yr.line.key,"pin_code")==0) {
        if( strcmp( yr.line.value,bt_device->pin_code) !=0 ) {
            bt_device->setPinCode(yr.line.value); 
            awaiting_bt_response = true; 
        }
    }
}

/**
 * @brief handles Sensor configuration
 */
void Config::configureSensor() {

    if (yr.line.newitem) curr_sensor = NULL;

    // id line MUST be first
    if ( strcmp(yr.line.key,"id")==0 ) {

      sensors_id.push(atoi(yr.line.value));
      
      //search for sensor with that id
      Sensor *s;
      sensor_list->goToFirst();
      while( s = sensor_list->getNext()) {
          if(s->getId()==sensors_id.peekLast()) {
              curr_sensor = s;
          }
      }

      //if it doesn't exist, create new
      if(curr_sensor == NULL) {
          //create new sensor
          curr_sensor = new Sensor();
          curr_sensor->setId( sensors_id.peekLast() );
          sensor_list->push(curr_sensor);
      }
    }

    if( strcmp(yr.line.key,"class")==0 ) {

      if(curr_sensor==NULL) internalError();
      
      //config's and current's classes don't match
      else if( strcmp(curr_sensor->getClassName(),yr.line.value)!=0) {;
            
          //remove sensor from list
          sensor_list->removeItem(curr_sensor);
          free(curr_sensor);
          
          //add with new class
          if( strcmp(yr.line.value,"Memory")==0 ) {
              curr_sensor = new MemorySensor();
          } else 
          if( strcmp(yr.line.value,"Analog")==0 ) {
              curr_sensor = new AnalogSensor();
          } else {
              internalError();
          }
          
          curr_sensor->setId(sensors_id.peekLast());
          sensor_list->push(curr_sensor);
      }
    }

    if( strcmp(yr.line.key,"name")==0 ) {
      if(curr_sensor==NULL) internalError();
      else if(strcmp(curr_sensor->getName(),yr.line.value)!=0) {
          curr_sensor->pause();
          curr_sensor->setName(yr.line.value);
      }
    }

    if( strcmp(yr.line.key,"units")==0) {
      if(curr_sensor==NULL) internalError();
      else if(strcmp(curr_sensor->getUnits(),yr.line.value)!=0) {
          curr_sensor->pause();
          curr_sensor->setUnits(yr.line.value);
      }
    }

    if( strcmp(yr.line.key,"pin")==0) {
      if(curr_sensor==NULL) internalError();
      else if(curr_sensor->getPin()!=atoi(yr.line.value)) {
          curr_sensor->pause();
          curr_sensor->setPin(atoi(yr.line.value));
      }
    }

    if( strcmp(yr.line.key,"steps")==0) {
      if(curr_sensor==NULL) internalError();
      else if(curr_sensor->getSteps()!=atoi(yr.line.value)) {
          curr_sensor->pause();
          curr_sensor->setSteps(atoi(yr.line.value));
      }
    }
    
    if( strcmp(yr.line.key,"ref")==0) {
      if(curr_sensor==NULL) internalError();
      else if(strcmp(curr_sensor->getRef(),yr.line.value)!=0) {
          curr_sensor->pause();
          curr_sensor->setRef(yr.line.value);
      }
    }
    
    if( strcmp(yr.line.key,"trans_func")==0) {
        if(curr_sensor==NULL) internalError();
        else {
            if(curr_sensor->transform!=NULL) {
                delete(curr_sensor->transform);
                curr_sensor->transform=NULL;
            } else {
                curr_sensor->transform = new TransformFunction(yr.line.value);
            }
        }
    }

}

void Config::configureParam() {
   if(curr_sensor != NULL)
     if(curr_sensor->transform != NULL) {
       curr_sensor->transform->addParam(atof(yr.line.key));
     }

}

/** @} */

/** 
* @brief Reads a char from a Config file and interprets its content.
* 
* This functions receives a char from a config file and interprets its content
* when a new line is completed. readConfigChar takes care only of state changes,
* tracking what YAML node is actually being read, and leaves the actual configuration 
* actions to the configure functions.
* @see configureRoot()
* @see configureBT()
* @see configureSensor()
*/
void Config::readConfigChar(char c) {

  if( yr.readChar( c )) {

      //first we calculate state changes depending on indentation level
      if(yr.line.indent_level==0) yml_state=YML_ROOT;
      if(yr.line.indent_level==1) if( yml_state==YML_PARAM ) yml_state=YML_SENSOR;
    
      switch(yml_state) {
          case YML_ROOT:
              configureRoot();

              if ( strcmp(yr.line.key,"bluetooth")==0)   { yml_state = YML_BT; }
              else if (strcmp(yr.line.key,"sensors")==0) { yml_state = YML_SENSOR;}
              break;
              
          case YML_BT:
              configureBT();
              break;
          
          case YML_SENSOR:
              configureSensor();
              if( strcmp(yr.line.key,"trans_param")==0) { yml_state = YML_PARAM; }
              break;
          
          case YML_PARAM:
              configureParam();
      }

  }
}

/**
 * @brief returns a YAML text describing the current data configuration

char* Config::getHeader() {
    
} */

/**
  * @brief used to inform Config of the result of the BT configuration commands
  * @param ok true when command was accomplished successfully, false otherwise
  */
 void Config::BTCommandResponse( boolean ok ) {
     awaiting_bt_response = false;
 }
 
 void Config::setSensorList( QueueList <Sensor * > *sl) {
     sensor_list = sl;
 }
 
 void Config::setBtHandler( BTComm *bt ) {
     bt_device = bt;
 }
 
 void Config::internalError() {
     Serial.println("500 internal error");
 }

