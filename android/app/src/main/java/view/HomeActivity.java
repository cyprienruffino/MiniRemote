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
import controller.Controller;
import controller.communication.callbackInterface.NetworkDiscovery;
import controller.communication.callbackInterface.SendFinished;
import controller.communication.callbackInterface.ServiceAttached;
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
        switch (item.getItemId()) {
            case R.id.menu_serv:
                d = new ServiceDialog();
                d.show(getFragmentManager(), "ServiceDiag");
                break;
            case R.id.menu_dc:
                unbindTcpService();
                runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.dc)));
                break;
            case R.id.setting:
                d = new ChangeThemeDialog();
                d.show(getFragmentManager(),"settingDialog");
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
            case R.id.main_diapo_button:
                v = new Intent(HomeActivity.this, DiapoActivity.class);
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
        try {
            dialog.dismiss();
        } catch (NullPointerException e) {

        }
        runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.server_found)));
    }

    @Override
    public void onNoNetworkFound() {
        DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(WAITING_SCREEN);
        try {
            dialog.dismiss();
        } catch (NullPointerException e) {

        }
        runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.no_server_found)));
        unbindTcpService();
    }

    @Override
    protected void onDestroy() {
        unbindTcpService();
        super.onDestroy();
    }

    public void unbindTcpService() {
        Controller.isServiceStarted = false;
        Controller.setTcpService(null);
        try {
            tcpService.send(new EventWrapper(new ResponseEvent(ResponseEvent.SERVICE_SHUTDOWN)));
            tcpService.setOnSendFinished(new SendFinished() {
                @Override
                public void onSendFinished() {
                    tcpService.stop();
                    tcpService = null;
                }
            });
        } catch (NullPointerException e) {
            //Tcp qui a foiré
        }
        try {
            unbindService(sc);
        } catch (IllegalArgumentException e2) {
            //Service not registered
        }
    }
}
