package controller.communication.events;

/**
 * Created by Valentin on 21/01/2016.
 */
public class MediaEvent extends RemoteEvent {
    private MediaEventType type;

    public MediaEvent(MediaEventType type) {
        this.type = type;
    }

    public MediaEventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MediaEvent{" +
                "type=" + type +
                '}';
    }

    public enum MediaEventType {
        Prec, Start, Next, Origin, Pause, Stop, Volup, Voldown, Mute, Play, Fullscreen;
    }
}