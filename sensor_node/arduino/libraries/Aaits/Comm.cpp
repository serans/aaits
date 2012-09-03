#include "Comm.h"
#include "PUtils.h"
#include <avr/pgmspace.h>

/** @brief stores protocol name */
extern prog_char msg_protocol_name[] PROGMEM = "HTTP/1.1";

/**
 * @defgroup RESPONSE_STRINGS Response codes
 *
 * Strings containing the different response codes. Stored
 * in flash memory to save RAM.
 * 
 * @{
 */
extern prog_char msg_200[] PROGMEM = "200 OK";
extern prog_char msg_400[] PROGMEM = "400 Bad Request";
extern prog_char msg_403[] PROGMEM = "403 Forbidden";
extern prog_char msg_404[] PROGMEM = "404 Not Found";
extern prog_char msg_500[] PROGMEM = "500 Internal Server Error";

/** @} */

/** @brief array of response strings 
 *
 *  @see RESPONSE_STRINGS
 *  @see RESPONSE_NUMBER
 */
extern prog_char *msg_table[] = { msg_200, msg_400, msg_403, msg_404, msg_500 };


extern prog_char req_GET[] PROGMEM    = "GET";
extern prog_char req_POST[] PROGMEM   = "POST";
extern prog_char req_DELETE[] PROGMEM = "DELETE";

extern prog_char req_FILEINFO[] PROGMEM = "FILEINFO";

/** @brief main loop */
void Comm::loop() {
    
    if( comm_state == COMM_SENDING_FILE ) {
        if(file->isOpen()) {
            char c = file->read();
            if(c<0) {
                file->close();
                
                delete(file);
                endTransmission();
            } else {
                Serial.print(c);
            }
        }
    } 
    
    if( comm_state == COMM_SENDING_STRING ) {
        if( getLine == NULL) endTransmission();
        else {
            Serial.print(getLine());
        }
    } 

    if(Serial.available()) {
      readChar(Serial.read());
    }
}

/** @brief reads a char from Serial and acts depending on the current state
 *  @param c char from the request
 *  @see interpretateRequest
 */
void Comm::readChar( char c ) {
    
    if(comm_state==COMM_LISTENING) {
        if(c==' ' || c=='\n' || c=='\r' ) {
          //word completed: interpretate
          interpretateRequest(recv_str);
          recv_str[0]='\0';
        } else {
          //add c to word
          if(strlen(recv_str)<COMM_MAX_STRING_LENGTH-1) {
              byte len = strlen(recv_str);
              recv_str[ len ] = c;
              recv_str[ len+1 ] = '\0';
          }
        }
    }else if(comm_state==COMM_RECEIVING_FILE) {
      if(file->isOpen()) {
            if(c==EOT) {
                file->sync();
                file->close();
                delete file;
                sendResponseCode(MSG_200);
                endTransmission();
                comm_state=COMM_LISTENING;
                onFileReceived();
            } else {
                file->write(c);
            }
        } else {
          delete file;
          sendResponseCode(MSG_500);
          endTransmission();          
          comm_state=COMM_LISTENING;
        }
    }
}

/** @brief interpretates request
 *  @details Interpretating requests are very simple, the structure is always: ACTION SUBJECT
 *  (with a whitespace separating both).
 *  @param req string containing the next word of the request
 */
void Comm::interpretateRequest(char *req) {
    byte response;
    
    if(req_state == REQ_ROOT) {
        //Determinate ACTION
        
        if( strcmpP( req, req_GET) ) {
            req_state = REQ_GET;
        } else if(strcmpP( req, req_POST )) {
            req_state = REQ_POST;
        } else if(strcmpP( req, req_DELETE )) {
            req_state = REQ_DELETE;
        }
        
    }else {
        //Determinate SUBJECT and launch callbacks
        
        if(req_state == REQ_GET) {
            if(this->GET==NULL)     {
                sendResponseCode(MSG_500);
                endTransmission();
            } else {
                response = this->GET(req);
                sendResponseCode(response);
                if(response!=MSG_200) endTransmission();
            }
        }else 
        
        if(req_state == REQ_POST) {
            if(this->POST==NULL)     {
                sendResponseCode(MSG_500);
                endTransmission();
            } else {
                response = this->POST(req); 
                if(response!=MSG_200) {
                    sendResponseCode(response);
                    endTransmission();
                }
                //response will be sent AFTER receiving the data
            }
        }else 
        
        if(req_state == REQ_DELETE) {
            if(this->DELETE==NULL)     {
                sendResponseCode(MSG_500);
                endTransmission();
            } else {
                sendResponseCode(this->DELETE(req));
                endTransmission();
            }
        }
        req_state = REQ_ROOT;        
    }
}

/** @brief finishes a transmission */
void Comm::endTransmission() {
    Serial.write((byte) EOT);
    comm_state = COMM_LISTENING;
}

/** @brief Starts the transmission of a string 
 *  @details When a the transmission of a string is initiated, Comm will launch
 *  getLine callback to retrieve the string to send, and will continue doing
 *  so until the callback is deleted
 *  @see getLine
 */
void Comm::startStringTransmission() {
    comm_state = COMM_SENDING_STRING;
}

/**
 * @brief Sends a HTTP response
 */
void Comm::sendResponseCode( byte response_code ) {   
    printP(msg_protocol_name);
    Serial.print(" ");
    printP(msg_table[response_code]);    
    Serial.println();
    Serial.println();
}

/** @brief starts sending a file.
 *  @details In order to prevent blocking the execution of the main program during file send,
 *  this functions sets up the file to be sent one character at a time in the loop function
 *  @param file to be sent
 *  @see loop
 */
void Comm::sendFile(SdFile *file) {
    comm_state = COMM_SENDING_FILE;
    this->file = file;
}

/** @brief sets the file to which the received info is to be saved
 *
 *  @param file to save results
 *  @see readChar
 */
void Comm::saveToFile( SdFile *file) {
    comm_state = COMM_RECEIVING_FILE;
    this->file = file;
}

