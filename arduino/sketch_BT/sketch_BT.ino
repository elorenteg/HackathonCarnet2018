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
 *  5V - 2
 *  X  - 10kOhm + GND
 */
#include <SoftwareSerial.h>   // Incluimos la librería  SoftwareSerial  
SoftwareSerial BT(10,11);    // Definimos los pines RX y TX del Arduino conectados al Bluetooth

const int buttonPin = 2;
int buttonState = 0;

void setup(){
  // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);
  
  BT.begin(9600);       // Inicializamos el puerto serie BT que hemos creado
  Serial.begin(9600);   // Inicializamos el puerto serie

  Serial.println("Start BT, Name is HC-06, MAC is 20:15:10:20:04:46");
}

void manageBT() {
  // Si llega un dato por el puerto BT se envía al monitor serial
  if(BT.available()){
    Serial.write(BT.read());
  }

  // Si llega un dato por el monitor serial se envía al puerto BT
  if(Serial.available()){
     BT.write(Serial.read());
  }
}
 
void loop(){
  manageBT();

  // read the state of the pushbutton value:
  buttonState = digitalRead(buttonPin);

  // check if the pushbutton is pressed. If it is, the buttonState is HIGH:
  if (buttonState == HIGH) {
    BT.write("Apretado\r");
  }
}
