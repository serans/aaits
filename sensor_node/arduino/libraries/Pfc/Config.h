/** @file Config.h 
 *
 *  @author Miguel Hermo Serans
 */
 
#ifndef CONFIG_H
#define CONFIG_H

#include "Libs/SdFat/SdFat.h"
#include "Libs/TimerOne/TimerOne.h"

#include "Sensors/Sensor.h"
#include "Sensors/MemorySensor.h"
#include "Sensors/AnalogSensor.h"

#include "Comm/Comm.h"
#include "Comm/BTComm.h"

#include "Yaml/YamlReader.h"
#include "Utils/QueueList.h"

/**
 * @defgroup YAML_STATE Yaml interpreter state
 *
 * Configuration is formatted in YAML. the state
 * represents the different YAML nodes Config may
 * be parsing at the moment
 *
 * @{
 */
#define YML_ROOT 0
#define YML_SENSOR 1
#define YML_PARAM 2
#define YML_INIT 3
#define YML_ACTION 4
#define YML_BT 5
/** @} */

/** @brief Stores, retrieves and modifies Sensor node's configuration */
class Config {  
   private:
     YamlReader yr;
     BTComm *bt_device;     
     QueueList < Sensor* > *sensor_list;
     SdFile config_file;
     
   /** @brief indicates if the configuration has been changed 
     * @defgroup CONFIG_STATE_VARS Configuration vars
     * During configuration, this set of variables is used to keep track of the current
     * state of the process, what changes has been made, etc.
     * @{
     */
     
     /** @brief flag indicating a change in configuration addecting data adquisition */
     boolean data_config_changed;
     
     /** @brief state of the yml configuration file interpreter */
     byte yml_state;
     
     /** @brief pointer to the sensor that is being configured */
     Sensor *curr_sensor;
     
     /** @brief stores sensor's ids read from the config file, to delete those not present
      *
      * There's no specefic command for deleting a sensor, so we keep track of what
      * sensors are present at the config file to check if there are any sensor at sensor_list
      * that must be deleted
      * @see finishConfiguration
      */
     QueueList<byte> sensors_id;
     
     /** @} */
     
     void configureRoot();
     void configureBT();
     void configureSensor();
     void internalError();
     void finishConfiguration();
     
   public:
     void (*onDataConfigChanged)(void);
     void (*onTick)(void);
     
     char *device_name;
     unsigned long int sampling_period;

    /** @brief flag indicating if we are waiting for a bt response
     *
     * When a configuration command is sent to the BT module, we must wait for it to take place
     * before sending a new command. Therefore, when this flag is set to <b>true</b>, we don't
     * continue with the configuration
     */
     boolean awaiting_bt_response;
   
     Config() {
         sampling_period=0;
         onDataConfigChanged=NULL;
         onTick=NULL;
     };

     void readFromFile();
     void loop();
     
     void readConfigChar(char c);
     
     void BTCommandResponse(boolean ok);
     void setSensorList( QueueList< Sensor * > *sl );
     void setBtHandler( BTComm *bt );
    // char* getDataHeader();
     
};

#endif

