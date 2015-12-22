package controller;

import controller.communication.events.EventWrapper;
import controller.communication.wifi.TCPService;

/**
 * Created by whiteshad on 25/11/15.
 */
public class Controller {
    public static boolean isServiceStarted=false;
    public static TCPService getTcpService() {
        return tcpService;
    }

    public static void setTcpService(TCPService tcpService) {
        Controller.tcpService = tcpService;
    }

    private static TCPService tcpService=null;

    public static void execute(EventWrapper event){

    }


}
