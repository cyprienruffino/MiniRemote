package controller.communication.wifi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * Created by whiteshad on 23/12/15.
 */


public class Discovery implements Runnable{
    private static final int RECEIVING_TIMEOUT = 10000;
    private static final int EPSON_VP_PORT = 3629;
    private DatagramSocket socket;
    private String ipServer;


    public Discovery() {

        try {
            socket = new DatagramSocket(EPSON_VP_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public String getIpServer() {
        return ipServer;
    }


    public String getMyAdresseIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private DatagramPacket helloPacket (){
        byte[] data=new byte[16];
        //Protocol identifier
        String s="ESC/VP.net";
        byte[] protocol = s.getBytes(StandardCharsets.US_ASCII);
        for(int i=0;i<10;i++){
            data[i]=protocol[i];
        }
        int i=10;
        //Version identifier
        data[i++]=0x10;
        //Type identifier: HELLO
        data[i++]=1;
        //reserved
        data[i++]=0;
        data[i++]=0;
        //Status code: Always set 0x00 since it is a request
        data[i++]=0x00;
        //Number of headers: No headers
        data[i++]=0;
        try {
            return new DatagramPacket(data,data.length, InetAddress.getByName("255.255.255.255"),EPSON_VP_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DatagramPacket sendBroadcast() throws Exception {

        socket.setBroadcast(true);
        DatagramPacket packet = helloPacket();
        socket.send(packet);

        socket.setSoTimeout(RECEIVING_TIMEOUT);
        byte[] buf = new byte[1024];
        DatagramPacket rec = new DatagramPacket(buf, buf.length);
        String myAdress =getMyAdresseIP();
        socket.receive(rec);
        while (rec.getAddress().getHostAddress().contains(myAdress))
            socket.receive(rec);

        return rec;
    }

    private  String getServerIp() {
        try {
            DatagramPacket packet = sendBroadcast();
            return packet.getAddress().getHostAddress();
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void run() {
        ipServer=getServerIp();
    }

}
