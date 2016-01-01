package controller.communication.events;

/**
 * Created by cyprien on 01/01/16.
 */
public class ProjectorEvent extends RemoteEvent{
    public static final String POWER_ON="PROJECTOR_POWER_ON";
    public static final String POWER_OFF="PROJECTOR_POWER_OFF";

    public String getAction() {
        return action;
    }

    private String action;

    public ProjectorEvent(String action){
        this.action=action;
    }
}
