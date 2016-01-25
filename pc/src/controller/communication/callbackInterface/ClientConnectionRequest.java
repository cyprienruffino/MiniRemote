package controller.communication.callbackInterface;

import java.net.InetAddress;

/**
 * Created by Valentin on 24/01/2016.
 */
public interface ClientConnectionRequest {
    void onClientConnection(int port, InetAddress address);
}
