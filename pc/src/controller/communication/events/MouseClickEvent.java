package controller.communication.events;

import java.io.Serializable;

/**
 * Created by cyprien on 09/11/15.
 */
public class MouseClickEvent extends RemoteEvent implements Serializable{

    public static final String MOUSE_PRESS = "REMOTE_EVENT_MOUSE_PRESS";
    public static final String MOUSE_RELEASE = "REMOTE_EVENT_MOUSE_RELEASE";
    public static final String MOUSE_HIT = "REMOTE_EVENT_MOUSE_HIT";
    //Clic gauche
    public static final String MOUSE_CONTEXT = "REMOTE_EVENT_MOUSE_CONTEXT";

    public static final int MOUSE_LEFT=0;
    public static final int MOUSE_RIGHT=1;
    public static final int MOUSE_MIDDLE=2;

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

    @Override
    public String toString() {
        return "MouseClickEvent{" +
                "action='" + action + '\'' +
                ", click=" + click +
                '}';
    }
}
