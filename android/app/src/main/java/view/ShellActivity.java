package view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import controller.Controller;
import controller.communication.callbackInterface.ErrorInterface;
import controller.communication.events.CommandEvent;
import controller.communication.events.CommandKillEvent;
import controller.communication.events.EventWrapper;
import controller.communication.events.RemoteEvent;
import controller.communication.wifi.TCPService;
import orleans.info.fr.remotecontrol.R;


/**
 * Created by Valentin on 21/01/2016.
 */
public class ShellActivity extends Activity implements ErrorInterface {
    public static boolean isRunning=false;
    public static ShellActivity instance=null;
    private TCPService tcpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shell);
        tcpService = Controller.getTcpService();
        if (tcpService == null) {
            runOnUiThread(new ToastRunnable(getApplicationContext(), getString(R.string.no_tcp_service)));
        }
        isRunning=true;
        ShellActivity.instance=this;
    }

    @Override
    protected void onStop(){
        super.onStop();
        isRunning=false;
    }

    private void send(RemoteEvent e) {
        if (tcpService != null) {
            tcpService.send(new EventWrapper(e), this);
        }

    }

    @Override
    public void onError(String message) {
        runOnUiThread(new ToastRunnable(this, message));
    }


    public void sendCommand(View view) {
        EditText text= (EditText) findViewById(R.id.edit_command);
        send(new CommandEvent(text.getText().toString()));
        text.setText("");
    }


    public void kill(View view) {
        send(new CommandKillEvent());
    }

    public void writeToTerminal(String text){
        TextView textView= (TextView) findViewById(R.id.shell_view);
        String newText= ((String) textView.getText()).concat(System.getProperty("line.separator")+" "+text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(newText);
            }
        });
    }
}
