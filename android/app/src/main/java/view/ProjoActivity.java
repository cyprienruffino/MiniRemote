package view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 16/11/2015.
 */
public class ProjoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projoview);
    }

    public void status(View view) {
    }

    public void onoff(View view) {
    }
}
