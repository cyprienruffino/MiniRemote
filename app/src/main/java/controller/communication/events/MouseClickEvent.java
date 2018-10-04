package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class MouseClickEvent extends RemoteEvent {
    public enum MouseAction {
        Press, Release, Hit
    }

    public enum MouseButton {
        Left, Center, Right
    }

    private MouseAction action;
    private MouseButton button;

    public MouseClickEvent(MouseAction action, MouseButton button) {
        this.action = action;
        this.button = button;
    }

    public MouseAction getAction() {
        return action;
    }

    public MouseButton getButton() {
        return button;
    }

    @Override
    public String toString() {
        return "MouseClickEvent{" +
                "action=" + action +
                ", button=" + button +
                '}';
    }
}
