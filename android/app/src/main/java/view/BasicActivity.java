package view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.widget.Toast;
import controller.Controller;
import controller.communication.events.ActionException;
import controller.communication.events.EventWrapper;
import controller.communication.events.KeyboardEvent;
import controller.communication.events.MouseClickEvent;
import controller.communication.events.MoveMouseEvent;
import controller.communication.events.ResolutionEvent;
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
                        //Log.d("MOVEMENT", "x : " + event.getX() + " y :" + event.getY());
                        if (tcpService != null)
                            tcpService.send(new EventWrapper(new MoveMouseEvent(event.getX(), event.getY())));
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
        Log.wtf("DEVICE","Height : "+display.heightPixels+" Width : "+display.widthPixels);
        if (tcpService != null)
            tcpService.send(new EventWrapper(new ResolutionEvent(display.heightPixels, display.widthPixels)));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("TEST", "onKeyDown");
        int key = getKeyIntToSend(keyCode, event);
        Log.wtf("KEY : ", String.valueOf(keyCode));
        Log.wtf("UNICODE KEY : ", String.valueOf(key));
        try {
            tcpService.send(new EventWrapper(new KeyboardEvent((char) key, KeyboardEvent.KEY_HIT)));
        } catch (ActionException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.d("BASIC ACTIVITY", "TcpService pas encore lancé");
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
        tcpService.send(new EventWrapper(new MouseClickEvent(1, MouseClickEvent.MOUSE_HIT)));
    }

    public void gauche(View view) {
        tcpService.send(new EventWrapper(new MouseClickEvent(0, MouseClickEvent.MOUSE_HIT)));
    }

    private int getKeyIntToSend(int keyCode, KeyEvent event) {
        if (keyCode == 67) return 8; //Isolation du backspace
        return event.getUnicodeChar();
    }
}