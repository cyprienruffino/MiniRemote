package controller.communication.wifi;

import controller.communication.callbackInterface.ClientConnected;
import controller.communication.callbackInterface.SendFinished;
import controller.communication.events.ActionException;
import controller.communication.events.EventWrapper;
import main.Controller;

import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Valentin on 23/01/2016.
 */
public class TCPServer {
    private ServerSocket serverSocket;
    private Socket client;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;


    public TCPServer(int port, ClientConnected coCallback) {
        try {
            serverSocket = new ServerSocket(port);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("TCP begin");
                        client = serverSocket.accept();
                        System.out.println("TCP accepted");
                        new Thread(new ClientProcess(client, coCallback), "InputStream").start();
                    } catch (SocketException e) {

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            System.err.println(port + " déjà utilisé par la machine !");
        }
    }

    public void close() {
        try {
            if (outputStream != null)
                outputStream.close();
            if (inputStream != null)
                inputStream.close();
            if (client != null)
                client.close();
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {

        }
    }

    public void send(EventWrapper e, SendFinished callback) {
        privateSend(e, callback);
    }

    public void send(EventWrapper e) {
        privateSend(e, null);
    }

    private synchronized void privateSend(EventWrapper e, SendFinished callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!client.isClosed()) {
                    try {
                        outputStream.writeObject(e);
                        outputStream.flush();
                        if (callback != null)
                            callback.onSendFinished();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }, "OutputStream").start();
    }

    public class ClientProcess implements Runnable {
        private Socket client;
        private ClientConnected coCallback;

        public ClientProcess(Socket client, ClientConnected coCallback) {
            this.client = client;
            this.coCallback = coCallback;
        }

        @Override
        public void run() {
            try {
                outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.flush();
                System.out.println("OutputStream OK");
                inputStream = new ObjectInputStream(client.getInputStream());
                System.out.println("InputStream OK");
                coCallback.onConnection(null);
                while (!client.isClosed()) {
                    try {
                        Object resp = inputStream.readObject();
                        Controller.handleControl(resp);
                    } catch (SocketException e) {
                    } catch (EOFException e) {
                        Controller.getInstance().restartServer();
                    } catch (ClassNotFoundException | InterruptedException | ActionException | AWTException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.err.println("Problème lors de la connexion");
            }
        }
    }
}
