package controller.communication.callbackInterface;

/**
 * Created by Valentin on 02/01/2016.
 */
public interface ServiceAttached {
    void onTCPServiceAttached();

    void onUDPServiceAttached();
}
