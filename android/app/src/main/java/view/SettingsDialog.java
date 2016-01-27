package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 26/01/2016.
 */
public class SettingsDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] tab = {getString(R.string.theme), getString(R.string.port)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.settings)
                .setItems(tab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogFragment d;
                        switch (which) {
                            case 0:
                                d = new ChangeThemeDialog();
                                d.show(getFragmentManager(), "changeThemeDialog");
                                break;
                            case 1:
                                d = new PortDialog();
                                d.show(getFragmentManager(), "portDiaog");
                                break;
                        }
                        dismiss();
                    }
                });
        return builder.create();
    }
}
