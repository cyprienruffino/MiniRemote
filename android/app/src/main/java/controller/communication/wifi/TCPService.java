package controller.communication.wifi;

import android.os.AsyncTask;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import controller.communication.callbackInterface.ErrorInterface;
import controller.communication.callbackInterface.SendFinished;
import controller.communication.events.EventWrapper;

/**
 * Created by Valentin on 23/01/2016.
 */
public class TCPService extends SurService {
    private boolean running = false;
    private Socket socket;
    private ObjectOutputStream outputStream;

    public synchronized void send(EventWrapper e, SendFinished callback, ErrorInterface errorInterface) {
        if (running) {
            new AsyncTask<EventWrapper, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (outputStream == null) {
                        try {
                            outputStream = new ObjectOutputStream(socket.getOutputStream());
                            outputStream.flush();
                        } catch (IOException e) {
                            if (errorInterface != null)
                                errorInterface.onError("Erreur inconu");
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                protected Void doInBackground(EventWrapper... params) {
                    for (EventWrapper e : params) {
                        try {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (callback != null) {
                        callback.onSendFinished();
                    }
                }
            }.execute(e);
        }
    }

    public void stop() {
        running = false;
    }

    public void startServer(int port, InetAddress address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(address, port);
                    running = true;
                    new Thread(new InputRun(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private class InputRun implements Runnable {
        private Socket socket;

        public InputRun(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.flush();
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                while (running) {
                    Object o;
                    try {
                        o = inputStream.readObject();
                        System.out.println(o);
                    } catch (EOFException e) {
                        System.out.println("EOFException");
                        running = false;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
