#ifndef TransformFunction_h
#define TransformFunction_h

#include "Arduino.h"
#include "PUtils.h"

#define MAX_T_PARAM 4
#define T_NONE  0
#define T_POLY  1

/**
 *
 */
class TransformFunction {
    
    protected:
        float params[MAX_T_PARAM];
        byte n_params;
        byte type;
       
    public:
 
        TransformFunction(char *c) {
            type=T_NONE;
            n_params = 0;
            if(strcmp(c,"poly")==0) type=T_POLY;
        }
    
        TransformFunction() {
            n_params = 0;
            type = T_NONE;
        }
        
        void addParam(float p) {
            if(n_params>MAX_T_PARAM) return;
            
            params[n_params] = p;
            n_params++;
        };
        
        float poly(float x) {
            int i;
            float result=0;
            
            for(i=0; i<n_params; i++) { 
              result = params[i]*pow(x,i) + result;
            }
            
            return result;
        }
        
        float convert(float x) {
            switch(type) {
                case T_NONE:
                    return x;
                    break;
                case T_POLY:
                    return poly(x);
                    break;
            }
        };
        
        char *getClassName()   { 
            switch(type) {
                case T_NONE:
                    return "none";
                    break;
                case T_POLY:
                    return "poly";
                    break;
            }
        }
        float getParam(byte i) { return params[i]; }
        byte numParams()       {return n_params;}
        
};
#endif
