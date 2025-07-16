<p align="center">
  <img src="https://img.shields.io/badge/platform-ESP32-blue" />
  <img src="https://img.shields.io/badge/mobile-Java-green" />
  <img src="https://img.shields.io/badge/status-Developing-yellow" />
</p>

<h1 align="center">📡 ESP32-BLE Alarm Sistemi</h1>

> ⚠️ **Elektrik akımına duyarlı BLE uyarı sistemi — IoT destekli mobil bildirim entegrasyonu**

---

## 🚀 Proje Hakkında

**ESP32-BLE**, herhangi bir elektrik akımı algılandığında Bluetooth Low Energy (BLE) üzerinden log mesajı göndererek, bu mesajları mobil uygulama aracılığıyla **gerçek zamanlı bildirim** olarak kullanıcıya ileten akıllı bir alarm sistemidir.

Proje iki temel bileşenden oluşur:
- 🔌 **ESP32 Gömülü Sistemi (Arduino IDE ile programlanmıştır)**
- 📱 **Android Mobil Uygulama (Java ile geliştirilmiştir)**

---

## 🔌 ESP32 Donanım Yapısı

ESP32, akım algılandığında BLE aracılığıyla log mesajı üretir. Örneğin, nRF Connect gibi BLE tarayıcı uygulamaları bu mesajı görebilir.

- Gömülü yazılım **Arduino IDE** üzerinden ESP32’ye yüklenmiştir
- BLE üzerinden log mesajı yayınlanır
- Devre bağlantısı basit: akım algılandığında BLE tetiklenir

---

## 📱 Android Uygulama

Android Studio üzerinde Java ile geliştirilen uygulama, ESP32'nin gönderdiği BLE log mesajlarını dinler ve **mobil cihazda bildirim olarak gösterir.**

### Teknik Detaylar:
- **Geliştirme Ortamı:** Android Studio
- **Programlama Dili:** Java
- **Minimum API Seviyesi:** API 21 (Android 5.0 Lollipop)
- **Gradle Yapılandırması:** Groovy DSL

📌 *API 21, BLE teknolojisinin yaygın desteklendiği en geniş aralıktaki sürüm olduğu için başlangıç noktası olarak tercih edilmiştir.*

---

## 🛠️ Şu Anda Geliştirilen Özellikler

- [x] ESP32 tarafından gönderilen BLE mesajının algılanması
- [x] Mobil cihazda lokal bildirim olarak gösterilmesi
- [ ] **Birden fazla kullanıcıya aynı anda bildirim iletimi** *(Geliştirme aşamasında)*

---

## 🎯 Kullanım Senaryoları

- ⚡ Elektrik kesintisi ya da geri gelmesi uyarısı
- 🏭 Endüstriyel sistemlerde cihaz izleme
- 📦 Akıllı ev otomasyon sistemlerinde uyarı tetikleme
- 👨‍🔧 Uzaktan çalışan teknisyenler için güvenlik bildirimleri

---

## 🔧 Kurulum

### ESP32 için:
1. Arduino IDE'yi indirip kurun
2. ESP32 kart paketini yükleyin (Board Manager üzerinden)
3. Gömülü kodu ESP32'ye yükleyin

### Mobil Uygulama için:
1. Android Studio ile projeyi açın
2. `AndroidManifest.xml` içinde Bluetooth ve Bildirim izinlerini kontrol edin
3. Gerçek cihazla test edin *(BLE, emülatörde çalışmaz)*

---

## 🔮 Gelecek Planlar

- 🔔 Çoklu kullanıcıya eş zamanlı bildirim gönderme
- ☁️ Firebase/MQTT gibi bulut entegrasyonları
- 📊 BLE mesaj geçmişi kaydı ve grafiksel analiz
- 🔐 Kullanıcı yetkilendirme sistemi

---

## 🤝 Katkı Sağlamak İster misiniz?

Pull request'ler, öneriler ve geri bildirimler memnuniyetle karşılanır. İlgili herkes bu projeye katkı sağlayabilir.


---

> **ESP32-BLE Alarm Sistemi**, donanım ve yazılımı bir araya getirerek gerçek zamanlı uyarı sistemi sunar. Bu proje, IoT'nin gücünü mobil teknolojilerle buluşturmak isteyen herkes için güçlü ve ölçeklenebilir bir başlangıçtır.
