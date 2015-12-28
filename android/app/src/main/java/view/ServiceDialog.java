package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import controller.communication.wifi.TCPService;
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
                                //Wifi
                                Intent i=new Intent(getActivity().getApplicationContext(), TCPService.class);
                                getActivity().bindService(i,((HomeActivity)getActivity()).getSc(),Context.BIND_AUTO_CREATE);
                                break;
                            case 1:
                                //Bluetooth

                                break;
                        }
                    }
                });
        return builder.create();
    }
}
