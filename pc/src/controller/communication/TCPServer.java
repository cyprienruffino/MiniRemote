package controller.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by whiteshad on 04/11/15.
 */
public class TCPServer {
    private final static int PORT = 8888;
    private ServerSocket server=null;
    private Socket socket=null;
    private OutputStream out=null;
    private InputStream in=null;

    public TCPServer() {
        try {
            server=new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runServer(){
        try {
            socket=server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
//runOnRunUI
//asynctask