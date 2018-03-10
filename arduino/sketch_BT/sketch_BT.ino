#include <SoftwareSerial.h>   // Incluimos la librería  SoftwareSerial  
SoftwareSerial BT(10,11);    // Definimos los pines RX y TX del Arduino conectados al Bluetooth
 
void setup(){
  BT.begin(9600);       // Inicializamos el puerto serie BT que hemos creado
  Serial.begin(9600);   // Inicializamos el puerto serie
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
}
