#include <SoftwareSerial.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include "ELMduino.h"

SoftwareSerial mySerial(17, 16);  
#define ELM_PORT mySerial
#define DEBUG_PORT Serial

ELM327 myELM327;

const char* ssid = ""; //your ssid
const char* password = ""; //your password

WiFiClient client;
HTTPClient http;

enum obd_state_t {
  ENG_RPM,
  SPEED,
  MANIFOLD_PRESSURE,
  INTAKE_AIR,
  SHORT_TERM_FUEL_TRIM,
  COOLANT_TEMP,
  END_OF_DATA
};

obd_state_t obd_state = ENG_RPM;

uint32_t speed = 0;
uint32_t rpm = 0;
float MAP = 0.0;
float IAT = 0.0;
float STFT = 0.0;
float coolant_temp = 0.0;
float fuelRate = 0.0;
float fuelConsumption = 0.0;

#define R 287.0  
#define FUEL_DENSITY 745.0  
#define STOICH_AFR 14.7

void setup() {
#if LED_BUILTIN
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LOW);
#endif

  Serial.begin(115200);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to Wi-Fi...");
  }
  Serial.println("Connected to Wi-Fi!");

  ELM_PORT.begin(38400);

  Serial.println("Attempting to connect to ELM327...");

  if (!myELM327.begin(ELM_PORT, true, 2000)) {
    Serial.println("Couldn't connect to OBD scanner");
    while (1);
  }

  Serial.println("Connected to ELM327");
}

void loop() {
  switch (obd_state) {
    case ENG_RPM: {
        rpm = myELM327.rpm();

        if (myELM327.nb_rx_state == ELM_SUCCESS) {
            DEBUG_PORT.print("rpm: ");
            DEBUG_PORT.println(rpm);
            obd_state = SPEED;
        } else if (myELM327.nb_rx_state != ELM_GETTING_MSG) {
            myELM327.printError();
            obd_state = SPEED;
        }

        break;
    }

    case SPEED: {
        speed = myELM327.kph();

        if (myELM327.nb_rx_state == ELM_SUCCESS) {
            DEBUG_PORT.print("Speed: ");
            DEBUG_PORT.println(speed);
            obd_state = MANIFOLD_PRESSURE;
        } else if (myELM327.nb_rx_state != ELM_GETTING_MSG) {
            myELM327.printError();
            obd_state = MANIFOLD_PRESSURE;
        }

        break;
    }

    case MANIFOLD_PRESSURE: {
        MAP = myELM327.manifoldPressure();

        if (myELM327.nb_rx_state == ELM_SUCCESS) {
            DEBUG_PORT.print("MAP (kPa): ");
            DEBUG_PORT.println(MAP);
            obd_state = INTAKE_AIR;
        } else if (myELM327.nb_rx_state != ELM_GETTING_MSG) {
            myELM327.printError();
            obd_state = INTAKE_AIR;
        }

        break;
    }

    case INTAKE_AIR: {
        IAT = myELM327.intakeAirTemp();

        if (myELM327.nb_rx_state == ELM_SUCCESS) {
            DEBUG_PORT.print("Intake Air Temp (°C): ");
            DEBUG_PORT.println(IAT);
            obd_state = SHORT_TERM_FUEL_TRIM;
        } else if (myELM327.nb_rx_state != ELM_GETTING_MSG) {
            myELM327.printError();
            obd_state = SHORT_TERM_FUEL_TRIM;
        }

        break;
    }

    case SHORT_TERM_FUEL_TRIM: {
        STFT = myELM327.shortTermFuelTrimBank_1();

        if (myELM327.nb_rx_state == ELM_SUCCESS) {
            DEBUG_PORT.print("Short-Term Fuel Trim (%): ");
            DEBUG_PORT.println(STFT);
            obd_state = COOLANT_TEMP;
        } else if (myELM327.nb_rx_state != ELM_GETTING_MSG) {
            myELM327.printError();
            obd_state = COOLANT_TEMP;
        }

        break;
    }

    case COOLANT_TEMP: {
        coolant_temp = myELM327.engineCoolantTemp();

        if (myELM327.nb_rx_state == ELM_SUCCESS) {
            DEBUG_PORT.print("Coolant Temperature (°C): ");
            DEBUG_PORT.println(coolant_temp);
            obd_state = END_OF_DATA;
        } else if (myELM327.nb_rx_state != ELM_GETTING_MSG) {
            myELM327.printError();
            obd_state = END_OF_DATA;
        }

        break;
    }

    case END_OF_DATA: {
      double IAT_K = IAT + 273.15;
      double VE = 0.85;         
      double displacement = 1.4;  //change if needed
      double airMassFlow = (MAP * rpm * VE * displacement) / (R * IAT_K);
      double actualAFR = STOICH_AFR * (1 + STFT / 100);
      double fuelMassFlow = airMassFlow / actualAFR;
      fuelRate = (fuelMassFlow / FUEL_DENSITY) * 3600;
      double speed_kph = speed;  
      fuelRate = fuelRate * 6;
      if (speed_kph > 0) {  
        fuelConsumption = (fuelRate * 100) / speed_kph;
      } else {
        fuelConsumption = fuelRate;
      }

      Serial.print("Fuel Rate (L/hr): ");
      Serial.println(fuelRate, 2);
      Serial.print("Fuel Consumption (L/100km): ");
      Serial.println(fuelConsumption, 2);

      sendDataToBackend(speed, rpm, fuelConsumption, coolant_temp);

      

        delay(1000);
        obd_state = ENG_RPM;
        break;
    }
}

}


void sendDataToBackend(uint32_t speed, uint32_t rpm, float fuelConsumption, float coolantTemp) {
  if (WiFi.status() == WL_CONNECTED) {
    http.begin(client, "http://172.20.10.2:8080/add/676E4F6E"); //http://serverip:port/add/VIN
    http.addHeader("Content-Type", "application/json");

  String jsonPayload = "{"
                     "\"speed\": " + String(speed) + 
                     ", \"rpm\": " + String(rpm) +  
                     ", \"coolantTemp\": " + String(round(coolantTemp)) +  
                     ", \"fuelRate\": " + String(fuelConsumption, 1) +  
                     ", \"car\": {\"vin\": \"676E4F6E\"}"
                     "}";

    int httpResponseCode = http.POST(jsonPayload);

    if (httpResponseCode > 0) {
      Serial.println("HTTP Response code: " + String(httpResponseCode));
    } else {
      Serial.println("Error sending POST request: " + String(httpResponseCode));
    }

    http.end();
  } else {
    Serial.println("Error: Wi-Fi not connected");
  }
}
