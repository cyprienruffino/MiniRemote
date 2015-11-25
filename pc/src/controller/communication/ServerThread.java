package controller.communication;

import java.io.IOException;
import java.net.ServerSocket;

import controller.communication.events.RemoteEvent;
import controller.communication.events.RemoteEventHandler;

/**
 * Created by whiteshad on 12/11/15.
 */
public class ServerThread extends Thread {
    private RemoteEventHandler eventHandler;
    private SocketThread client=null;
    private ServerSocket server;
    private int port;
    private boolean acceptingConnection;

    public ServerThread(RemoteEventHandler eventHandler, int port) {
        this.eventHandler = eventHandler;
        this.port=port;
        acceptingConnection=true;
    }

    @Override
    public void run() {
        try {
            server=new ServerSocket(port);
            if (acceptingConnection){
                client=new SocketThread(eventHandler,server.accept());
                System.out.println("new client connected");
                acceptingConnection=false;
                }
            client.start();
            while (true){
                if (!client.isConnected()){
                    acceptingConnection=true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketThread getClientThread(){
        return client;
    }

}
