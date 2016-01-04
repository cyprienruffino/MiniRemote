package controller.communication.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import controller.communication.callbackInterface.NetworkDiscovery;

import java.io.InterruptedIOException;
import java.net.*;
import java.util.Enumeration;


public class Discovery implements Runnable{
    public static final int RECEIVING_TIMEOUT = 10000;
    private static final int PC_PORT = 8888;
    private static final int EPSON_VP_PORT = 3629;
    public static final int PC_REMOTE_MODE=0;
    public static final int VP_REMOTE_MODE=1;
    private DatagramSocket socket;
    private Context mContext;
    private String ipServer;
    private int port;
    private NetworkDiscovery callback;



    public Discovery(NetworkDiscovery cb,Context context,int mode) {
        callback=cb;
        //System.out.println("in discovery");
        mContext =context;
        try {
            switch (mode){
                case PC_REMOTE_MODE:
                    port=PC_PORT;
                    break;
                case VP_REMOTE_MODE:
                    port=EPSON_VP_PORT;
                    break;
            }
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
           // makeToast("ERREUR: Lancement socket client UDP");
        }
    }

    public String getIpServer() {
        return ipServer;
    }

    private void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }

    public void stop() {

        try
        {
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //makeToast("ERREUR: fermeture du client UDP");
        }
    }

    private String getServerIp() {
        try
        {
            DatagramPacket packet = sendBroadcast("Ping");
            return packet.getAddress().getHostAddress();
        }
        catch (InterruptedIOException ie)
        {
            Log.d("ERROR", "No server found");
            //makeToast("ERREUR: Aucun serveur trouvé");
            try
            {
                socket.close();
            }
            catch (Exception e2) {}
            //ie.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("ERROR", "Verify your Wifi connection");
            //makeToast("ERREUR: Vérifiez votre connexion Wifi");
            try
            {
                socket.close();
            }
            catch (Exception e2) {}
            //e.printStackTrace();
        }

        stop();
        callback.onNoNetworkFound();
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
            //e.printStackTrace();
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
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), broadcastAdress, port);
        socket.send(packet);

        socket.setSoTimeout(RECEIVING_TIMEOUT);
        byte[] buf = new byte[1024];
        DatagramPacket rec = new DatagramPacket(buf, buf.length);
        String myAdress =getMyAdresseIP();
        socket.receive(rec);
        while (rec.getAddress().getHostAddress().contains(myAdress))
            socket.receive(rec);
        stop();
        return rec;
    }

    @Override
    public void run() {
        setIpServer(getServerIp());
        if (ipServer != null)
            callback.onNetworkFound();
    }

    /*private void makeToast(String s){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t=Toast.makeText(mContext,s,Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }*/
}
