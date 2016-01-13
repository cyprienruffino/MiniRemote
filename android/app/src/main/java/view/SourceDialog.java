package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import controller.Controller;
import controller.communication.events.EventWrapper;
import controller.communication.events.ProjectorEvent;
import controller.communication.events.RemoteEvent;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 12/01/2016.
 */
public class SourceDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] tab = {getString(R.string.source_pc), getString(R.string.source_HDMI), getString(R.string.source_video), getString(R.string.getsource)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.source_title)
                .setItems(tab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //Ordinateur
                                send(new ProjectorEvent(ProjectorEvent.SET_SOURCE_PC));
                                break;
                            case 1:
                                //HDMI
                                send(new ProjectorEvent(ProjectorEvent.SET_SOURCE_HDMI));
                                break;
                            case 2:
                                //Video
                                send(new ProjectorEvent(ProjectorEvent.SET_SOURCE_VIDEO));
                                break;
                            case 3:
                                //Obtenir source
                                send(new ProjectorEvent(ProjectorEvent.GET_SOURCE));
                                break;
                        }
                    }
                });
        return builder.create();
    }
    private void send(RemoteEvent event){
        try {
            Controller.getTcpService().send(new EventWrapper(event));
        } catch (NullPointerException e){
            getActivity().runOnUiThread(new ToastRunnable(getActivity().getApplicationContext(),getString(R.string.no_tcp_service)));
        }
    }
}
