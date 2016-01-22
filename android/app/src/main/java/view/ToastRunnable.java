package view;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Valentin on 04/01/2016.
 */
public class ToastRunnable implements Runnable {
    private String string;
    private Context context;

    public ToastRunnable(Context context, String string) {
        this.context = context;
        this.string = string;
    }

    @Override
    public void run() {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
}
