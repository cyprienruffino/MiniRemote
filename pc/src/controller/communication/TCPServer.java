package controller.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by whiteshad on 04/11/15.
 */
public class TCPServer {
    private final static int PORT = 8888;
    private ServerSocket server=null;
    private Socket socket=null;
    private ObjectOutputStream out=null;
    private ObjectInputStream in=null;

    public TCPServer() {
        try {
            server=new ServerSocket(PORT);
            System.out.println("serveur TCP lancé");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runServer(){
        try {
            System.out.println("en attente de client");
            socket=server.accept();
            System.out.println("client connecté");
            out=new ObjectOutputStream(socket.getOutputStream());
            in=new ObjectInputStream(socket.getInputStream());
            while (socket!=null && !socket.isClosed()) {
             in.readObject();
            }
            stop();
            System.out.println("serveur fermé");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void send(Object o){
        try {
            out.writeObject(o);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        try {
            in.close();
            out.close();
            socket.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
