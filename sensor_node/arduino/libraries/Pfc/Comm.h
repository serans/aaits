/** @file Comm.h 
 *  
 *  @author Miguel Hermo Serans
 */
#ifndef COMM_H
#define COMM_H

#include <Arduino.h>
#include "PUtils.h"

#define COMM_MAX_STRING_LENGTH 13

/**
 * @defgroup COMM_STATE Communications state
 *
 * Represents the different states at which the communications can be at
 * @{
 */
#define COMM_LISTENING      0
#define COMM_RECEIVING_FILE 1
#define COMM_SENDING_FILE   2
#define COMM_SENDING_STRING 3
/** @} */

/** @defgroup REQ_STATE Request interpretation state
 *
 *  Represent the different states of the interpretation of a request
 * @{ */
#define REQ_ROOT          0
#define REQ_GET           1
#define REQ_GET_FILEINFO  2
#define REQ_POST          3
#define REQ_DELETE        4
/** @} */

/**
 * @defgroup RESPONSE_NUMBER Response codes index
 *
 * Indexes to acces the different response codes strings
 * @see msg_table
 * 
 * @{
 */
/** @brief OK */
#define MSG_200 0
/** @brief Bad Request */
#define MSG_400 1
/** @brief Not Found */
#define MSG_403 2
/** @brief Not Found */
#define MSG_404 3
/** @brief Internal Server Error */
#define MSG_500 4
/** @} */

#define EOT 4

/** @brief Handles sensor node's communications
 */
class Comm {
    protected:
        byte comm_state;
        byte req_state;
        char recv_str[COMM_MAX_STRING_LENGTH];
        void interpretateRequest(char *req);
        SdFile *file;
      
    public:
      Comm() {
          comm_state = COMM_LISTENING;
          req_state = REQ_ROOT;
      }
     
     virtual void loop();
     
     virtual void readChar(char c);
     virtual void sendResponseCode( byte response_code );
     void sendFile  ( SdFile *file );
     void saveToFile( SdFile *file);
     void startStringTransmission();
     void endTransmission();
     
     /** @defgroup COMM_CALLBACKS callback functions 
      *  @{ */
     byte  (*GET)    (char *subject);
     byte  (*POST)   (char *subject);
     byte  (*DELETE) (char *subject);
     void  (*onFileReceived) (void);
     char* (*getLine) (void);
     /** @} */
      
};

#endif
