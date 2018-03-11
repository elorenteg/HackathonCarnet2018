package com.bvisible.carnet.controllers;

import android.content.Context;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class BluetoothController {
    public static final int BLUETOOTH_CONNECTING = 1;
    public static final int BLUETOOTH_CONNECTED = 2;
    public static final int BLUETOOTH_READY = 3;
    public static final int BLUETOOTH_DISCONNECTED = 4;
    public static final int BLUETOOTH_FAILED = 5;

    public static final int SEND_PALO_HELLO = 1;
    public static final int SEND_PALO_VIBRATION = 2;
    public static final int SEND_PALO_LED = 3;
    public static final int SEND_PALO_OLED = 4;
    private static final String TAG = BluetoothController.class.getSimpleName();
    private static BluetoothController instance;
    public final String hc06Bluetooth = "20:15:10:20:04:46";
    private final Context mContext;
    private BluetoothSPP bluetoothSPP;
    private ReadReceived readReceivedCallback;
    private BluetoothStatus bluetoothStatusCallback;

    private BluetoothController(Context context) {
        // Initializes Bluetooth adapter.
        this.mContext = context;
        this.bluetoothSPP = new BluetoothSPP(mContext);
    }

    public static BluetoothController getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothController(context);
        }
        return instance;
    }

    public void setCallbacks(BluetoothStatus bluetoothStatusCallback, ReadReceived callbackReadRequest) {
        this.bluetoothStatusCallback = bluetoothStatusCallback;
        this.readReceivedCallback = callbackReadRequest;
    }

    public void startService() {
        if (bluetoothSPP.isBluetoothAvailable()) {
            bluetoothSPP.setupService();
            bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
        }

        bluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                // Do something when successfully connected
                bluetoothStatusCallback.onBluetoothChanged(BLUETOOTH_CONNECTED);
            }

            public void onDeviceDisconnected() {
                // Do something when connection was disconnected
                bluetoothStatusCallback.onBluetoothChanged(BLUETOOTH_DISCONNECTED);
            }

            public void onDeviceConnectionFailed() {
                // Do something when connection failed
                bluetoothStatusCallback.onBluetoothChanged(BLUETOOTH_FAILED);
            }
        });

        bluetoothSPP.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    // Do something when successfully connected
                    // Don't send callback, already done in BluetoothConnection Listener
                    //bluetoothStatusCallback.onBluetoothChanged(BLUETOOTH_CONNECTED);
                } else if (state == BluetoothState.STATE_CONNECTING) {
                    // Do something while connecting
                    bluetoothStatusCallback.onBluetoothChanged(BLUETOOTH_CONNECTING);
                } else if (state == BluetoothState.STATE_LISTEN) {
                    // Do something when device is waiting for connection
                    bluetoothStatusCallback.onBluetoothChanged(BLUETOOTH_READY);
                    bluetoothSPP.connect(hc06Bluetooth);
                } else if (state == BluetoothState.STATE_NONE) {
                    // Do something when device don't have any connection
                }
            }
        });

        bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                readReceivedCallback.onReadReceived(message);
            }
        });
    }

    public void sendData(int code, String message) {
        if (bluetoothSPP != null) {
            bluetoothSPP.send(code + message, true);
        }
    }

    public void stopService() {
        bluetoothSPP.stopService();
    }

    public interface BluetoothStatus {
        void onBluetoothChanged(int bluetoothStatus);
    }

    public interface ReadReceived {
        void onReadReceived(String valueReceived);
    }
}
