package view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import controller.communication.Discovery;
import controller.communication.TCPClient;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by whiteshad on 04/11/15.
 */
public class CoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.println(Log.DEBUG, "DEBUG", "HI");
        System.out.println("test");
        setContentView(R.layout.connexion);
    }

    public void connexion(View view) {
        Discovery dis=new Discovery(this.getBaseContext());
        Thread t= new Thread(new UDPDiscovery(dis));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TextView txtV = (TextView) findViewById(R.id.connexion_txtView);
        txtV.setText(dis.getIpServer());
        t.interrupt();
        Thread t1=new Thread(new TCP(dis.getIpServer()));
        t1.start();
    }

}

 class UDPDiscovery implements Runnable{
    private Discovery d;

    public UDPDiscovery(Discovery d){
        this.d=d;
    }
    @Override
    public void run() {
        d.setIpServer(d.getServerIp());
    }
}

class TCP implements Runnable{
    private TCPClient tcp;
    private String ip;

    public TCP(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {
        tcp=new TCPClient(ip);
    }
}