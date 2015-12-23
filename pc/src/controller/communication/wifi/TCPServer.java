package controller.communication.wifi;

import java.awt.AWTException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import controller.communication.events.ActionException;
import controller.communication.events.EventWrapper;
import controller.communication.events.ResponseEvent;
import main.Controller;


/**
 * Created by cyprien on 04/11/15.
 */
public class TCPServer {
    private final static int PORT = 8888;

    private Thread serverInputThread;
    private Thread serverOutputThread;
    private Socket socket=null;
    private ServerSocket serverSocket=null;
    private List<EventWrapper> events;
    private Object lock=new Object();
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public TCPServer() {
        events= Collections.synchronizedList(new ArrayList<>());
    }

    public void startServer() throws IOException {
        serverSocket=new ServerSocket(PORT);
        System.out.println("en attente de client");
        socket=serverSocket.accept();
        System.out.println("client connecté");


        out=new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        System.out.println("OutputStream Instancié");
        in=new ObjectInputStream(socket.getInputStream());
        System.out.println("InputStream Instancié");
        ServerInput actionInput = new ServerInput(in, events, lock, this);
        ServerOutput actionOutput = new ServerOutput(out, events, lock, this);

        serverInputThread=new Thread(actionInput);
        serverOutputThread=new Thread(actionOutput);
        serverInputThread.start();
        serverOutputThread.start();
        System.out.println("Threads lancés");
    }

    public void send(EventWrapper event){
        synchronized (lock) {
            events.add(event);
            lock.notifyAll();
        }
    }


    public void stop() throws IOException {
        send(new EventWrapper(new ResponseEvent(ResponseEvent.SERVER_SHUTDOWN)));
        serverOutputThread.interrupt();
        serverInputThread.interrupt();
        in.close();
        out.close();
        serverSocket.close();
        socket.close();
    }

    private class ServerInput implements Runnable{
        private ObjectInputStream in;
        private List<EventWrapper> events;
        private EventWrapper received;
        private EventWrapper response;
        private Object lock;
        private TCPServer server;

        public ServerInput(ObjectInputStream in, List<EventWrapper> events, Object lock, TCPServer server) throws IOException {
            this.server=server;
            this.in=in;
            this.events=events;
            this.lock=lock;
            System.out.println("InputThread instancié");
        }

        @Override
        public void run(){
            try {
                synchronized (lock){
                    while(true) {
                        try {
                            received = (EventWrapper) in.readObject();
                        }catch (SocketException e) {
                            server.stop();
                        }
                        System.out.println("Objet recu");
                        //response = Controller.handleControl(received);
                        events.add(response);
                        lock.notifyAll();
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private class ServerOutput implements Runnable{
        private List<EventWrapper> events;
        EventWrapper event;
        private ObjectOutputStream out;
        private Object lock;
        private TCPServer server;

        public ServerOutput(ObjectOutputStream out, List<EventWrapper> events, Object lock, TCPServer server) throws IOException {
            this.server=server;
            this.out=out;
            this.events=events;
            this.lock=lock;
            System.out.println("OutputThread instancié");
        }

        @Override
        public void run(){
            try {
                synchronized (lock) {
                    while (true) {
                        lock.wait();
                        while (!events.isEmpty()) {
                            event=events.remove(0);
                            if(event!=null)
                                out.writeObject(event);
                            System.out.println("Event envoyé");
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
