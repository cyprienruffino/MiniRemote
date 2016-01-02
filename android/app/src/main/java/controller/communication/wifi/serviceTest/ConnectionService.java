package controller.communication.wifi.serviceTest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import controller.Controller;
import controller.communication.events.RemoteEvent;
import controller.communication.events.RemoteEventHandler;
import controller.communication.wifi.ConnectionManager;
import controller.communication.wifi.Discovery;

/**
 * Created by whiteshad on 25/11/15.
 */
public class ConnectionService extends Service {
    public static final int PC_REMOTE_MODE_WIFI=0;
    public static final int VP_REMOTE_MODE_WIFI_DIRECT=1;
    public static final int VP_REMOTE_MODE_WIFI_PC=2;
    public static final int VP_REMOTE_MODE_BLUETOOTH_PC=3;
    public static final int PC_REMOTE_MODE_BLUETOOTH=4;
    private static final int PC_PORT = 8888;
    private static final int EPSON_VP_PORT = 3629;
    private ConnectionBinder connectionBinder=new ConnectionBinder();
    private Socket client;
    private String ip;
    private int port;
    private ObjectInputStream in;
    private Thread inThread;
    private Controller controller;
    private boolean running=false;




    /*public void startService(int mode,Controller c){
        controller=c;
        discovery(mode);
        try {
            client = new Socket(ip,port);
            in=new ObjectInputStream(client.getInputStream());
            running=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        inThread=new ReadThread(in,this.getBaseContext(),controller);
        inThread.start();
    }*/


   /* private void discovery(int mode){
        Discovery dis=new Discovery(this.getBaseContext(),mode);
        Thread t= new Thread(dis);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ip=dis.getIpServer();
        switch (mode){
            case PC_REMOTE_MODE_WIFI:
                this.port=PC_PORT;
                break;
            case VP_REMOTE_MODE_WIFI_DIRECT:
                this.port=EPSON_VP_PORT;
                break;
            case VP_REMOTE_MODE_WIFI_PC:
                this.port=PC_PORT;
                break;
        }
        dis.stop();
        t.interrupt();
    }*/



    private void send(RemoteEvent event){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectOutputStream out= null;
                if(running && client.isConnected()) {
                    try {
                        out = new ObjectOutputStream(client.getOutputStream());
                        //out.writeObject(event);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }



    @Override
    public IBinder onBind(Intent intent) {
        return this.connectionBinder;
    }

    public class ConnectionBinder extends Binder{
        ConnectionService getService() {
            return ConnectionService.this;
        }

        void send(RemoteEvent e){
            ConnectionService.this.send(e);
        }

    }
}
