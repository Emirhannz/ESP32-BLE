<p align="center">
  <img src="https://img.shields.io/badge/platform-ESP32-blue" />
  <img src="https://img.shields.io/badge/mobile-Java-green" />
  <img src="https://img.shields.io/badge/status-Developing-yellow" />
</p>

<h1 align="center">📡 ESP32-BLE Alarm System</h1>

> ⚠️ **BLE-powered alert system triggered by electrical current — IoT-enabled mobile notification integration**

---

## 🚀 About the Project

**ESP32-BLE** is a smart alarm system that sends a Bluetooth Low Energy (BLE) log message whenever it detects electrical current. These messages are received by a mobile application and shown as **real-time notifications** to the user.

The project consists of two main components:
- 🔌 **ESP32 Embedded System** (programmed via Arduino IDE)
- 📱 **Android Mobile Application** (developed with Java)

---

## 🔌 ESP32 Hardware Structure

When current is detected, the ESP32 transmits a log message via BLE. This can be viewed using apps like nRF Connect.

- Firmware is written and uploaded using **Arduino IDE**
- Sends BLE log messages upon trigger
- Simple circuit: current detection directly triggers BLE signal

---

## 📱 Android Application

The mobile app is built with Java in Android Studio and listens for BLE messages sent by the ESP32, displaying them as **local notifications**.

### Technical Details:
- **IDE:** Android Studio
- **Language:** Java
- **Minimum API Level:** API 21 (Android 5.0 Lollipop)
- **Gradle DSL:** Groovy

📌 *API 21 was chosen to support a wide range of devices compatible with BLE.*

---

## 🛠️ Features Under Development

- [x] Detection of BLE messages from ESP32
- [x] Display of messages as local notifications
- [ ] **Simultaneous notifications to multiple users** *(In progress)*

---

## 🎯 Use Cases

- ⚡ Power outage or restoration alerts
- 🏭 Monitoring electrical devices in industrial systems
- 📦 Smart home automation triggers
- 👨‍🔧 Remote technician safety notifications

---

## 🔧 Setup

### For ESP32:
1. Download and install Arduino IDE
2. Install the ESP32 board package via Board Manager
3. Upload the embedded firmware to the ESP32

### For Android App:
1. Open the project in Android Studio
2. Ensure necessary permissions are set in `AndroidManifest.xml` (Bluetooth, Notification)
3. Test on a real device *(BLE does not work on emulators)*

---

## 🔮 Future Plans

- 🔔 Push notifications to multiple users simultaneously
- ☁️ Cloud integrations (e.g., Firebase, MQTT)
- 📊 BLE message history logging and analysis
- 🔐 User authentication and authorization system

---

## 🤝 Want to Contribute?

Pull requests, suggestions, and feedback are welcome. Anyone interested is encouraged to contribute to the project.

---

> **ESP32-BLE Alarm System** combines hardware and software to deliver real-time alerts. It’s a scalable starting point for those looking to merge IoT technology with mobile applications.
