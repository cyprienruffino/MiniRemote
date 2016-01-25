package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

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
}
