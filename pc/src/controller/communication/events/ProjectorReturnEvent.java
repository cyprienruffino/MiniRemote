package controller.communication.events;

/**
 * Created by whiteshad on 27/01/16.
 */
public class ProjectorReturnEvent extends RemoteEvent {
    public final static String ERROR_TIMEOUT="NO_PROJECTOR";
    public final static String ERROR_HELLO="DISCOVERY_ERROR";
    public final static String PROJECTOR_SOURCE="PROJECTOR_SOURCE";
    public final static String PROJECTOR_NAME="PROJECTOR_NAME";
    private String action;
    private String value;

    public String getAction() {
        return action;
    }


    public String getValue() {
        return value;
    }

    public ProjectorReturnEvent(String event, String value) {

        this.action = event;
        this.value = value;
    }

    @Override
    public String toString() {
        return "event='" + action + ", value='" + value ;
    }
}
