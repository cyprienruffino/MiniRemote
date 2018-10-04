package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class ActionException extends Exception {

    public ActionException(String message) {
        super(message);
    }

    public ActionException() {
        super("Action Exception");
    }
}
