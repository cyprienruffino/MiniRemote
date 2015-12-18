package view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import controller.communication.events.MouseClickEvent;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 02/11/2015.
 */
public class BasicActivity extends Activity {
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
                        Log.d("MOVEMENT", "x : "+event.getX()+" y :"+event.getY());
                        return true;
                    default :
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("TEST", "onKeyDown");
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
        MouseClickEvent e = new MouseClickEvent(0, MouseClickEvent.MOUSE_CONTEXT);
    }

    public void gauche(View view) {
    }
}
