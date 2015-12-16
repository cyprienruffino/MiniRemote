package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class ActionException extends Exception {
    private String message;

    public ActionException(String message) {
        this.message = message;
    }

    public ActionException() {
        this.message = "Action Exception";
    }
}
