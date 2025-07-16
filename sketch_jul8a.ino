#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLE2902.h>

#define ALARM_PIN 12
bool alarmTriggered = false;

BLECharacteristic *pCharacteristic;
bool deviceConnected = false;

#define SERVICE_UUID        "NULL"
#define CHARACTERISTIC_UUID "NULL"

class MyServerCallbacks: public BLEServerCallbacks {
  void onConnect(BLEServer* pServer) {
    deviceConnected = true;
    Serial.println("📱 Telefon bağlandı.");
  }

  void onDisconnect(BLEServer* pServer) {
    deviceConnected = false;
    Serial.println("📴 Bağlantı kesildi.");
  }
};

void setup() {
  Serial.begin(115200);
  pinMode(ALARM_PIN, INPUT_PULLDOWN);  // pull-down aktif

  BLEDevice::init("ESP32_KAPI_ALARM");
  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  BLEService *pService = pServer->createService(SERVICE_UUID);
  pCharacteristic = pService->createCharacteristic(
                      CHARACTERISTIC_UUID,
                      BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY

                    );
  pCharacteristic->addDescriptor(new BLE2902());

  pService->start();
  pServer->getAdvertising()->addServiceUUID(SERVICE_UUID);
  pServer->getAdvertising()->start();
  Serial.println("📡 BLE kapı alarm sistemi hazır.");
}

void loop() {
  int state = digitalRead(ALARM_PIN);

  if (state == HIGH && !alarmTriggered && deviceConnected) {
    delay(30);  // filtreleme
    if (digitalRead(ALARM_PIN) == HIGH) {
      pCharacteristic->setValue("ALARM!");
      pCharacteristic->notify();
      Serial.println("🚨 ALARM tetiklendi → Bildirim yollandı.");
      alarmTriggered = true;
    }
  }

  if (state == LOW) {
    alarmTriggered = false;
  }

  delay(100);
}
