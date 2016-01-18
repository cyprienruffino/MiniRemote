package model;

import controller.communication.wifi.Discovery;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cyprien on 01/01/16.
 */

public class ProjectorModule {
    public static final String SET_SOURCE_PC="1F";
    public static final String SET_SOURCE_HDMI="30";
    public static final String SET_SOURCE_VIDEO="41";


    private class ProjectorTCPServer {

        private static final int EPSON_VP_PORT = 3629;


        private Thread serverOutputThread;
        private Socket socket = null;
        private ServerSocket serverSocket = null;
        private List<byte[]> events;
        private Object lock = new Object();
        private DataOutputStream out;
        private boolean closed = false;

        public boolean isClosed() {
            return closed;
        }

        public ProjectorTCPServer() {
            events = Collections.synchronizedList(new ArrayList<>());
        }

        public void startServer() throws IOException, InterruptedException {

            Discovery discovery = new Discovery();
            Thread thread = new Thread(discovery);
            thread.start();
            thread.join();
            String IP = discovery.getIpServer();

            socket=new Socket(IP, EPSON_VP_PORT);
            out = new DataOutputStream(socket.getOutputStream());
            out.flush();
            System.out.println("OutputStream projecteur Instancié");
            ServerOutput actionOutput = new ServerOutput(out, events, lock, this);

            serverOutputThread = new Thread(actionOutput, "OuputThread");
            serverOutputThread.start();
            System.out.println("Threads projecteur lancés");
        }

        public void send(byte[] byteArray) {
            synchronized (lock) {
                events.add(byteArray);
                lock.notifyAll();
            }
        }


        public void stop() throws IOException {
            //send(new EventWrapper(new ResponseEvent(ResponseEvent.SERVER_SHUTDOWN)));
            closed = true;
            if (serverOutputThread != null)
                serverOutputThread.interrupt();
            if (out != null)
                out.close();
            if (serverSocket != null)
                serverSocket.close();
            if (socket != null)
                socket.close();
            System.out.println("serverOuputThread projecteur : " + (serverOutputThread == null ? "null" : serverOutputThread.getState()));
            System.out.println("serverSocket projecteur : " + (serverSocket == null ? "null" : serverSocket.isClosed()));
            System.out.println("socket projecteur : " + (socket == null ? "null" : socket.isClosed()));
        }


        private class ServerOutput implements Runnable {
            private List<byte[]> events;
            byte[] event;
            private DataOutputStream out;
            private Object lock;
            private ProjectorTCPServer server;

            public ServerOutput(DataOutputStream out, List<byte[]> events, Object lock, ProjectorTCPServer server) throws IOException {
                this.server = server;
                this.out = out;
                this.events = events;
                this.lock = lock;
                System.out.println("OutputThread Projecteur instancié");
            }

            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        while (true) {
                            lock.wait();
                            while (!events.isEmpty()) {
                                event = events.remove(0);
                                if (event != null)
                                    out.write(event);
                                System.out.println("Projector event envoyé");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.err.println("Projector thread Interrupted");
                    //e.printStackTrace();
                }
            }
        }
    }

    private ProjectorModule() throws IOException, InterruptedException {
        server=new ProjectorTCPServer();
        server.startServer();
    }
    private static ProjectorModule instance;

    private ProjectorTCPServer server;

    public static ProjectorModule getInstance() throws IOException, InterruptedException {
        if(instance==null)
            instance=new ProjectorModule();
        return instance;
    }

    private void sendByteArray(byte[] bytes){
        server.send(bytes);
    }

    public void sendPowerOn(){
        String pwrOn="PWR ON\n";
        byte [] b=pwrOn.getBytes(StandardCharsets.US_ASCII);
        sendByteArray(b);
    }
    public void sendPowerOff(){
        String pwrOff="PWR OFF\n";
        byte [] b=pwrOff.getBytes(StandardCharsets.US_ASCII);
        sendByteArray(b);
    }

    public void sendGetSource(){
        String getSrc="SOURCE?\n";
        byte [] b=getSrc.getBytes(StandardCharsets.US_ASCII);
        sendByteArray(b);
    }

    public void sendSetSource(String src){
        String getSrc="SOURCE "+src+"\n";
        byte [] b=getSrc.getBytes(StandardCharsets.US_ASCII);
        sendByteArray(b);
    }

    public void stop() throws IOException {
        server.stop();
    }

}
