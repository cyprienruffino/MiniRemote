package view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import orleans.info.fr.remotecontrol.R;

/**
 * Created by corentin on 19/12/15.
 */
public class ChangeThemeDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] tab={getString(R.string.lightTheme), getString(R.string.darkTheme)};
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.titre_theme)
                .setItems(tab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            //TODO a refaire
                            case 0:
                                getActivity().setTheme(R.style.AppTheme_Light);
                                break;
                            case 1:
                                getActivity().setTheme(R.style.AppTheme);
                                break;
                        }
                    }
                });
        return builder.create();
    }

}
