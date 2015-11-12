package controller.communication;

import android.app.Activity;
import android.widget.Toast;

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
    private Activity activity;

    public TCPClient(String ip,Activity act)  {
        activity=act;
        try {
            InetAddress adr=InetAddress.getByName(ip);
            this.socket = new Socket(adr, PORT);
            makeToast("Connexion au serveur effectué");
            out=new ObjectOutputStream(socket.getOutputStream());
            in=new ObjectInputStream(socket.getInputStream());
           while (socket!=null && !socket.isClosed()){
               send("Ping");
           }
            out.close();
            in.close();
            this.socket.close();
        } catch (UnknownHostException e) {
            makeToast("ERREUR: Echec de la  connexion au serveur");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Object o){
        try{
            out.writeObject(o);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void halt(){
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeToast(String s){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t=Toast.makeText(activity.getBaseContext(),s,Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }
}


