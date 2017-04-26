package view.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import controller.Controller;
import controller.communication.callbackInterface.ErrorInterface;
import controller.communication.events.EventWrapper;
import controller.communication.events.KeyboardEvent;
import controller.communication.events.MouseClickEvent;
import controller.communication.events.MoveMouseEvent;
import controller.communication.events.RemoteEvent;
import controller.communication.events.ResolutionEvent;
import controller.communication.wifi.TCPService;
import orleans.info.fr.remotecontrol.R;
import view.ToastRunnable;

/**
 * Created by Valentin on 02/11/2015.
 */
public class BasicActivity extends Activity implements ErrorInterface {
    private TCPService tcpService;

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
                        send(new MoveMouseEvent(event.getX(), event.getY()));
                        return true;
                    default:
                        return false;
                }
            }
        });
        findViewById(R.id.basic_gauche).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        send(new MouseClickEvent(MouseClickEvent.MouseAction.Press, MouseClickEvent.MouseButton.Left));
                        break;
                    case MotionEvent.ACTION_UP:
                        send(new MouseClickEvent(MouseClickEvent.MouseAction.Release, MouseClickEvent.MouseButton.Left));
                        break;
                }
                return true;
            }
        });

        tcpService = Controller.getTcpService();
        if (tcpService == null)
            Toast.makeText(this, getString(R.string.no_tcp_service), Toast.LENGTH_SHORT).show();
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        send(new ResolutionEvent(display.heightPixels, display.widthPixels));
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        int n = event.getCharacters().length();
        for (int i = 0; i < n; i++) {
            int key = event.getCharacters().charAt(i);
            send(new KeyboardEvent(key, KeyboardEvent.KeyboardAction.KeyHit, null));
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        send(getKeyToSend(event, KeyboardEvent.KeyboardAction.KeyPress));
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        send(getKeyToSend(event, KeyboardEvent.KeyboardAction.KeyRelease));
        return super.onKeyUp(keyCode, event);
    }

    public void clavier(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void droite(View view) {
        send(new MouseClickEvent(MouseClickEvent.MouseAction.Hit, MouseClickEvent.MouseButton.Right));
    }

    private KeyboardEvent getKeyToSend(KeyEvent event, KeyboardEvent.KeyboardAction action) {
        int keycode = event.getUnicodeChar(event.getMetaState());
        KeyboardEvent.SpecialKey spcode = null;
        if (keycode == 0) {
            keycode = -1;
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DEL:
                    spcode = KeyboardEvent.SpecialKey.Del;
                    break;
                case KeyEvent.KEYCODE_FORWARD_DEL:
                    spcode = KeyboardEvent.SpecialKey.Forward_Del;
                    break;
                case KeyEvent.KEYCODE_CAPS_LOCK:
                    spcode = KeyboardEvent.SpecialKey.Caps_Lock;
                    break;
                case KeyEvent.KEYCODE_CTRL_LEFT:
                case KeyEvent.KEYCODE_CTRL_RIGHT:
                    spcode = KeyboardEvent.SpecialKey.Ctrl;
                    break;
                case KeyEvent.KEYCODE_TAB:
                    spcode = KeyboardEvent.SpecialKey.Tab;
                    break;
                case KeyEvent.KEYCODE_ALT_LEFT:
                    spcode = KeyboardEvent.SpecialKey.Alt;
                    break;
                case KeyEvent.KEYCODE_ALT_RIGHT:
                    spcode = KeyboardEvent.SpecialKey.Alt_Gr;
                    break;
                case KeyEvent.KEYCODE_ENTER:
                    spcode = KeyboardEvent.SpecialKey.Enter;
                    break;
                case KeyEvent.KEYCODE_MENU:
                    spcode = KeyboardEvent.SpecialKey.Context;
                    break;
            }
        }
        return new KeyboardEvent(keycode, action, spcode);
    }

    private void send(RemoteEvent e) {
        if (tcpService != null)
            tcpService.send(new EventWrapper(e), this);
    }

    @Override
    public void onError(String message) {
        runOnUiThread(new ToastRunnable(this, message));
    }
}