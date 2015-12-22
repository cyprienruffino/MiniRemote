package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class KeyboardEvent extends RemoteEvent{
    public static final String KEY_PRESS = "REMOTE_EVENT_KEY_PRESS";
    public static final String KEY_RELEASE = "REMOTE_EVENT_KEY_RELEASE";
    public static final String KEY_HIT = "REMOTE_EVENT_KEY_HIT";

    private char keycode;
    private String action;

    public KeyboardEvent(char keycode, String action) throws ActionException {
        this.keycode = keycode;
        if (action.equals(KEY_HIT) && action.equals(KEY_PRESS) && action.equals(KEY_RELEASE))
            throw new ActionException();
        this.action = action;
    }

    public char getKeycode() {
        return keycode;
    }

    public String getAction() {
        return action;
    }
}
