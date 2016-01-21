package view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import controller.communication.events.RemoteEvent;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 21/01/2016.
 */
public class DiapoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diapo);
    }

    public void go_to(View view) {
    }

    public void prec(View view) {
    }

    public void next(View view) {
    }

    public void start(View view) {
    }

    public void origin(View view) {
    }

    public void startHere(View view) {
    }

    public void last(View view) {
    }

    private void send(RemoteEvent e) {

    }
}
