package view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.net.InetAddress;

import view.ToastRunnable;
import view.activities.HomeActivity;

/**
 * Created by Valentin on 24/01/2016.
 */
public class ServerDialog extends DialogFragment {
    public static final String nameTag = "TAGARG1";
    public static final String adressTag = "TAGARG2";
    public static final String portTag = "TAGARG3";
    private boolean args = false;

    private String[] name;
    private String[] adress;
    private int[] port;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
        } catch (NullPointerException e) {
            getActivity().runOnUiThread(new ToastRunnable(getActivity().getApplicationContext(), "Args Missing"));
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        name = args.getStringArray(nameTag);
        adress = args.getStringArray(adressTag);
        port = args.getIntArray(portTag);
        if (name != null && port != null && adress != null)
            this.args = true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (!args)
            return null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choisir serveur")
                .setItems(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HomeActivity act = (HomeActivity) getActivity();
                        act.connectTo(adress[which], port[which]);
                    }
                });

        return builder.create();
    }

    public void refresh(InetAddress address, int port) {
        int length = this.adress.length + 1;
        String[] a = new String[length];
        int[] p = new int[length];
        String[] n = new String[length];
        //name
        for (int i = 0; i < name.length; i++) {
            n[i] = name[i];
        }
        n[length - 1] = address.getHostName();
        //address
        for (int i = 0; i < adress.length; i++) {
            a[i] = adress[i];
        }
        a[length - 1] = address.getHostAddress();
        //port
        for (int i = 0; i < this.port.length; i++) {
            p[i] = this.port[i];
        }
        p[length - 1] = port;

        //on remet les param et on update
        Bundle b = new Bundle();
        b.putIntArray(ServerDialog.portTag, p);
        b.putStringArray(ServerDialog.nameTag, n);
        b.putStringArray(ServerDialog.adressTag, a);
        ServerDialog serverDialog = new ServerDialog();
        serverDialog.setArguments(b);
        serverDialog.show(getFragmentManager(), HomeActivity.SERVER_SCREEN);
        dismiss();

    }
}
