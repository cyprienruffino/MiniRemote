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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import controller.Controller;
import controller.communication.callbackInterface.*;
import controller.communication.events.EventWrapper;
import controller.communication.events.ResponseEvent;
import controller.communication.wifi.SurService;
import controller.communication.wifi.TCPService;
import controller.communication.wifi.UDPService;
import orleans.info.fr.remotecontrol.R;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HomeActivity extends Activity implements ServiceAttached, NetworkDiscovery, ErrorInterface {
    public static final String WAITING_SCREEN = "waiting_screen";
    public static final String SERVER_SCREEN = "server_screen";
    private TCPService tcpService;
    private UDPService udpService;
    private ServiceConnection udpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            udpService = (UDPService) ((SurService.SurBinder) service).getService();
            onUDPServiceAttached();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private int port;
    private InetAddress address;
    private ServiceConnection tcpServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            tcpService = (TCPService) ((SurService.SurBinder) service).getService();
            onTCPServiceAttached();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public ServiceConnection getTcpServiceConnection() {
        return tcpServiceConnection;
    }

    public ServiceConnection getUdpServiceConnection() {
        return udpServiceConnection;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        DialogFragment dialogFragment = new ServiceDialog();
        dialogFragment.show(getFragmentManager(), "ServiceDialog");
        Controller.setClientDisconnected(new ClientDisconnected() {
            @Override
            public void onDisconnection() {
                unbindUdpService();
                unbindTcpService();
                runOnUiThread(new ToastRunnable(HomeActivity.this, getString(R.string.dc)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                if (tcpService != null)
                    tcpService.send(new EventWrapper(new ResponseEvent(ResponseEvent.Response.ServiceShutdown)), this, new SendFinished() {
                        @Override
                        public void onSendFinished() {
                            unbindUdpService();
                            unbindTcpService();
                            runOnUiThread(new ToastRunnable(HomeActivity.this, getString(R.string.dc)));
                        }
                    });

                break;
            case R.id.setting:
                d = new SettingsDialog();
                d.show(getFragmentManager(), "settingDialog");
                break;
            case R.id.credit:
                d = new AboutDialog();
                d.show(getFragmentManager(), "creditDialog");
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
             case R.id.main_media_button:
                v = new Intent(HomeActivity.this, MultimediaActivity.class);
                startActivity(v);
                break;
        }
    }

    @Override
    public void onNetworkFound(InetAddress address, int port) {
        DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(WAITING_SCREEN);
        try {
            dialog.dismiss();
        } catch (NullPointerException e) {
        }
        DialogFragment serverDialog = (DialogFragment) getFragmentManager().findFragmentByTag(SERVER_SCREEN);
        if (serverDialog == null) {
            serverDialog = new ServerDialog();
            Bundle b = new Bundle();
            b.putIntArray(ServerDialog.portTag, new int[]{port});
            b.putStringArray(ServerDialog.nameTag, new String[]{address.getHostName()});
            b.putStringArray(ServerDialog.adressTag, new String[]{address.getHostAddress()});
            serverDialog.setArguments(b);
            serverDialog.show(getFragmentManager(), SERVER_SCREEN);
        } else {
            //todo update actual dialog (***)
        }
    }

    @Override
    public void onNoNetworkFound() {
        DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(WAITING_SCREEN);
        try {
            dialog.dismiss();
        } catch (NullPointerException e) {

        }
        runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.no_server_found)));
        unbindUdpService();
    }

    public void unbindUdpService() {
        if (udpService != null)
            udpService.close();
        try {
            unbindService(udpServiceConnection);
        } catch (IllegalArgumentException e) {
            //Service not registered
        }
        udpService = null;
    }

    @Override
    protected void onDestroy() {
        Controller.onClientDisconnection();
        try {
            tcpService.send(new EventWrapper(new ResponseEvent(ResponseEvent.Response.ServiceShutdown)), this, new SendFinished() {
                @Override
                public void onSendFinished() {
                    tcpService.stop();
                    tcpService = null;
                }
            });
        } catch (NullPointerException e) {
            //Tcp qui a foiré
        }
        super.onDestroy();
    }

    public void unbindTcpService() {
        Controller.isServiceStarted = false;
        Controller.setTcpService(null);
        try {
            unbindService(tcpServiceConnection);
        } catch (IllegalArgumentException e2) {
            //Service not registered
        }
    }

    @Override
    public void onError(String message) {
        runOnUiThread(new ToastRunnable(this, message));
    }

    @Override
    public void onTCPServiceAttached() {
        tcpService.startServer(port, address);
        Controller.setTcpService(tcpService);
        Controller.isServiceStarted = true;
    }

    @Override
    public void onUDPServiceAttached() {
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
        udpService.startServer(this, this, this);
    }

    public void connectTo(String adress, int port) {
        try {
            this.address = InetAddress.getByName(adress);
            this.port = port;
            udpService.sendConnect(port, address, this);
            bindService(new Intent(this, TCPService.class), tcpServiceConnection, BIND_AUTO_CREATE);
            unbindUdpService();
        } catch (UnknownHostException e) {
            runOnUiThread(new ToastRunnable(this, "Erreur dans l'adresse du serveur"));
        }
    }

    public void restartCom() {
        unbindTcpService();
        unbindUdpService();
        DialogFragment dialogFragment = new ServiceDialog();
        dialogFragment.show(getFragmentManager(), "ServiceDialog");
    }
}
