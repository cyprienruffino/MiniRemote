package view;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Valentin on 04/01/2016.
 */
public class ToastRunnable implements Runnable {
    private String string;
    private Context context;
    private int length;

    public ToastRunnable(Context context, String string) {
        this.context = context;
        this.string = string;
        length = Toast.LENGTH_SHORT;
    }

    public ToastRunnable(String string, Context context, int length) {
        this.string = string;
        this.context = context;
        this.length = length;
    }

    @Override
    public void run() {
        Toast.makeText(context, string, length).show();
    }
}
