package view;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import controller.Controller;
import controller.communication.events.EventWrapper;
import controller.communication.events.MoveMouseEvent;
import controller.communication.wifi.ConnectionManager;
import controller.communication.wifi.TCPService;
import controller.communication.wifi.serviceTest.ServiceConnector;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by whiteshad on 04/11/15.
 */
public class CoActivity extends Activity {
    private TCPService tcpService;
    private ServiceConnection sc=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            tcpService = ((TCPService.TCPBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            tcpService=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        Intent intent=new Intent(this.getApplicationContext(), TCPService.class);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Log.d("QUIT", "ONDESTROY ");
        try {
            unbindService(sc);
        } catch (IllegalArgumentException e){
            Log.d("ONSTOP", "Pas de service");
        }
        super.onDestroy();
    }

    public void connexion(View view) throws IOException, InterruptedException {
        tcpService.startServer();
        Controller.setTcpService(tcpService);
        Controller.isServiceStarted=true;
    }
    public void test(View view){
        MoveMouseEvent event = new MoveMouseEvent(1000, 1000);
        tcpService.send(new EventWrapper(event));
    }


}

