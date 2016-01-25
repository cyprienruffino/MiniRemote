package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class KeyboardEvent extends RemoteEvent {
    private int keycode;
    private KeyboardAction action;
    private SpecialKey specialKey;

    public KeyboardEvent(int keycode, KeyboardAction action, SpecialKey sp) {
        this.keycode = keycode;
        this.action = action;
        specialKey = sp;
    }

    public int getKeycode() {
        return keycode;
    }

    public KeyboardAction getAction() {
        return action;
    }

    public SpecialKey getSpecialKey() {
        return specialKey;
    }

    @Override
    public String toString() {
        return "KeyboardEvent{" +
                "keycode=" + keycode +
                ", action=" + action +
                ", specialKey=" + specialKey +
                '}';
    }

    public enum KeyboardAction {
        KeyPress, KeyRelease, KeyHit
    }

    public enum SpecialKey {
        Alt,
        Tab,
        Alt_Gr,
        Ctrl,
        Context,
        Del,
        Forward_Del,
        Shift,
        Caps_Lock,
        Enter
    }
}