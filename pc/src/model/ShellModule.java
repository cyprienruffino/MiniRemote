package model;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import controller.communication.events.EventWrapper;
import controller.communication.events.RuntimeOutputEvent;
import main.Controller;

/**
 * Created by cyprien on 24/10/15.
 */
public class ShellModule {
    //Singleton
    private static ShellModule instance;

    private ShellModule() throws AWTException {
    }

    /**
     * Singleton getter
     *
     * @return Instance of the module
     * @throws AWTException
     */
    public static ShellModule getInstance() throws AWTException {
        if (instance == null) instance = new ShellModule();
        return instance;
    }

    private Process currentProcess;
    private Thread inputThread;
    private Thread runChecker;
    private BufferedWriter writer;
    private boolean stop = false;
    private boolean running = false;

    /**
     * Execute a command with host command processor
     *
     * @param command Command to execute
     * @throws IOException
     */
    public void execute(String command) throws IOException {
        if(!running) {
            try {
                currentProcess = Runtime.getRuntime().exec(command);
                InputStream processInput = currentProcess.getInputStream();
                inputThread = new Thread(new ProcessInputReader(processInput));
                inputThread.run();
                runChecker=new Thread(new ProcessRunner());
                runChecker.start();
                writer=new BufferedWriter(new OutputStreamWriter(currentProcess.getOutputStream()));
                Controller.getInstance().send(new EventWrapper(new RuntimeOutputEvent(command)));
            } catch (IOException e) {
                Controller.getInstance().send(new EventWrapper(new RuntimeOutputEvent("Commande inconnue")));
            }
            stop = false;
            running=true;
        }else{
            writer.write(command);
            Controller.getInstance().send(new EventWrapper(new RuntimeOutputEvent(command)));
        }
    }

    /**
     * Kill
     */
    public void killCurrentProcess() {
        stop=true;
        currentProcess.destroy();
        currentProcess = null;
        runChecker.interrupt();
    }

    /**
     * Close running ProcessInputReader
     */
    public void closeInputThread(){
        stop=true;
    }

    private class ProcessInputReader implements Runnable {

        private BufferedReader stream;
        private String output = "";
        private RuntimeOutputEvent runtimeOutputEvent;

        public ProcessInputReader(InputStream stream) throws UnsupportedEncodingException {
            this.stream = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        }


        @Override
        public void run() {
            while(!stop) {
                try {
                    while ((output=stream.readLine())!=null) {
                        //System.out.println(output);
                        runtimeOutputEvent=new RuntimeOutputEvent(output);
                        Controller.getInstance().send(new EventWrapper(runtimeOutputEvent));
                        System.out.println(runtimeOutputEvent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ProcessRunner implements Runnable{

        @Override
        public void run() {
            try {
                currentProcess.waitFor();
                running=false;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}