#include <Arduino.h>
#include <avr/pgmspace.h>
#include <SdFile.h>

#ifndef PUTILS_H
#define PUTILS_H

void copyP( prog_char p[], char dest[]);
void printP( prog_char p[] );
void sdprintP( SdFile *f, prog_char p[] );
boolean strcmpP( char *ram, prog_char prog[] );

#endif
