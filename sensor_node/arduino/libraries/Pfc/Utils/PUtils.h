#include <Arduino.h>
#include <avr/pgmspace.h>
#include <SdFile.h>

#ifndef PUTILS_H
#define PUTILS_H

void printP( prog_char p[] );
void sdprintP( SdFile *f, prog_char p[] );
boolean strcmpP( char *ram, prog_char prog[] );

#endif
