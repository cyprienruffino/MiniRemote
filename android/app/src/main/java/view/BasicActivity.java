package view;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import controller.Controller;
import controller.communication.events.*;
import controller.communication.wifi.TCPService;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 02/11/2015.
 */
public class BasicActivity extends Activity {
    TCPService tcpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic);
        findViewById(R.id.basic_move).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        send(new MoveMouseEvent(event.getX(),event.getY()));
                        return true;
                    default:
                        return false;
                }
            }
        });

        tcpService = Controller.getTcpService();
        if (tcpService == null)
            Toast.makeText(this, getString(R.string.no_tcp_service), Toast.LENGTH_SHORT).show();
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        send(new ResolutionEvent(display.heightPixels,display.widthPixels));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int key = getKeyIntToSend(keyCode, event);
        try {
            send(new KeyboardEvent((char) key, KeyboardEvent.KEY_HIT));
        } catch (ActionException e) {
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Log.d("TEST", "onKeyLongPress");
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("TEST", "onKeyUp");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        Log.d("TEST", "onKeyMultiple");
        if (repeatCount < 3)
            onKeyDown(keyCode, event);
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        Log.d("TEST", "onKeyShortcut");
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("TEST", "onBackPressed");
    }

    public void clavier(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void droite(View view) {
        send(new MouseClickEvent(1, MouseClickEvent.MOUSE_HIT));
    }

    public void gauche(View view) {
        send(new MouseClickEvent(0, MouseClickEvent.MOUSE_HIT));
    }

    private int getKeyIntToSend(int keyCode, KeyEvent event) {
        if (keyCode == 67) return 8; //Isolation du backspace
        return event.getUnicodeChar();
    }

    private void send(RemoteEvent e) {
        if (tcpService != null)
            tcpService.send(new EventWrapper(e));
    }
}