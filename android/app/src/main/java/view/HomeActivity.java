package view;

import android.app.Activity;
import android.os.Bundle;

import orleans.info.fr.remotecontrol.R;

public class HomeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }
}
