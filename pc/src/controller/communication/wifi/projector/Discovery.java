package controller.communication.wifi.projector;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import controller.communication.events.ProjectorReturnEvent;
import controller.communication.events.RemoteEvent;

/**
 * Created by whiteshad on 23/12/15.
 */


public class Discovery implements Runnable {
    private static final int RECEIVING_TIMEOUT = 10000;
    private static final int EPSON_VP_PORT = 55799;
    private static final int EPSON_VP_PORT_TCP = 3629;
    private DatagramSocket socket;
    private String ipServer;
    private RemoteEvent result;



    public Discovery() {

        try {
            socket = new DatagramSocket(EPSON_VP_PORT_TCP);
        } catch (SocketException e){
            e.printStackTrace();
        }
    }

    public String getIpServer() {
        return ipServer;
    }


    public String getMyAdresseIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DatagramPacket helloPacket() {
        byte[] data = new byte[16];
        //Protocol identifier
        String s = "ESC/VP.net";
        byte[] protocol = s.getBytes(StandardCharsets.US_ASCII);
        for (int i = 0; i < 10; i++) {
            data[i] = protocol[i];
        }
        int i = 10;
        //Version identifier
        data[i++] = 0x10;
        //Type identifier: HELLO
        data[i++] = 1;
        //reserved
        data[i++] = 0;
        data[i++] = 0;
        //Status code: Always set 0x00 since it is a request
        data[i++] = 0x00;
        //Number of headers: No headers
        data[i++] = 0;
        try {
            return new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), EPSON_VP_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DatagramPacket sendBroadcast(){
    try {
        socket.setBroadcast(true);
        DatagramPacket packet = helloPacket();
        socket.send(packet);

        socket.setSoTimeout(RECEIVING_TIMEOUT);
        byte[] buf = new byte[1024];
        DatagramPacket rec = new DatagramPacket(buf, buf.length);
        String myAdress = getMyAdresseIP();
        socket.receive(rec);
        while (rec.getAddress().getHostAddress().contains(myAdress))
            socket.receive(rec);
        return rec;
    }catch (SocketTimeoutException e) {
        result=new ProjectorReturnEvent(ProjectorReturnEvent.ERROR_TIMEOUT,null);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
    }

    private String getServerIp() {
        try {
            DatagramPacket packet = sendBroadcast();
            analyseReponse(packet.getData());
            return packet.getAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void analyseReponse(byte[] rec) {
        switch (rec[14]) {
            case 0x20://ok
                result = new ProjectorReturnEvent(ProjectorReturnEvent.PROJECTOR_NAME,new String(rec, 17, 16));
                break;
            default://ko
                result = new ProjectorReturnEvent(ProjectorReturnEvent.ERROR_HELLO,null);;
                break;
        }
    }

    public RemoteEvent getResult() {
        return result;
    }

    @Override
    public void run() {
        ipServer = getServerIp();
    }

}

