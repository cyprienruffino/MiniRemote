package controller.communication.events;

/**
 * Created by cyprien on 01/01/16.
 */
public class ProjectorEvent extends RemoteEvent{
    public static final String POWER_ON="PROJECTOR_POWER_ON";
    public static final String POWER_OFF="PROJECTOR_POWER_OFF";
    public static final String GET_SOURCE="PROJECTOR_GET_SOURCE";
    public static final String SET_SOURCE_PC="PROJECTOR_SET_SOURCE_PC";
    public static final String SET_SOURCE_HDMI="PROJECTOR_SET_SOURCE_HDMI";
    public static final String SET_SOURCE_VIDEO="PROJECTOR_SET_SOURCE_VIDEO";

    public String getAction() {
        return action;
    }

    private String action;

    public ProjectorEvent(String action){
        this.action=action;
    }

    @Override
    public String toString() {
        return "ProjectorEvent{" +
                "action='" + action + '\'' +
                '}';
    }
}
