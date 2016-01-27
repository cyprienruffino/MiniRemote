package controller;

import controller.communication.callbackInterface.ClientDisconnected;
import controller.communication.events.EventWrapper;
import controller.communication.events.RemoteEvent;
import controller.communication.events.ResponseEvent;
import controller.communication.events.RuntimeOutputEvent;
import controller.communication.wifi.TCPService;
import view.ShellActivity;

/**
 * Created by whiteshad on 25/11/15.
 */
public class Controller {
    public static boolean isServiceStarted = false;
    public static ClientDisconnected callback;
    private static TCPService tcpService = null;
    private static int port = 1337;

    public static TCPService getTcpService() {
        return tcpService;
    }

    public static void setTcpService(TCPService tcpService) {
        Controller.tcpService = tcpService;
    }

    public static void setClientDisconnected(ClientDisconnected clientDisconnected) {
        callback = clientDisconnected;
    }

    public static void onClientDisconnection() {
        if (callback != null)
            callback.onDisconnection();
    }

    public static void execute(EventWrapper recv) {
        EventWrapper wrapper = recv;
        RemoteEvent event = wrapper.getTypeOfEvent().cast(wrapper.getRemoteEvent());
        System.out.println(event);
        if (event.getClass().equals(RuntimeOutputEvent.class)){
            RuntimeOutputEvent runtimeOutputEvent=(RuntimeOutputEvent)event;
            if(ShellActivity.isRunning){
                ShellActivity.instance.writeToTerminal(runtimeOutputEvent.getOutput());
            }
        }
        if (event.getClass().equals(ResponseEvent.class)) {
            ResponseEvent responseEvent = (ResponseEvent) event;
            if (responseEvent.getResponse().equals(ResponseEvent.Response.Ok)) {

            }
            if (responseEvent.getResponse().equals(ResponseEvent.Response.ServerShutdown)) {
                if (callback != null) {
                    callback.onDisconnection();
                }
            }
            if (responseEvent.getResponse().equals(ResponseEvent.Response.Failure)) {
            }
        }
    }


    public static int getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        Controller.port = port;
    }
}
