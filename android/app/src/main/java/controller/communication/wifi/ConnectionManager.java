package controller.communication.wifi;

import android.content.Context;


import java.util.Observable;
import java.util.Observer;

import controller.communication.SocketThread;
import controller.communication.events.RemoteEvent;
import controller.communication.events.RemoteEventHandler;

/**
 * Created by whiteshad on 04/11/15.
 */
public class ConnectionManager extends Observable implements Observer {
    public static final int NEW_REMOTE_EVENT=0x1;
    public static final int PC_REMOTE_MODE_WIFI=0;
    public static final int VP_REMOTE_MODE_WIFI_DIRECT=1;
    public static final int VP_REMOTE_MODE_WIFI_PC=2;
    public static final int VP_REMOTE_MODE_BLUETOOTH_PC=3;
    public static final int PC_REMOTE_MODE_BLUETOOTH=4;
    private static final int PC_PORT = 8888;
    private static final int EPSON_VP_PORT = 3629;
    private RemoteEventHandler eventHandlerRecv;
    private RemoteEventHandler eventHandlerSend;
    private Context context;
    private SocketThread client;

    public ConnectionManager(Context context) {
        this.eventHandlerRecv =new RemoteEventHandler();
        this.eventHandlerSend= new RemoteEventHandler();
        this.context=context;
        eventHandlerRecv.addObserver(this);
    }

    public void startConnection(int mode){
        Discovery dis=new Discovery(context,PC_REMOTE_MODE_WIFI);
        Thread t= new Thread(dis);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int port=0;
        switch (mode){
            case PC_REMOTE_MODE_WIFI:
                port=PC_PORT;
                break;
            case VP_REMOTE_MODE_WIFI_DIRECT:
                port=EPSON_VP_PORT;
                break;
            case VP_REMOTE_MODE_WIFI_PC:
                port=PC_PORT;
                break;
        }
        client=new SocketThread(eventHandlerRecv,eventHandlerSend,dis.getIpServer(),port);
        client.start();
        dis.stop();
        t.interrupt();
    }


    public void stop(){
        client.close();
        client.interrupt();
    }

    public RemoteEvent getRemoteEvent()  {
            synchronized (eventHandlerRecv) {
                return eventHandlerRecv.getRemoteEvent();
            }
    }

    public boolean clientIsRunning(){
        return client.isRunning();
    }

    public void sendRemoteEvent(RemoteEvent event){
        synchronized (eventHandlerSend) {
            this.eventHandlerSend.addRemoteEvent(event);
        }
        eventHandlerSend.notifyAll();
    }


    public synchronized SocketThread getClient(){
        return client;
    }

    @Override
    public void update(Observable observable, Object data) {
        int event=(Integer)data;
        switch (event){
            case RemoteEventHandler.NEW_REMOTE_EVENT:
                notify(this.NEW_REMOTE_EVENT);
                break;
        }
    }

    private void notify(int event){
        setChanged();
        notifyObservers(event);
    }
}
