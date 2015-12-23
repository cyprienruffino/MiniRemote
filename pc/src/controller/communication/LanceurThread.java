package controller.communication;

import java.io.IOException;

import main.Controller;

/**
 * Created by Valentin on 23/12/2015.
 */
public class LanceurThread implements Runnable {
    private Controller controller;

    public LanceurThread(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.lancerServers();
    }
}
