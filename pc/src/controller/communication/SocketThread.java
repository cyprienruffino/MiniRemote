package controller.communication;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import controller.communication.events.RemoteEvent;
import controller.communication.events.RemoteEventHandler;
import controller.communication.events.StringEvent;

/**
 * Created by whiteshad on 12/11/15.
 */
public class SocketThread  extends Thread{
    private RemoteEventHandler eventHandler;
    private Socket client;
    private String ip;
    private int port;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public SocketThread(RemoteEventHandler eventHandler, String ip,int port) {
        this.eventHandler = eventHandler;
        this.ip=ip;
        this.port=port;
    }

    public SocketThread(RemoteEventHandler eventHandler,Socket socket){
        this.client=socket;
        this.eventHandler=eventHandler;
    }


    @Override
    public void run() {
        try {
            if(client==null) {
                this.client = new Socket(ip, port);
            }
            this.in= new ObjectInputStream(client.getInputStream());
            this.out= new ObjectOutputStream(client.getOutputStream());

            while (true){
                if(in.available()!=0) {
                    eventHandler.addRemoteEvent((RemoteEvent) in.readObject());
                    System.out.println(((StringEvent) (eventHandler.getRemoteEvent())).s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

public void close(){
    try {
        in.close();
        out.close();
        client.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public boolean isConnected(){
        return client.isConnected();
    }

    public void sendRemoteEvent(RemoteEvent event){
        try {
            out.writeObject(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
