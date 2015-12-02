package view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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
        DialogFragment d;
        Bundle b;
        switch (item.getItemId()){
            case R.id.menu_dev:
                d=new NetworkDialog();
                b=new Bundle();
                b.putString("title",getString(R.string.networkdialog_device));
                b.putStringArray("test", new String[]{"Dell petit mais puissant", "MSI tout pourri", "Papy Asus Rog", "Asus Rog disque tout mort","Asus trop vieux pour ces conneries"});
                d.setArguments(b);
                d.show(getFragmentManager(),"DiagDevice");
                break;
            case R.id.menu_net:
                d=new NetworkDialog();
                b=new Bundle();
                b.putString("title",getString(R.string.networkdialog_network));
                b.putStringArray("test",new String[]{"Ragnarok","Civil Wars", "Infinity Wars", "Winter Soldier"});
                d.setArguments(b);
                d.show(getFragmentManager(),"DiagNet");
                break;
            case R.id.menu_serv:
                d=new ServiceDialog();
                d.show(getFragmentManager(),"ServiceDiag");
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void navigation(View view) {
        Intent v;
        switch (view.getId()){
            case  R.id.main_basic_button :
                v= new Intent(HomeActivity.this, BasicActivity.class);
                startActivity(v);
                break;
            case R.id.main_connexion_button:
                v= new Intent(HomeActivity.this,CoActivity.class);
                startActivity(v);
                break;
            case R.id.main_projo_button :
                v=new Intent(HomeActivity.this, ProjoActivity.class);
                startActivity(v);
                break;
        }
    }
}
