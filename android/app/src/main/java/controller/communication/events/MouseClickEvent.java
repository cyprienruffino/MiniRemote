package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class MouseClickEvent {
    public static final String MOUSE_PRESS = "REMOTE_EVENT_MOUSE_PRESS";
    public static final String MOUSE_RELEASE = "REMOTE_EVENT_MOUSE_RELEASE";
    public static final String MOUSE_HIT = "REMOTE_EVENT_MOUSE_HIT";

    private String action;

    public MouseClickEvent(String action) throws ActionException {
        if (action.equals(MOUSE_HIT) && action.equals(MOUSE_PRESS) && action.equals(MOUSE_RELEASE))
            throw new ActionException();
    }
}
