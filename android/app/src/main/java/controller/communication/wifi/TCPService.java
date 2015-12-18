package controller.communication.wifi;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import controller.Controller;
import controller.communication.events.EventWrapper;

/**
 * Created by cyprien on 18/12/15.
 */
public class TCPService extends Service{
    /*******************Service part**************************************/
    private final IBinder mBinder = new TCPBinder() ;
    public class TCPBinder extends Binder{
        public TCPService getService(){ return TCPService.this;}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    /*******************Server part****************************************/

    private final static int PORT = 8888;
    private Thread serverInputThread;
    private Thread serverOutputThread;
    private ServerSocket serverSocket=null;
    private List<EventWrapper> events;
    private Object lock=new Object();

    public void startServer() throws IOException, InterruptedException {

        events=Collections.synchronizedList(new ArrayList<EventWrapper>());
        Discovery discovery=new Discovery(this.getApplicationContext(), Discovery.PC_REMOTE_MODE);
        Thread thread=new Thread(discovery);
        thread.start();
        thread.join();
        String IP=discovery.getIpServer();
        discovery.stop();

        ServerInput actionInput = new ServerInput(IP, PORT, events, lock);
        ServerOutput actionOutput = new ServerOutput(IP, PORT, events, lock);
        serverInputThread=new Thread(actionInput);
        serverOutputThread=new Thread(actionOutput);
        serverInputThread.start();
        serverOutputThread.start();
    }

    public void send(EventWrapper event){
        synchronized (lock) {
            events.add(event);
            lock.notify();
        }
    }


    public void stop(){
        serverInputThread.interrupt();
    }

    private class ServerInput implements Runnable{
        private Socket socket=null;
        private List<EventWrapper> events;
        private EventWrapper received;
        private EventWrapper response;
        private String IP;
        private int PORT;
        private Object lock;

        public ServerInput(String IP, int PORT, List<EventWrapper> events, Object lock) throws IOException {
            this.IP=IP;
            this.PORT=PORT;
            this.events=events;
            this.lock=lock;
        }

        @Override
        public void run(){
            try {
                this.socket=new Socket(IP, PORT);
                ObjectInputStream in=new ObjectInputStream(socket.getInputStream());

                while(true){
                    received=(EventWrapper)in.readObject();
                    Log.wtf("OK!", "Ca marche!");
                    Controller.execute(received);
                }

            } catch (IOException | ClassNotFoundException  e) {
                e.printStackTrace();
            }
        }
    }
    private class ServerOutput implements Runnable{
        private final List<EventWrapper> events;
        private Socket socket=null;
        private String IP;
        private int PORT;
        private Object lock;

        public ServerOutput(String IP, int PORT, List<EventWrapper> events,Object lock) throws IOException {
            this.IP=IP;
            this.PORT=PORT;
            this.events=events;
            this.lock=lock;
        }

        @Override
        public void run(){
            try {
                this.socket=new Socket(IP, PORT);
                ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                synchronized (lock){
                    while (true) {
                        lock.wait();
                        while (!events.isEmpty()) {
                            out.writeObject(events.remove(0));
                            out.flush();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
