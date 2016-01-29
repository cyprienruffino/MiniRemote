package controller.communication.wifi;

import controller.communication.callbackInterface.ClientConnectionRequest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Valentin on 23/01/2016.
 */
public class UDPServer {
    public int port;
    private boolean running = true;
    private DatagramSocket server;

    public UDPServer(int port, ClientConnectionRequest callback) {
        this.port = port;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server = new DatagramSocket(port);
                    System.out.println("Server UDP started");
                    while (running) {
                        try {
                            byte[] buffer = new byte[4096];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            server.receive(packet);
                            String recv = new String(packet.getData());
                            packet.setLength(buffer.length);
                            if (recv.contains("Ping")) {
                                byte[] buffer2 = "Pong".getBytes();
                                DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, packet.getAddress(), packet.getPort());
                                server.send(packet2);
                                System.out.println("Ping recv");
                            } else if (recv.contains("Connect")) {
                                System.out.println("Connect recv");
                                callback.onClientConnection(packet.getPort(), packet.getAddress());
                            }
                        } catch (SocketException e) {
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void close() {
        running = false;
        if (server != null)
            server.close();
    }
}
