package controller;

import controller.communication.events.EventWrapper;
import controller.communication.events.RemoteEvent;
import controller.communication.events.ResponseEvent;
import controller.communication.wifi.TCPService;

/**
 * Created by whiteshad on 25/11/15.
 */
public class Controller {
    public static boolean isServiceStarted = false;

    public static TCPService getTcpService() {
        return tcpService;
    }

    public static void setTcpService(TCPService tcpService) {
        Controller.tcpService = tcpService;
    }

    private static TCPService tcpService = null;

    public static EventWrapper execute(EventWrapper recv) {
        EventWrapper wrapper = recv;
        RemoteEvent event = wrapper.getTypeOfEvent().cast(wrapper.getRemoteEvent());

        if (event.getClass().equals(ResponseEvent.class)) {
            ResponseEvent responseEvent = (ResponseEvent) event;
            if (responseEvent.getResponse().equals(ResponseEvent.Response.Ok))
                return null;
            if (responseEvent.getResponse().equals(ResponseEvent.Response.ServerShutdown)) {
                tcpService.stop();
                return null;
            }
            if (responseEvent.getResponse().equals(ResponseEvent.Response.Failure)) {
                return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Ok));
            }
        }
        return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Failure));
    }


}
