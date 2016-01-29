package model;

import controller.communication.events.RemoteEvent;
import controller.communication.events.ResponseEvent;
import controller.communication.events.RuntimeOutputEvent;
import main.Controller;

import java.awt.*;
import java.io.*;

/**
 * Created by cyprien on 24/10/15.
 */
public class ShellModule {
    //Singleton
    private static ShellModule instance;
    private Process currentProcess;
    private Thread inputThread;
    private Thread runChecker;
    private BufferedWriter writer;
    private boolean stop = false;
    private boolean running = false;

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

    private void send(RemoteEvent event) {
        Controller.getInstance().send(event);
    }

    /**
     * Execute a command with host command processor
     *
     * @param command Command to execute
     * @throws IOException
     */
    public void execute(String command) throws IOException {
        if(command==null ||command.equals(""))
            return;
        if (!running) {
            try {
                currentProcess = Runtime.getRuntime().exec(command);
                send(new RuntimeOutputEvent(command));
                InputStream processInput = currentProcess.getInputStream();
                inputThread = new Thread(new ProcessInputReader(processInput));
                inputThread.start();
                runChecker = new Thread(new ProcessRunner());
                runChecker.start();
                writer = new BufferedWriter(new OutputStreamWriter(currentProcess.getOutputStream()));
            } catch (IOException e) {
                send(new ResponseEvent(ResponseEvent.Response.Failure));
            }
            stop = false;
            running = true;
        } else {
            writer.write(command);
            send(new RuntimeOutputEvent(command));
        }
    }

    /**
     * Kill
     */
    public void killCurrentProcess() {
        stop = true;
        if (currentProcess != null)
            currentProcess.destroy();
        currentProcess = null;
    }

    /**
     * Close running ProcessInputReader
     */
    public void closeInputThread() {
        stop = true;
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
            try {
                while ((output = stream.readLine()) != null && !stop) {
                    output = "test";
                    send(new RuntimeOutputEvent(output));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ProcessRunner implements Runnable {

        @Override
        public void run() {
            try {
                currentProcess.waitFor();
                running = false;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}