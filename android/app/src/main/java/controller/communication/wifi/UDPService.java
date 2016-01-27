package controller.communication.wifi;

import android.content.Context;
import controller.Controller;
import controller.communication.callbackInterface.ErrorInterface;
import controller.communication.callbackInterface.NetworkDiscovery;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

/**
 * Created by Valentin on 23/01/2016.
 */
public class UDPService extends SurService {
    public static final int timeout = 10000;
    public int port;
    private boolean serverFound = false;
    private DatagramSocket socket;
    private DatagramSocket socketConnect;
    private NetworkDiscovery cb;

    public void startServer(Context context, NetworkDiscovery callback, ErrorInterface errorCallback) {
        port = Controller.getPort();
        cb = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new DatagramSocket(port);
                    byte[] ping = "Ping".getBytes();
                    byte[] buffer = new byte[4096];
                    socket.setBroadcast(true);
                    socket.setSoTimeout(timeout);
                    DatagramPacket packet = new DatagramPacket(ping, ping.length, Utils.getBroadcastAddress(context), port);
                    socket.send(packet);
                    DatagramPacket packRecv = new DatagramPacket(buffer, buffer.length);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    socket.setSoTimeout(timeout);
                                    socket.receive(packRecv);
                                    onServerFound(packRecv);
                                    packRecv.setLength(buffer.length);
                                }
                            } catch (InterruptedIOException e) {
                                if (!serverFound) {
                                    onNoServerFound();
                                }
                            } catch (SocketException e) {
                            } catch (IOException e) {
                                errorCallback.onError("Erreur inconnu");
                            }
                        }
                    }, "UDPServerResponseThread").start();
                } catch (SocketException e) {
                    errorCallback.onError("Erreur inconnu 3");
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    errorCallback.onError("Can't have BroadcastAddress");
                } catch (IOException e) {
                    errorCallback.onError("Erreur inconnu 2");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void close() {
        if (socket != null)
            socket.close();
        if (socketConnect != null) {
            socketConnect.close();
        }

    }

    public void onServerFound(DatagramPacket packet) {
        String data = new String(packet.getData());
        if (data.contains("Pong")) {
            System.out.println("J'ai reçu un pong");
            serverFound = true;
            cb.onNetworkFound(packet.getAddress(), packet.getPort());
        }
    }

    public void sendConnect(int port, InetAddress adress, ErrorInterface errorInterface) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.close();
                    System.out.println("Socket closed ou pas");
                    byte[] connect = "Connect".getBytes();
                    DatagramPacket packet = new DatagramPacket(connect, connect.length, adress, port);
                    socketConnect = new DatagramSocket(port);
                    System.out.println("J'essaie d'envoyer à " + adress + " " + port);
                    socketConnect.send(packet);
                    socketConnect.close();
                } catch (IOException e) {
                    errorInterface.onError("Erreur de connexion avec le serveur");
                    e.printStackTrace();
                }


            }
        }).start();
    }

    private void onNoServerFound() {
        cb.onNoNetworkFound();
    }
}
