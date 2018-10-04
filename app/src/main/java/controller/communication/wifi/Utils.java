package controller.communication.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * Created by Valentin on 24/01/2016.
 * Don't try to instantiate this class !
 */
public class Utils {
    private Utils() {
    }

    /**
     * Get broadcast ip adress
     *
     * @param context android context
     * @return broadcast ip adress
     * @throws UnknownHostException
     */
    public static InetAddress getBroadcastAddress(Context context) throws UnknownHostException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    /**
     * Get its ip adress
     *
     * @param context android context
     * @return Its ip
     * @throws UnknownHostException
     */
    public static String wifiIpAddress(Context context) throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        return InetAddress.getByAddress(ipByteArray).getHostAddress();
    }
}
