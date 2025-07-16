    package com.example.blealarmapp;
    import android.Manifest;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.bluetooth.BluetoothGatt;
    import android.bluetooth.BluetoothGattCallback;
    import android.bluetooth.BluetoothGattCharacteristic;
    import android.bluetooth.BluetoothGattDescriptor;
    import android.bluetooth.BluetoothGattService;
    import android.bluetooth.BluetoothManager;
    import android.bluetooth.le.ScanCallback;
    import android.bluetooth.le.ScanResult;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.os.Build;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;
    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.app.NotificationCompat;
    import androidx.core.content.ContextCompat;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.net.URLEncoder;
    import java.util.ArrayList;
    import java.util.List;
    import android.app.AlertDialog;
    import android.text.InputType;
    import java.util.HashSet;
    import java.util.Set;
    import java.io.OutputStream;
    import java.io.*;
    import org.json.JSONArray;
    import org.json.JSONObject;






    public class MainActivityJAVA extends AppCompatActivity {
    
        private void saveTelegramUser(String newUser) {
            SharedPreferences preferences = getSharedPreferences("TelegramUsers", MODE_PRIVATE);
            Set<String> users = preferences.getStringSet("userList", new HashSet<>());
    
            if (!newUser.startsWith("@")) {
                Toast.makeText(this, "Kullanıcı adı '@' ile başlamalı", Toast.LENGTH_SHORT).show();
                return;
            }
    
            if (users.contains(newUser)) {
                Toast.makeText(this, "Bu kullanıcı zaten kayıtlı.", Toast.LENGTH_SHORT).show();
                return;
            }
    
            users.add(newUser);
    
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet("userList", users);
            editor.apply();
    
            Toast.makeText(this, "Kaydedildi: " + newUser, Toast.LENGTH_SHORT).show();
        }
    
    
    
    
    
        private final int REQUEST_CODE_PERMISSIONS = 101;
        private final String[] REQUIRED_PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.POST_NOTIFICATIONS
        };
    
        BluetoothAdapter bluetoothAdapter;
        BluetoothGatt bluetoothGatt;
        TextView statusText, messageText;
        ListView deviceListView;
        ArrayAdapter<String> deviceListAdapter;
        List<BluetoothDevice> foundDevices = new ArrayList<>();
    
        final String CHANNEL_ID = "alarmChannel";
        final String TARGET_MESSAGE = "ALARM!";
    
        SharedPreferences prefs;
        LinearLayout telegramSection;
        EditText telegramInput;
        Button saveButton;
        String connectedDeviceName = "";
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main_java);
    
            statusText = findViewById(R.id.statusText);
            messageText = findViewById(R.id.messageText);
            deviceListView = findViewById(R.id.deviceList);
            deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            deviceListView.setAdapter(deviceListAdapter);
    
            telegramSection = findViewById(R.id.telegramSection);
            telegramInput = findViewById(R.id.telegramUserInput);
            saveButton = findViewById(R.id.saveTelegramUserBtn);
            prefs = getSharedPreferences("Settings", MODE_PRIVATE);
    
            saveButton.setOnClickListener(v -> {
                String inputUser = telegramInput.getText().toString().trim();
                if (!inputUser.startsWith("@")) {
                    Toast.makeText(this, "Lütfen @ ile başlayan kullanıcı adı girin", Toast.LENGTH_SHORT).show();
                    return;
                }
                prefs.edit().putString("telegram_user_" + connectedDeviceName, inputUser).apply();
                Toast.makeText(this, "Telegram kullanıcı adı kaydedildi", Toast.LENGTH_SHORT).show();
                telegramSection.setVisibility(View.GONE);
            });
    
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
    
            deviceListView.setOnItemClickListener((parent, view, position, id) -> {
                BluetoothDevice selectedDevice = foundDevices.get(position);
    
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1001);
                    return;
                }
    
                connectedDeviceName = selectedDevice.getName() != null ? selectedDevice.getName() : selectedDevice.getAddress();
    
                // 👇 KULLANICI ADI POPUP GÖSTERME BLOĞU
                String key = "telegram_user_" + connectedDeviceName;
                String existingUser = prefs.getString(key, null);
    
                if (existingUser == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Telegram kullanıcı adı");
                    builder.setMessage("Telegram kullanıcı adınızı @ ile yazın (örn: @kullaniciadi):");
    
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
    
                    builder.setPositiveButton("Kaydet", (dialog, which) -> {
                        String enteredUsername = input.getText().toString().trim();
                        if (!enteredUsername.startsWith("@")) {
                            Toast.makeText(this, "Lütfen @ ile başlayan geçerli bir ad girin", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        prefs.edit().putString(key, enteredUsername).apply();
                        Toast.makeText(this, "Kullanıcı adı kaydedildi", Toast.LENGTH_SHORT).show();
                    });
    
                    builder.setNegativeButton("İptal", (dialog, which) -> dialog.cancel());
                    builder.show();
                }
    
                // 👉 GATT bağlanma ve mesajlar
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    statusText.setText("Bağlantı reddedildi: İzin yok!");
                    return;
                }
    
                try {
                    statusText.setText("Bağlantı kuruluyor: " + selectedDevice.getName());
                    selectedDevice.connectGatt(MainActivityJAVA.this, false, gattCallback);
    
                    String savedUser = prefs.getString("telegram_user_" + connectedDeviceName, null);
                    if (savedUser == null) {
                        telegramSection.setVisibility(View.VISIBLE); // eski giriş alanı da görünür kalabilir
                    } else {
                        Log.i("Telegram", "Kayıtlı kullanıcı bulundu: " + savedUser);
                    }
    
                } catch (SecurityException e) {
                    statusText.setText("Bağlantı başlatılamadı: Güvenlik izni hatası!");
                }
            });
    
            requestPermissionsIfNecessary();
            checkBluetoothEnabled();
    
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
    
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                    statusText.setText("Bağlantı durumu: Tarama başlatıldı");
                    try {
                        bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
                    } catch (SecurityException e) {
                        statusText.setText("Tarama başlatılamadı: Güvenlik izni reddedildi!");
                    }
                } else {
                    statusText.setText("Tarama izni reddedildi!");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_SCAN},
                            REQUEST_CODE_PERMISSIONS);
                }
            }
    
            Button btnListUsers = findViewById(R.id.userListBtn);
            Button btnAddUser = findViewById(R.id.addUserBtn);
    
    
    // Kayıtlı kullanıcıları göster
            btnListUsers.setOnClickListener(v -> {
                StringBuilder allUsers = new StringBuilder();
                for (String key : prefs.getAll().keySet()) {
                    if (key.startsWith("telegram_user_")) {
                        String device = key.replace("telegram_user_", "");
                        String user = prefs.getString(key, "");
                        allUsers.append(device).append(" → ").append(user).append("\n");
                    }
                }
    
                if (allUsers.length() == 0) allUsers.append("Kayıtlı kullanıcı yok.");
    
                new AlertDialog.Builder(this)
                        .setTitle("Kayıtlı Kullanıcılar")
                        .setMessage(allUsers.toString())
                        .setPositiveButton("Tamam", null)
                        .show();
            });
    
    // Yeni kullanıcı ekle
            btnAddUser.setOnClickListener(v -> {
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText deviceNameInput = new EditText(this);
                deviceNameInput.setHint("Cihaz adı");
                layout.addView(deviceNameInput);

                final EditText userInput = new EditText(this);
                userInput.setHint("Telegram kullanıcı adı (@ ile)");
                layout.addView(userInput);

                new AlertDialog.Builder(this)
                        .setTitle("Yeni Kullanıcı Ekle")
                        .setView(layout)
                        .setPositiveButton("Kaydet", (dialog, which) -> {
                            String dev = deviceNameInput.getText().toString().trim();
                            String usr = userInput.getText().toString().trim();
                            if (!usr.startsWith("@")) {
                                Toast.makeText(this, "Kullanıcı adı @ ile başlamalı", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String username = usr.replace("@", "");
                            String botToken = "7806933268:AAGi1y_diJ15Iu9cOFZCx13lJYjlVk4FMck";

                            new Thread(() -> {
                                String chatId = getChatIdByUsername(botToken, username);
                                runOnUiThread(() -> {
                                    if (chatId != null) {
                                        prefs.edit().putString("telegram_user_" + dev, chatId).apply();
                                        Toast.makeText(this, "Kullanıcı başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Kullanıcıdan /start mesajı bekleniyor!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }).start();

                        })
                        .setNegativeButton("İptal", null)
                        .show();
            });



            createNotificationChannel();
        }
    
    
    
        private void requestPermissionsIfNecessary() {
            List<String> missingPermissions = new ArrayList<>();
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(permission);
                }
            }
            if (!missingPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        missingPermissions.toArray(new String[0]),
                        REQUEST_CODE_PERMISSIONS);
            }
        }
    
        private boolean hasAllPermissions() {
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
    
        private void checkBluetoothEnabled() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth desteklenmiyor!", Toast.LENGTH_SHORT).show();
            } else if (!bluetoothAdapter.isEnabled()) {
                try {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1001);
                } catch (SecurityException e) {
                    Toast.makeText(this, "Bluetooth izni reddedildi!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_CODE_PERMISSIONS) {
                if (hasAllPermissions()) {
                    Toast.makeText(this, "İzinler verildi, tarama başlatılıyor...", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(this, "Gerekli izinler reddedildi!", Toast.LENGTH_LONG).show();
                    statusText.setText("Tarama izni reddedildi!");
                }
            }
        }
    
        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                if (ActivityCompat.checkSelfPermission(MainActivityJAVA.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    if (device.getName() != null && !foundDevices.contains(device)) {
                        foundDevices.add(device);
                        deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
                        deviceListAdapter.notifyDataSetChanged();
                        runOnUiThread(() -> statusText.setText("Cihaz bulundu: " + device.getName()));
                    }
                }
            }
        };
    
        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    if (ActivityCompat.checkSelfPermission(MainActivityJAVA.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        runOnUiThread(() -> statusText.setText("Bağlantı başarılı!"));
                        bluetoothGatt = gatt;
                        gatt.discoverServices();
                    } else {
                        runOnUiThread(() -> statusText.setText("Servisleri keşfetmek için izin gerekli!"));
                    }
                }
            }
    
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (ActivityCompat.checkSelfPermission(MainActivityJAVA.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    for (BluetoothGattService service : gatt.getServices()) {
                        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                gatt.setCharacteristicNotification(characteristic, true);
    
                                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                        java.util.UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                if (descriptor != null) {
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(descriptor);
                                }
                            }
                        }
                    }
                } else {
                    runOnUiThread(() -> statusText.setText("Bildirimleri almak için izin gerekli!"));
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                if (ActivityCompat.checkSelfPermission(MainActivityJAVA.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(() -> statusText.setText("Veri almak için izin gerekli!"));
                    return;
                }
                String message = new String(characteristic.getValue());
                runOnUiThread(() -> {
                    if (message.contains(TARGET_MESSAGE)) {
                        showNotification("ALARM", "ESP32'den ALARM mesajı alındı!");

                        // username'e karşılık gelen chatId'yi getir
                        String telegramUsername = prefs.getString("telegram_user_" + connectedDeviceName, null);
                        String chatId = prefs.getString("telegram_userid_" + telegramUsername.replace("@", ""), null);

                        if (chatId != null) {
                            sendTelegramMessage(chatId, "🚨 ALARM! ESP32 cihazından uyarı geldi!");
                        } else {
                            Log.e("Telegram", "Chat ID bulunamadı! Kullanıcıdan /start mesajı gelmemiş olabilir.");
                        }
                    }

                });
            }
        };
    
        void showNotification(String title, String content) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
    
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
    
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            try {
                manager.notify(1, builder.build());
            } catch (SecurityException e) {
                Toast.makeText(this, "Bildirim gönderilemedi: izin reddedildi!", Toast.LENGTH_SHORT).show();
            }
        }
    
        void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Alarm Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
    
    
            }
    
        }

        public String getChatIdByUsername(String botToken, String targetUsername) {
            String urlString = "https://api.telegram.org/bot" + botToken + "/getUpdates";
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(result.toString());
                JSONArray updates = json.getJSONArray("result");

                for (int i = updates.length() - 1; i >= 0; i--) {
                    JSONObject message = updates.getJSONObject(i).getJSONObject("message");
                    JSONObject from = message.getJSONObject("from");

                    String username = from.getString("username");
                    if (username.equalsIgnoreCase(targetUsername)) {
                        return String.valueOf(from.getLong("id")); // CHAT_ID bulundu
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        public void sendTelegramMessage(String chatId, String message) {
            String botToken = "7806933268:AAGi1y_diJ15Iu9cOFZCx13lJYjlVk4FMck";
            String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            new Thread(() -> {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String payload = "chat_id=" + chatId + "&text=" + URLEncoder.encode(message, "UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write(payload.getBytes());
                    os.flush();
                    os.close();

                    Log.d("Telegram", "Durum Kodu: " + conn.getResponseCode());
                } catch (Exception e) {
                    Log.e("Telegram", "Telegram gönderim hatası: " + e.getMessage());
                }
            }).start();
        }









    }
