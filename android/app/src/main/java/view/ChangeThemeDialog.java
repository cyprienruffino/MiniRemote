package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import orleans.info.fr.remotecontrol.R;

/**
 * Created by corentin on 19/12/15.
 */
public class ChangeThemeDialog extends DialogFragment implements View.OnClickListener{

    @Override
    public void onClick(View view){
        switch (view.getId()){
            default:
            case R.id.bouttonTheme1:
                getActivity().setTheme(R.style.AppTheme_Light);
                break;
            case R.id.bouttonTheme2:
                getActivity().setTheme(R.style.AppTheme);
                break;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        getActivity().findViewById(R.id.bouttonTheme2).setOnClickListener(this);
        getActivity().findViewById(R.id.bouttonTheme1).setOnClickListener(this);
        return builder.create();
    }

}
