package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by Valentin on 02/12/2015.
 */
public class NetworkDialog extends DialogFragment {
    private String titre;
    private boolean args;
    private String[] test;



    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        titre=args.getString("title");
        test=args.getStringArray("test");
        if(titre!=null && test!=null)
            this.args=true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
        } catch (NullPointerException e){
            Log.d("NetDiag","Args missing");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(!args)
            return null;
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(titre)
                .setItems(test,null);

        return builder.create();
    }
}
