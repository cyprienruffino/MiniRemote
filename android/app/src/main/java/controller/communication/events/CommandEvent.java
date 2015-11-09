package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class CommandEvent {
    private String command;

    public CommandEvent(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
