package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class MouseClickEvent extends RemoteEvent {
    public static final String MOUSE_PRESS = "REMOTE_EVENT_MOUSE_PRESS";
    public static final String MOUSE_RELEASE = "REMOTE_EVENT_MOUSE_RELEASE";
    public static final String MOUSE_HIT = "REMOTE_EVENT_MOUSE_HIT";
    //Clic gauche
    public static final String MOUSE_CONTEXT = "REMOTE_EVENT_MOUSE_CONTEXT";

    private String action;
    private int click;

    public MouseClickEvent(int click, String action) {
        this.action = action;
        this.click = click;
    }

    public String getAction() {
        return action;
    }

    public int getClick() {
        return click;
    }
}
