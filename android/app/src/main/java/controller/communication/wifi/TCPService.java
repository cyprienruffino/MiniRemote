package controller.communication.wifi;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import controller.Controller;
import controller.communication.callbackInterface.NetworkDiscovery;
import controller.communication.callbackInterface.SendFinished;
import controller.communication.events.EventWrapper;
import view.HomeActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cyprien on 18/12/15.
 */
public class TCPService extends Service implements NetworkDiscovery {


    /*******************
     * Service part
     **************************************/


    private final IBinder mBinder = new TCPBinder();
    private boolean closed = false;

    @Override
    public void onNetworkFound() {
        String IP = discovery.getIpServer();
        discovery.stop();
        try {
            ServerInput actionInput = new ServerInput(IP, PORT, events, lock);
            actionOutput = new ServerOutput(IP, PORT, events, lock);
            serverInputThread = new Thread(actionInput);
            serverOutputThread = new Thread(actionOutput);
            serverInputThread.start();
            serverOutputThread.start();
            activity.onNetworkFound();
        } catch (IOException e) {
            activity.onNoNetworkFound();
        }
    }

    @Override
    public void onNoNetworkFound() {
        activity.onNoNetworkFound();
    }

    public void setOnSendFinished(SendFinished cb) {
        if (actionOutput != null)
            actionOutput.setcallback(cb);
    }

    public class TCPBinder extends Binder {
        public TCPService getService() {
            return TCPService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /*******************
     * Server part
     ****************************************/

    private final static int PORT = 8888;
    private Thread serverInputThread;
    private Thread serverOutputThread;
    private ServerOutput actionOutput;
    private ServerSocket serverSocket = null;
    private List<EventWrapper> events;
    private Object lock = new Object();
    private HomeActivity activity;
    private Discovery discovery;

    public void startServer(Activity act) {
        activity = (HomeActivity) act;
        events = Collections.synchronizedList(new ArrayList<EventWrapper>());
        discovery = new Discovery(this, this.getApplicationContext(), Discovery.PC_REMOTE_MODE);
        Thread thread = new Thread(discovery);
        thread.start();
    }

    public void send(EventWrapper event) {
        synchronized (lock) {
            if (events == null)
                events = Collections.synchronizedList(new ArrayList<EventWrapper>());
            events.add(event);
            lock.notify();
        }
    }


    public void stop() {
        closed = true;
    }

    private class ServerInput implements Runnable {
        private Socket socket = null;
        private List<EventWrapper> events;
        private EventWrapper received;
        private EventWrapper response;
        private String IP;
        private int PORT;
        private Object lock;

        public ServerInput(String IP, int PORT, List<EventWrapper> events, Object lock) throws IOException {
            this.IP = IP;
            this.PORT = PORT;
            this.events = events;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                this.socket = new Socket(IP, PORT);
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                while (!closed) {
                    received = (EventWrapper) in.readObject();
                    Log.wtf("OK!", "Ca marche!");
                    Controller.execute(received);
                }
            } catch (IOException | ClassNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }

    private class ServerOutput implements Runnable {
        private final List<EventWrapper> events;
        private EventWrapper event;
        private Socket socket = null;
        private String IP;
        private int PORT;
        private Object lock;
        private SendFinished callback;

        public void setcallback(SendFinished cb) {
            callback = cb;
        }

        public ServerOutput(String IP, int PORT, List<EventWrapper> events, Object lock) throws IOException {
            this.IP = IP;
            this.PORT = PORT;
            this.events = events;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                this.socket = new Socket(IP, PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                synchronized (lock) {
                    while (!closed) {
                        lock.wait();
                        while (!events.isEmpty()) {
                            event = events.remove(0);
                            if (event != null)
                                out.writeObject(event);
                            out.flush();
                            executeCallback();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void executeCallback() {
            if (callback != null)
                callback.onSendFinished();
        }
    }
}
