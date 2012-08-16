#ifndef BTCOMM_H
#define BTCOMM_H

#include <Arduino.h>
#include <avr/pgmspace.h>
#include "Comm.h"


/** @file BTComm.h 
 *
 *  @author Miguel Hermo Serans
 */

class Config;

/** 
 * @brief Comm implementation for Bluetooth Grove 0.9
 *
 * Comm defines a generic communication interface, but specific details must be implemented in subclasses. <br/>
 * Particularly, the configuration of the Bluetooth Grove 0.9 Bluetooth interface is handled here.
 *
 * @see http://www.seeedstudio.com/wiki/Grove_-_Serial_Bluetooth
 */
class BTComm: public Comm {
    private:
      boolean awaiting_bt_response;
      byte response_i;
      
    public:
      
      BTComm():Comm() {
          awaiting_bt_response = false;
      }

      //callbacks
      void (*onBTCommandResponse)(boolean r);

      void readChar(char c);
      void sendBTCommand(char* command);
      boolean checkOK(char c);
      
      void setDeviceName(char* name);
      void setPinCode(char* code);
      
      char device_name[30];
      char pin_code[6];
};


#endif
