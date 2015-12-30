package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class KeyboardEvent extends RemoteEvent{

    //On envoie le code Unicode du charactère
    private int keycode;
    private String action;

    public KeyboardEvent(int keycode, KeyAction action) {
        this.keycode = keycode;
        this.action = action.getCode();
    }

    public int getKeycode() {
        return keycode;
    }

    public String getAction() {
        return action;
    }

    public enum KeyAction{
        KEY_PRESS("REMOTE_EVENT_KEY_PRESS"),
        KEY_RELEASE("REMOTE_EVENT_KEY_RELEASE"),
        KEY_HIT("REMOTE_EVENT_KEY_HIT");

        private String code;

        KeyAction(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
