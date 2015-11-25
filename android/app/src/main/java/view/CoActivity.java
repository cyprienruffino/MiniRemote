package view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import controller.communication.wifi.ConnectionManager;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by whiteshad on 04/11/15.
 */
public class CoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);
    }

    public void connexion(View view) {

        ConnectionManager cm =new ConnectionManager(this.getBaseContext());
        cm.startConnection(ConnectionManager.PC_REMOTE_MODE_WIFI);
       // cm.sendRemoteEvent(new StringEvent("hello"));
    }

}

