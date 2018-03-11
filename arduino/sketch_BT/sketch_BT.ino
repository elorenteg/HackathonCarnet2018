/* This sketch communicates a bunch of servos/leds and Bluetooth with an Android Device.
 *  Tested in an Arduino Nano ATmega168
 *  
 *  BT  -  Arduino
 *  5V  -  5V
 *  GND -  GND
 *  RX  -  10
 *  TX  -  11
 *  
 *  Button - Arduino
 *  When pressed, communicates the 5V to the 2 pin of the Arduino.
 *  (5V) - (2)
 *  ()   - (10kOhm + GND)
 *  
 *  Shock - Arduino
 *  When moved, change the D0 from HIGH to LOW (Or inverse)
 *  5V  - Vcc
 *  GND - GND
 *  D0 - D6  
 */
#include <SoftwareSerial.h>   // Incluimos la librería  SoftwareSerial  
SoftwareSerial BT(10,11);    // Definimos los pines RX y TX del Arduino conectados al Bluetooth

const int shockAlarmTime = 250; 
unsigned long lastShockTime;
boolean bAlarm = false;

const int buttonPin = 2;
int buttonState = 0;

const int shockPin = 6;
int shockState = 0;

const int ledPin = 3;

const int motorPin = 4;

void setup(){
  pinMode(buttonPin, INPUT);
  pinMode(shockPin, INPUT);
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, HIGH);

  pinMode(motorPin, OUTPUT);
  digitalWrite(motorPin, HIGH);
  delay(1000);
  digitalWrite(motorPin, LOW);
  
  BT.begin(9600);       // Inicializamos el puerto serie BT que hemos creado
  Serial.begin(9600);   // Inicializamos el puerto serie

  Serial.println("Start BT, Name is HC-06, MAC is 20:15:10:20:04:46");
}

void loop(){
  // Gestion del BT
  // Si llega un dato por el puerto BT se envía al monitor serial
  if(BT.available()){
    Serial.write(BT.read());
  }

  // Si llega un dato por el monitor serial se envía al puerto BT
  if(Serial.available()){
     BT.write(Serial.read());
  }

  // Gestion del boton
  buttonState = digitalRead(buttonPin);
  if (buttonState == HIGH) {
    BT.write("BOTON_APRETADO\r");
  }

  // Gestion del sensor de shock
  shockState = digitalRead (shockPin);
  if (shockState == LOW) {
    lastShockTime = millis();
    if (!bAlarm) {
      BT.write("SHOCK_AGITADO\r");
      bAlarm = true;
    }
  } else {
    if((millis()-lastShockTime) > shockAlarmTime && bAlarm) {
      bAlarm = false;
    }
  }
}
