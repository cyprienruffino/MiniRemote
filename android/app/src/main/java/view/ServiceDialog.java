package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 02/12/2015.
 */
public class ServiceDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] tab={getString(R.string.wifi),getString(R.string.bluetooth)};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.servicedialog_title)
                .setItems(tab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Log.d("ServiceDialog","Arg 0");
                                break;
                            case 1:
                                Log.d("ServiceDialog","Arg 1");
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
