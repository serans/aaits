#include "AnalogSensor.h"

AnalogSensor::AnalogSensor() {
    Sensor::Sensor();
    setName("analog_sensor");
    setUnits("5/1023 Volts");
};

boolean AnalogSensor::action() {
    measured_value  = analogRead(pin);
    measured_millis = millis();
    return true;
};
