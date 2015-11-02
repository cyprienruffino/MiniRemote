package view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 22/10/2015.
 */
public class KeyboardActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clavier);
        EditText editText = (EditText) findViewById(R.id.keyboard_edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    public void send(View view) {
        EditText editText = (EditText) findViewById(R.id.keyboard_edittext);
        String toSend= editText.getText().toString();
        editText.setText("");
    }
}
