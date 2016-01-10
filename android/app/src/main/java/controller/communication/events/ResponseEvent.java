package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class ResponseEvent extends RemoteEvent {
    public static final String SERVER_SHUTDOWN = "REMOTECONTROL_SERVER_SHUTDOWN";
    public static final String SERVICE_SHUTDOWN = "REMOTECONTROL_SERVICE_SHUTDOWN";
    public static final String OK = "REMOTECONTROL_ACKNOWLEDGEMENT";
    public static final String FAILURE = "REMOTECONTROL_FAILURE";



    public String getResponse() {
        return response;
    }

    private String response;

    public ResponseEvent(String response){
        this.response=response;
    }

    @Override
    public String toString() {
        return "ResponseEvent{" +
                "response='" + response + '}';
    }
}
