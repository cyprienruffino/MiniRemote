package controller.communication.wifi;

import java.awt.AWTException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import controller.communication.events.ActionException;
import controller.communication.events.EventWrapper;
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

    public TCPServer() {
        events= Collections.synchronizedList(new ArrayList<>());
    }

    public void startServer() throws IOException {
        serverSocket=new ServerSocket(PORT);
        System.out.println("en attente de client");
        socket=serverSocket.accept();
        System.out.println("client connecté");


        ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
        ServerInput actionInput = new ServerInput(in, events, lock);
        ServerOutput actionOutput = new ServerOutput(out, events, lock);

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


    public void stop(){

    }

    private class ServerInput implements Runnable{
        private ObjectInputStream in;
        private List<EventWrapper> events;
        private EventWrapper received;
        private EventWrapper response;
        private Object lock;

        public ServerInput(ObjectInputStream in, List<EventWrapper> events, Object lock) throws IOException {
            this.in=in;
            this.events=events;
            this.lock=lock;
            System.out.println("InputThread instancié");
        }

        @Override
        public void run(){
            try {

                System.out.println("InputStream instancié");
                synchronized (lock){
                    while(true) {
                        received = (EventWrapper) in.readObject();
                        System.out.println("Objet recu");
                        response = Controller.handleControl(received);
                        events.add(response);
                        lock.notifyAll();
                    }
                }

            } catch (IOException | ClassNotFoundException | AWTException | ActionException e) {
                e.printStackTrace();
            }
        }
    }
    private class ServerOutput implements Runnable{
        private List<EventWrapper> events;
        private ObjectOutputStream out;
        private Object lock;

        public ServerOutput(ObjectOutputStream out, List<EventWrapper> events, Object lock) throws IOException {
            this.out=out;
            this.events=events;
            this.lock=lock;
            System.out.println("OutputThread instancié");
        }

        @Override
        public void run(){
            try {

                System.out.println("OutputStream instancié");
                synchronized (lock) {
                    while (true) {
                        lock.wait();
                        while (!events.isEmpty()) {
                            out.writeObject(events.remove(0));
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
