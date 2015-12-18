package controller.communication.wifi;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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
    public class TCPBinder extends Binder{
        public TCPService getService(){ return TCPService.this;}}
    private final IBinder mBinder = new TCPBinder() ;

    @Override
    public IBinder onBind(Intent intent) {
        events= Collections.synchronizedList(new ArrayList<>());
        return mBinder;
    }
    /*******************Server part****************************************/

    private final static int PORT = 8888;
    private Thread serverInputThread;
    private Thread serverOutputThread;
    private Socket socket=null;
    private ServerSocket serverSocket=null;
    private List<EventWrapper> events;

    public void startServer() throws IOException {

        String IP=new Discovery(this.getApplicationContext(), Discovery.PC_REMOTE_MODE).getIpServer();
        socket=new Socket(IP, PORT);

        ServerInput actionInput = new ServerInput(socket, events);
        ServerOutput actionOutput = new ServerOutput(socket, events);
        serverInputThread=new Thread(actionInput);
        serverOutputThread=new Thread(actionOutput);
        serverInputThread.start();
        serverOutputThread.start();
    }

    public void send(EventWrapper event){
        events.add(event);
        events.notifyAll();
    }


    public void stop(){
        serverInputThread.interrupt();
    }

    private class ServerInput implements Runnable{
        private Socket socket=null;
        private List<EventWrapper> events;
        private EventWrapper received;
        private EventWrapper response;

        public ServerInput(Socket socket, List<EventWrapper> events) throws IOException {
            this.socket=socket;
            this.events=events;
        }

        @Override
        public void run(){
            try {
                ObjectInputStream in=new ObjectInputStream(socket.getInputStream());

                while(true){
                    received=(EventWrapper)in.readObject();
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

        public ServerOutput(Socket socket, List<EventWrapper> events) throws IOException {
            this.socket=socket;
            this.events=events;
        }

        @Override
        public void run(){
            try {
                ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());

                while (true){
                    events.wait();
                    while (!events.isEmpty()){
                        out.writeObject(events.remove(0));
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
