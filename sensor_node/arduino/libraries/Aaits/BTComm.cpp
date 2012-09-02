#include "BTComm.h"
#include "Config.h"
     
/**
* @brief reads configuration responses from the BT module
* @param c input char.
*/
void BTComm::readChar(char c) {

    if(awaiting_bt_response==true) {
        if(checkOK(c)) {
            Serial.flush();
            Serial.println("OK!");
            awaiting_bt_response = false;
            if(onBTCommandResponse!=NULL) onBTCommandResponse(true);
        }
    } else {
        Comm::readChar(c);
    }
}
      
/**
* @brief Sends command to BT Module
*/
void BTComm::sendBTCommand(char *command) {
  
//  Serial.println("<c>");
  Serial.println(command);
//  Serial.println("</c>");
  awaiting_bt_response = true;
  response_i=0;

};

/**
* @brief Checks for an OK response from the bluetooth module.
*
* Bluetooth Grove responds with an OK message to every config
* command sent to it.
*/
boolean BTComm::checkOK(char c) {
  char bt_response_ok[3]="OK";
  if(c == bt_response_ok[response_i]) response_i++;
  if(response_i==2) { 
      response_i=0;
      return true;
  }
  return false;
}


void BTComm::initialize() {
   Serial.println("\r\n+STWMOD=0\r\n");
   delay(2000);
   Serial.println("\r\n+STNA=boya\r\n");
   delay(2000);
   Serial.println("\r\n+STAUTO=0\r\n");
   delay(2000);
   Serial.println("\r\n+STOAUT=1\r\n");
   delay(2000);
   Serial.println("\r\n+INQ=1\r\n");
   delay(5000);
}

void BTComm::setDeviceName(char *name) {
  char command[50];
  
  strcpy(device_name,name);
  sprintf(command, "\r\nSTNA=%s\r\n",device_name);
  sendBTCommand(command);
}

void BTComm::setPinCode(char *code) {
  char command[50];
  
  strcpy(pin_code,code);
  sprintf(command, "\r\nSTPIN=%s\r\n",pin_code);
  sendBTCommand(command);         
}
