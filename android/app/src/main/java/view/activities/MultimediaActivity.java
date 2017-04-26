package view.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import controller.Controller;
import controller.communication.callbackInterface.ErrorInterface;
import controller.communication.events.EventWrapper;
import controller.communication.events.MediaEvent;
import controller.communication.events.RemoteEvent;
import controller.communication.wifi.TCPService;
import orleans.info.fr.remotecontrol.R;
import view.ToastRunnable;


/**
 * Created by Valentin on 21/01/2016.
 */
public class MultimediaActivity extends Activity implements ErrorInterface {
    private TCPService tcpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media);
        tcpService = Controller.getTcpService();
        if (tcpService == null) {
            runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.no_tcp_service)));
        }

    }

    public void prec(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Prec));
    }

    public void next(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Next));
    }


    public void origin(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Origin));
    }

    public void voldown(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Voldown));
    }

    public void mute(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Mute));
    }

    public void volup(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Volup));
    }

    public void play(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Play));
    }

    public void fullscreen(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Fullscreen));
    }
    public void stop(View view) {
        send(new MediaEvent(MediaEvent.MediaEventType.Stop));
    }

    private void send(RemoteEvent e) {
        if (tcpService != null) {
            tcpService.send(new EventWrapper(e), this);
        }

    }

    @Override
    public void onError(String message) {
        runOnUiThread(new ToastRunnable(this, message));
    }



}
