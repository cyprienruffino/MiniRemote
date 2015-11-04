package view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import controller.communication.Discovery;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by whiteshad on 04/11/15.
 */
public class CoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conection);
    }

    public void connexion(View view) {
        Discovery dis=new Discovery(this.getBaseContext());
        String s= dis.getServerIp();

    }
}
