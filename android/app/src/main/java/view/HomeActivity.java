package view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import controller.Controller;
import controller.communication.events.EventWrapper;
import controller.communication.events.ResponseEvent;
import controller.communication.wifi.TCPService;
import orleans.info.fr.remotecontrol.R;

public class HomeActivity extends Activity implements ServiceAttached, NetworkDiscovery {
    private TCPService tcpService;
    private ServiceConnection sc = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            tcpService = ((TCPService.TCPBinder) service).getService();
            onServiceAttached();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        DialogFragment dialogFragment = new ServiceDialog();
        dialogFragment.show(getFragmentManager(), "ServiceDialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ONACTIVITYRESULT", requestCode + " " + resultCode + " " + data.toString());
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DialogFragment d;
        Bundle b;
        switch (item.getItemId()) {
            case R.id.menu_serv:
                d = new ServiceDialog();
                d.show(getFragmentManager(), "ServiceDiag");
                break;
            case R.id.menu_dc:
                unbindTcpService();
                Toast.makeText(getApplicationContext(), getString(R.string.dc), Toast.LENGTH_SHORT);
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void navigation(View view) {
        Intent v;
        switch (view.getId()) {
            case R.id.main_basic_button:
                v = new Intent(HomeActivity.this, BasicActivity.class);
                startActivity(v);
                break;
            case R.id.main_projo_button:
                v = new Intent(HomeActivity.this, ProjoActivity.class);
                startActivity(v);
                break;
        }
    }

    public ServiceConnection getSc() {
        return sc;
    }

    @Override
    public void onServiceAttached() {
        DialogFragment dialog = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.waiting, null));
                return builder.create();
            }
        };
        dialog.show(getFragmentManager(), WAITING_SCREEN);
        tcpService.startServer(this);
        Controller.setTcpService(tcpService);
        Controller.isServiceStarted = true;

    }

    private final String WAITING_SCREEN = "waiting_screen";
    @Override
    public void onNetworkFound() {
        DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(WAITING_SCREEN);
        dialog.dismiss();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.server_found), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNoNetworkFound() {
        DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(WAITING_SCREEN);
        dialog.dismiss();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.no_server_found, Toast.LENGTH_SHORT).show();
            }
        });
        unbindTcpService();
    }

    @Override
    protected void onDestroy() {
        unbindTcpService();
        super.onDestroy();
    }

    public void unbindTcpService() {
        try {
            tcpService.send(new EventWrapper(new ResponseEvent(ResponseEvent.SERVICE_SHUTDOWN)));
            tcpService.stop();
            tcpService = null;
            Controller.setTcpService(tcpService);
            Controller.isServiceStarted = false;
            unbindService(sc);
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e) {
        }
    }
}
