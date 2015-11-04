package view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.security.Key;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void navigation(View view) {
        Intent v;
        switch (view.getId()){
            case R.id.main_keyboard_button :
                v= new Intent(HomeActivity.this, KeyboardActivity.class);
                startActivity(v);
                break;
            case  R.id.main_basic_button :
                v= new Intent(HomeActivity.this, BasicActivity.class);
                startActivity(v);
                break;
            case R.id.main_connexion_button:
                v= new Intent(HomeActivity.this,CoActivity.class);
                startActivity(v);
        }
    }
}
