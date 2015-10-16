package controller;


import model.message.MessageInterface;

/**
 * Created by cyprien on 08/10/15.
 */
public class Controller {
    private static Controller ourInstance = new Controller();


    public static Controller getInstance() {
        return ourInstance;
    }

    private Controller() {}

    public void route(MessageInterface message){
        switch (message.getAction()){
            case NONE:
                break;
        }
    }
}
