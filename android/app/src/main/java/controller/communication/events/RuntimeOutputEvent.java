package controller.communication.events;

/**
 * Created by cyprien on 26/01/16.
 */
public class RuntimeOutputEvent extends RemoteEvent {
    private String output;

    public RuntimeOutputEvent(String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "RuntimeOutputEvent{" +
                "output='" + output + '\'' +
                '}';
    }
}
