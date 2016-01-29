package view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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
    private SoftwareActivity.Diapo soft;

    private class OnSwipeTouchListener implements OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diapo);
        tcpService = Controller.getTcpService();
        soft = Controller.getSoftware();
        Controller.setErrorInterface(this);
        if (tcpService == null) {
            runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.no_tcp_service)));
        }
        findViewById(R.id.diapolayout).setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
             @Override
             public void onSwipeLeft() {
                 send(new DiapoEvent(DiapoEvent.DiapoEventType.Next, soft.getId()));
             }
              @Override
             public void onSwipeTop() {
                 send(new DiapoEvent(DiapoEvent.DiapoEventType.Next, soft.getId()));
             }
              @Override
             public void onSwipeBottom() {
                 send(new DiapoEvent(DiapoEvent.DiapoEventType.Prec, soft.getId()));
             }
              @Override
             public void onSwipeRight() {
                 send(new DiapoEvent(DiapoEvent.DiapoEventType.Prec, soft.getId()));
             }
         });
    }

    public void go_to(View view) {
        EditText editText = (EditText) findViewById(R.id.numpagediapo);
        Editable text = editText.getText();
        try {
            int num = Integer.parseInt(text.toString());
            editText.setText("");
            send(new DiapoEvent(num, soft.getId()));
        } catch (NumberFormatException e) {
            runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.diapo_text_null)));
        }
    }

    public void prec(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Prec, soft.getId()));
    }

    public void next(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Next, soft.getId()));
    }

    public void start(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Start, soft.getId()));
    }

    public void origin(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Origin, soft.getId()));
    }

    public void startHere(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.StartHere, soft.getId()));
    }

    public void last(View view) {
        send(new DiapoEvent(DiapoEvent.DiapoEventType.Last, soft.getId()));
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
