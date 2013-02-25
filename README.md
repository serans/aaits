AAITS - Arduino-Android Integrated Telemonitoring System
===

AAITS is a complete telemonitoring system. The system consists on a serie of arduino-based sensor prepared to obtain 
measurements from its environment. These measurements are stored on a internal SD which can be later analyzed by
physically extracting the SD card from the node and uploading it to the AAITS server, or by using an android device
to transfer the data from the node into an android via bluetooth without interrupting the data adquisition.

All data adquisition parameters are configurable, again by either writing a YAML configuration file by hand on the node's
SD card or through an user-friendly android interface using bluetooth. In the latter case, the data adquisition task is
not interrupted, and the node will continue to work with the new configuration when it is uploaded.

This repository contains the full source code of the project:
 * Sensor Nodes: C and C++
 * Android App: Java
 * Web Server App: Java and Scala (Play! Framework 2.0)

Required hardware:
* Sensor nodes:
 * Arduino board
 * SD card reader
 * Serial Bluetooth interface
* Server:
 * Java web server
* Smartphone:
 * Android device with Bluetooth capabilities

This is a purely experimental project and it's not intended for use in production environments. If you need a professional
telemonitoring solution you should probably check http://www.libelium.com/products/waspmote
