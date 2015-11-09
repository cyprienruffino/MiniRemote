package controller.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by whiteshad on 04/11/15.
 */
public class TCPClient {
    private final static int PORT = 8888;
    private Socket socket=null;
    private ObjectOutputStream out=null;
    private InputStream in=null;

    public TCPClient(String ip)  {

        try {
            InetAddress adr=InetAddress.getByName(ip);
            this.socket = new Socket(adr, PORT);
            out=new ObjectOutputStream(socket.getOutputStream());
            in=new ObjectInputStream(socket.getInputStream());
            out.writeObject("ping");
            out.flush();
            out.close();
            in.close();
            this.socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
