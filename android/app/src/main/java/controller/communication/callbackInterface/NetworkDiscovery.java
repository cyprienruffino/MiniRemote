package controller.communication.callbackInterface;

import java.net.InetAddress;

/**
 * Created by Valentin on 02/01/2016.
 */
public interface NetworkDiscovery {
     void onNetworkFound(InetAddress address, int port);

     void onNoNetworkFound();
}
