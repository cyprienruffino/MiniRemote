package controller.communication.wifi.serviceTest;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import controller.Controller;
import controller.communication.events.RemoteEvent;

/**
 * Created by whiteshad on 25/11/15.
 */
public class ReadThread extends Thread {
    private ObjectInputStream in;
    private Context context;
    private Controller controller;
    private boolean running=false;


    public ReadThread(ObjectInputStream stream,Context c, Controller controller) {
        in=stream;
        context=c;
        this.controller=controller;
    }

    @Override
    public void run() {
        while (running){
            try {
                RemoteEvent e=(RemoteEvent)in.readObject();
                this.execute(e);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void execute(RemoteEvent event){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //controller.execute(event);
                Toast t=new Toast(context);
                t.makeText(context,"event Reçu",Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }
}
