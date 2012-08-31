#include <EEPROM.h>
#include <avr/pgmspace.h>
#include <SdFat.h>
#include <TimerOne.h>
#include <MemoryFree.h>

#include <Pfc.h>

#define EEPROM_UID   0
#define EEPROM_NFILE 1

Config config;
BTComm comm;

QueueList < Sensor* > sensors;
QueueList < char* >   buffer;

SdFat sd;
SdFile data_file;
SdBaseFile *file_list;

#define _DEBUG_

prog_char begin_str[]         PROGMEM = "\n\nSTARTING\n\n";
prog_char sd_not_found[]      PROGMEM = "\n\nSD CARD NOT FOUND\n\n";
prog_char file_not_found[]    PROGMEM = "\n\nFILE NOT FOUND\n\n";

prog_char p_device_uid[]      PROGMEM = "device_uid";
prog_char p_device_name[]     PROGMEM = "device_name";
prog_char p_sampling_period[] PROGMEM = "sampling_period";
prog_char p_timestamp[]       PROGMEM = "start_time";
prog_char p_sensors[]         PROGMEM = "sensors";
prog_char p_data_binary[]     PROGMEM = "data: !binary |\n";

prog_char p_fileinfo[]        PROGMEM = "FILEINFO";
prog_char p_config_file[]     PROGMEM = "CONFIG.YML";

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

/** 
 * @brief measurement times will take this value as a reference
 */
unsigned long ref_millis;

/** @brief function launched when device boots up */
void setup() {
  
   buffer_count = 0;
   new_buffer   = false;
  
   Serial.begin(9600);

   if(!sd.init(SPI_FULL_SPEED, SS_PIN)) printP(sd_not_found);

   config.setBtHandler( &comm );
   config.setSensorList(&sensors);
   
   //callbacks
   comm.GET = onGetRequest;
   comm.DELETE = onDeleteRequest;
   comm.POST = onPostRequest;
   comm.onFileReceived = onFileReceived;
   comm.onBTCommandResponse  = onBtResponse;   
   
   config.onDataConfigChanged = configChanged;
   config.onTick = tick;

   //comm.initialize();
   Serial.flush();
   config.readFromFile();
      
}

/** @brief Main loop: sends read chars to Comm and writes Sensor's buffer to SD */
void loop() {
    
   //handle millis overflow
   if(millis()<ref_millis) createDataFile();
  
   //check if a new data file has to be created
   if(new_buffer && buffer_count==0) {
       new_buffer=false;
       createDataFile();
   }
   
   //write buffer to SD
   if(buffer.count()>0) {
       if(data_file.isOpen()) {
         char* l = buffer.pop();
         if(new_buffer) buffer_count--;

         data_file.println(l);
         data_file.sync();
         free(l);    
       }
   }

   config.loop();
   comm.loop();
}

/**
 * @brief function executed periodically to get measurements from sensors into the buffer
 */
void tick() {
   char *l;
   Sensor *s;
   sensors.goToFirst(); 
   while ( s = sensors.getNext() ) {
      if(s->tick()) {
          l = (char*) malloc( sizeof(char) * 20);
          s->getDataLine(l,ref_millis);
          buffer.push(l);
      }
   }
}

/** @brief called when a POST request is received by Comm
 *  @details Instructs comm to write whatever is received to a temporary file on the SD card.
 *  Only the configuration file can be written, therefore all other requests are ignored.
 *  @param subject of the POST action
 */
byte onPostRequest(char *subject) {
    if(strcmpP(subject,p_config_file))  {
        SdFile *sdf = new SdFile();
        sdf->open("CONFIG.TMP", O_WRITE | O_TRUNC | O_CREAT );
        if(!sdf->isOpen()) return false;
        comm.saveToFile( sdf );
        return MSG_200;
    } else {
        return MSG_403;   
    }
}

/** @brief called when a file has been received via POST 
 *  @details Only the configuration can be received, so there's no need to
 *  check for filenames */
void onFileReceived() {
    sd.remove("CONFIG.YML");
    sd.rename("CONFIG.TMP","CONFIG.YML");
    config.readFromFile();
}

/** @brief called when a GET request is received by Comm
 *  @details When the current data file is requested, a new data file is opened in
 *  order to make sure that not two versions of the same file can exist. In this way, there's
 *  no need to check for changes in files, because once a file is requested, it's not longer modified.<br/>
 *  On the other hand, when FILEINFO is requested, as the corresponding resource has to be created,
 *  
 *  @see loop
 *  @see Comm::sendFile
 *  @see Comm::startByteTransmission
 *  @param subject of the GET action
 */
byte onGetRequest(char *subject) {

    if(strcmpP(subject,p_fileinfo)) {
        comm.getLine = getLsLine;
        comm.startStringTransmission();
        return MSG_200;
    }else{
        char datafilename[12];
        data_file.getFilename(datafilename);
        if(strcmp(subject, datafilename )==0) createDataFile();
        if(sd.exists(subject)) {
            SdFile *sdf = new SdFile();
            sdf->open(subject);
            if(sdf->isOpen()) {
                comm.sendFile( sdf );
                return MSG_200;
            } else {
                return MSG_500;
            }
        }else{
            return MSG_404;
        }
    }
}

/** @brief returns a line of the file list 
 *  @details This function returns a YAML-formatted file list, one line
 *  at a time.
 */
char* getLsLine() {
  
   sd.ls();
   comm.getLine = NULL;
    
}

/** @brief removes a file from the SD 
 *  @returns <br/>
 *  MSG_200 if the file is successfully deleted<br/>
 *  MSG_403 if the configuration or the current datafile is requested.<br/>
 *  MSG_404 if the file does not exist<br/>
 *  MSG_500 if the file cannot be deleted for an unknown reason
 */
byte onDeleteRequest(char *subject) {

    char datafilename[12];
    data_file.getFilename(datafilename);
    
    if( strcmp(datafilename,subject)==0) return MSG_403;
    if( strcmpP(datafilename, p_config_file) ) return MSG_403;
    
    if(sd.exists(subject)) {
        if(sd.remove(subject))
           return MSG_200;
        else
           return MSG_500;
    } else {
        return MSG_404;
    }
}

/** @brief dispatches BTComm's callback to Config */
void onBtResponse(boolean r) {
   config.BTCommandResponse(r);
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
  while ( s = sensors.getNext() ) s->start();
  
}

/**
 * @brief Creates a new data file
 */
void createDataFile() {
    byte file_number=0;
    char file_name[12];
    Sensor *s;

    if(data_file.isOpen()) data_file.close();
    
    //find filename

    file_number = EEPROM.read(EEPROM_NFILE);
    do {
        sprintf(file_name,"%d.YML",file_number++);
    } while(sd.exists(file_name));
    EEPROM.write(EEPROM_NFILE,file_number);

    data_file.open(file_name, O_CREAT | O_WRITE | O_APPEND);
    if(!data_file.isOpen()) {printP(file_not_found);Serial.println(file_name);}
    
    //write headers to file
    sdprintP(&data_file, p_device_uid);
    data_file.print(":");
    data_file.println(EEPROM.read(EEPROM_UID));
    
    sdprintP(&data_file, p_device_name);
    data_file.print(":");
    data_file.println(config.device_name);

    sdprintP(&data_file, p_sampling_period);
    data_file.print(":");
    data_file.println(config.sampling_period);

    //@TODO get real timestamp
    sdprintP(&data_file, p_timestamp);
    data_file.print(":");
    data_file.println("2012-08-12 12:24:10");
    
    sdprintP(&data_file, p_sensors);
    data_file.print(":\n");

    data_file.sync();

    sensors.goToFirst();
    while ( s = sensors.getNext() ) {
        s->writeDescriptionToSd(&data_file);
    }
    sdprintP(&data_file, p_data_binary);
    data_file.sync();
    ref_millis = millis();
}
