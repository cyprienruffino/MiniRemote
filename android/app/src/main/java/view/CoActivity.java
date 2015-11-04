package view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import controller.communication.Discovery;
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
        Thread t= new Thread(new Test(dis));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TextView txtV = (TextView) findViewById(R.id.connexion_txtView);
        txtV.setText(dis.getIpServer());
    }

}

 class Test implements Runnable{
    private Discovery d;

    public Test( Discovery d){
        this.d=d;
    }
    @Override
    public void run() {
        d.setIpServer(d.getServerIp());
    }
}