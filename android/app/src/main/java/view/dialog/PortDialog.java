package view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.EditText;

import controller.Controller;
import orleans.info.fr.remotecontrol.R;
import view.ToastRunnable;
import view.activities.HomeActivity;

/**
 * Created by Valentin on 26/01/2016.
 */
public class PortDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText editText = new EditText(getActivity().getApplicationContext());
        editText.setText(Controller.getPort() + "");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(editText)
                .setTitle(R.string.port)
                .setMessage(getString(R.string.port_label))
                .setPositiveButton(R.string.valider, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int port = Integer.valueOf(editText.getText().toString());
                            if (port == 1337)
                                getActivity().runOnUiThread(new ToastRunnable(getActivity().getApplicationContext(), getActivity().getString(R.string.egg_leet)));
                            if (port == 31415)
                                getActivity().runOnUiThread(new ToastRunnable(getActivity().getApplicationContext(), getActivity().getString(R.string.egg_pi)));
                            if (port == 16180)
                                getActivity().runOnUiThread(new ToastRunnable(getActivity().getApplicationContext(), getActivity().getString(R.string.egg_or)));
                            Controller.setPort(port);
                            dismiss();
                            ((HomeActivity) getActivity()).restartCom();
                        } catch (NumberFormatException e) {
                            getActivity().runOnUiThread(new ToastRunnable(getActivity().getApplicationContext(), getString(R.string.port_needed)));
                        }
                    }
                })
                .setNegativeButton(R.string.annuler, null)
                .setNeutralButton(R.string.defaut, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Controller.setDefaultPort();
                    }
                });
        return builder.create();
    }
}

