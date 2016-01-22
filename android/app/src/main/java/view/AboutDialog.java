package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 22/01/2016.
 */
public class AboutDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.credit)
                .setMessage(R.string.crd);
        return builder.create();
    }
}
