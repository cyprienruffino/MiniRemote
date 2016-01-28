package controller.communication.wifi;

import android.os.AsyncTask;
import controller.Controller;
import controller.FailureException;
import controller.communication.callbackInterface.ErrorInterface;
import controller.communication.callbackInterface.SendFinished;
import controller.communication.events.EventWrapper;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Valentin on 23/01/2016.
 */
public class TCPService extends SurService {
    private boolean running = false;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public void send(EventWrapper e) {
        privateSend(e, null, null);
    }

    public void send(EventWrapper e, ErrorInterface errorInterface) {
        privateSend(e, null, errorInterface);
    }

    public void send(EventWrapper e, SendFinished s) {
        privateSend(e, s, null);
    }

    public void send(EventWrapper e, ErrorInterface errorInterface, SendFinished s) {
        privateSend(e, s, errorInterface);
    }

    private synchronized void privateSend(EventWrapper e, SendFinished callback, ErrorInterface errorInterface) {
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
                                errorInterface.onError("Erreur inconu 1");
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
        try {
            if (outputStream != null)
                outputStream.close();
            if (inputStream != null)
                inputStream.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                inputStream = new ObjectInputStream(socket.getInputStream());
                running = true;
                while (running) {
                    Object o;
                    try {
                        o = inputStream.readObject();
                        Controller.execute((EventWrapper) o);
                    } catch (FailureException e) {
                        try {
                            Controller.getErrorInterface().onError("Failure");
                        } catch (NullPointerException e2) {

                        }
                    } catch (EOFException e) {
                        running = false;
                        Controller.onClientDisconnection();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SocketException e) {
                Controller.onClientDisconnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
