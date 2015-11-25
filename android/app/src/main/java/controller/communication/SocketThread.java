package controller.communication;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;


import controller.communication.events.RemoteEvent;
import controller.communication.events.RemoteEventHandler;
import controller.communication.events.StringEvent;

/**
 * Created by whiteshad on 12/11/15.
 */
public class SocketThread extends Thread implements Observer {
    private RemoteEventHandler eventHandlerRecv;
    private RemoteEventHandler eventHandlerSend;
    private Socket client;
    private String ip;
    private int port;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean running;

    public SocketThread(RemoteEventHandler eventHandlerRecv, RemoteEventHandler eventHandlerSend, String ip, int port) {
        this.eventHandlerRecv = eventHandlerRecv;
        this.eventHandlerSend = eventHandlerSend;
        this.ip = ip;
        this.port = port;
        this.running = false;
        eventHandlerSend.addObserver(this);
    }

    public SocketThread(RemoteEventHandler eventHandlerRecv, RemoteEventHandler eventHandlerSend, Socket socket) {
        this.client = socket;
        this.eventHandlerRecv = eventHandlerRecv;
        this.eventHandlerSend = eventHandlerSend;
        this.running = false;
    }


    @Override
    public void run() {
        try {
            this.client = new Socket(ip, port);
            this.in = new ObjectInputStream(client.getInputStream());
            this.out = new ObjectOutputStream(client.getOutputStream());
            client.notifyAll();
            Object o;
            while ((o = in.readObject()) != null)
                eventHandlerRecv.addRemoteEvent((RemoteEvent) o);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (client != null) {
                in.close();
                out.close();
                client.close();
            }
            this.running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public synchronized boolean isRunning() {
        return this.running;
    }



    private void send() {
        RemoteEvent e;
        try {
            e = eventHandlerSend.getRemoteEvent();
            out.writeObject(e);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void update (Observable observable, Object data){
        int event = (Integer) data;
        switch (event) {
            case RemoteEventHandler.NEW_REMOTE_EVENT:
                send();
        }
    }
}
