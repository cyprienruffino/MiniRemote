package controller.communication;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


public class Discovery {
    public static final int RECEIVING_TIMEOUT = 10000;
    public static final int RECEIVING_TIMEOUT_SERVER = 30000;
    private static final int mPort= 8888;
    private static final int EPSON_VP_PORT = 3629;
    private DatagramSocket socket;
    private Context mContext;
    private String ipServer;

    public String getIpServer() {
        return ipServer;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }

    public Discovery(Context c) {
        System.out.println("in discovry");
        mContext = c;
        try {
            socket = new DatagramSocket(mPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void stop() {

        try
        {
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getServerIp() {
        try
        {
            DatagramPacket packet = sendBroadcast("Ping");

            return packet.getAddress().getHostAddress();
        }
        catch (InterruptedIOException ie)
        {
            Log.d("ERROR", "No server found");

            try
            {
                socket.close();
            }
            catch (Exception e2) {}
            ie.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("ERROR", "Verify your Wifi connection");
            try
            {
                socket.close();
            }
            catch (Exception e2) {}
            e.printStackTrace();
        }

        stop();
        return null;
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

    private InetAddress getBroadcastAddress() throws Exception {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    private DatagramPacket sendBroadcast(String data) throws Exception {

        socket.setBroadcast(true);
        InetAddress broadcastAdress = getBroadcastAddress();
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), broadcastAdress, mPort);
        socket.send(packet);

        socket.setSoTimeout(RECEIVING_TIMEOUT);
        byte[] buf = new byte[1024];
        DatagramPacket rec=new DatagramPacket(buf, buf.length);;
        String myAdress =getMyAdresseIP();
        socket.receive(rec);
        while (rec.getAddress().getHostAddress().contains(myAdress))
            socket.receive(rec);
        stop();
        return rec;
    }
}
