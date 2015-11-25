package controller.communication.bluetooth;

/**
 * Created by cyprien on 24/11/15.
 */

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothSocketProvider {
    private BluetoothAdapter btAdapter = null;
    private BluetoothDevice device = null;

    public BluetoothSocketProvider() throws BluetoothNotSupportedException, IOException {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) throw new BluetoothNotSupportedException();

        if (!btAdapter.isEnabled()) {
            //TODO : INSERT INTENT CODE
        }
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return btAdapter.getBondedDevices();
    }

    public void setDeviceToConnect(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothSocket provideSocket() throws BluetoothDeviceNotInitializedException, BluetoothError, IOException {
        if (device == null)
            throw new BluetoothDeviceNotInitializedException();
        ConnectThread connectThread = new ConnectThread(device);
        return connectThread.getSocket(btAdapter);
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket socket;
        private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device) throws BluetoothError {
            try {
                try {
                    socket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                } catch (NullPointerException e) {
                    socket = device.createRfcommSocketToServiceRecord(DEFAULT_UUID); //
                }
            } catch (IOException e) {
                throw new BluetoothError();
            }
        }

        public BluetoothSocket getSocket(BluetoothAdapter adapter) throws IOException {
            // Cancel discovery because it will slow down the connection
            adapter.cancelDiscovery();
            try {
                socket.connect();
            } catch (IOException connectException) {
                socket.close();
            }
            return socket;
        }

        public void cancel() {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }
}