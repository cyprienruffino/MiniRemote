package view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import controller.Controller;
import controller.communication.callbackInterface.ErrorInterface;
import controller.communication.events.DiapoEvent;
import controller.communication.events.EventWrapper;
import controller.communication.events.RemoteEvent;
import controller.communication.wifi.TCPService;
import orleans.info.fr.remotecontrol.R;


/**
 * Created by Valentin on 21/01/2016.
 */
public class DiapoActivity extends Activity implements ErrorInterface {
    private TCPService tcpService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diapo);
        tcpService = Controller.getTcpService();
        if (tcpService == null) {
            runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.no_tcp_service)));
        }
    }

    public void go_to(View view) {
        EditText editText = (EditText) findViewById(R.id.numpagediapo);
        Editable text = editText.getText();
        if (text.equals("")) {
            int num = Integer.parseInt(text.toString());
            editText.setText("");
            send(new DiapoEvent(num));
        } else {
            runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.diapo_text_null)));
        }
    }

    public void prec(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Prec));
    }

    public void next(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Next));
    }

    public void start(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Start));
    }

    public void origin(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Origin));
    }

    public void startHere(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.StartHere));
    }

    public void last(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Last));
    }

    private void send(RemoteEvent e) {
        if (tcpService != null) {
            tcpService.send(new EventWrapper(e), null, this);
        }

    }

    @Override
    public void onError(String message) {
        runOnUiThread(new ToastRunnable(this, message));
    }
}
