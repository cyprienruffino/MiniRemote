package main;

import controller.communication.callbackInterface.SendFinished;
import controller.communication.events.*;
import controller.communication.wifi.TCPServer;
import controller.communication.wifi.UDPServer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import model.CursorModule;
import model.KeyboardModule;
import model.ProjectorModule;
import model.ShellModule;
import view.MainView;

import java.awt.*;
import java.io.IOException;

import static controller.communication.events.KeyboardEvent.*;

/**
 * Created by cyprien on 05/11/15.
 */
public class Controller {

    private static Controller controller;

    public static EventWrapper handleControl(Object recv) throws AWTException, IOException, ActionException, InterruptedException {

        EventWrapper wrapper = ((EventWrapper) recv);
        RemoteEvent event = wrapper.getTypeOfEvent().cast(wrapper.getRemoteEvent());

        System.out.println(event.getClass());

        if (event.getClass().equals(CommandEvent.class)) {
            CommandEvent commandEvent = (CommandEvent) event;
            ShellModule.getInstance().execute(commandEvent.getCommand());
            return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
        }

        if (event.getClass().equals(KeyboardEvent.class)) {
            KeyboardEvent keyboardEvent = (KeyboardEvent) event;
            System.out.println(keyboardEvent.getKeycode());
            switch (keyboardEvent.getAction()) {
                case KEY_HIT:
                    KeyboardModule.getInstance().hitKey(keyboardEvent.getKeycode());
                    break;
                case KEY_PRESS:
                    KeyboardModule.getInstance().keyPress(keyboardEvent.getKeycode());
                    break;
                case KEY_RELEASE:
                    KeyboardModule.getInstance().keyRelease(keyboardEvent.getKeycode());
            }
            return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
        }

        if (event.getClass().equals(MouseClickEvent.class)) {
            MouseClickEvent mouseClickEvent = (MouseClickEvent) event;
            if (mouseClickEvent.getClick() == 1) CursorModule.getInstance().mouseRightClick();
            else CursorModule.getInstance().mouseLeftClick();
            return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
        }

        if (event.getClass().equals(MoveMouseEvent.class)) {
            MoveMouseEvent moveMouseEvent = (MoveMouseEvent) event;
            CursorModule.getInstance().moveCursor(moveMouseEvent.getXmove(), moveMouseEvent.getYmove());
            return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
        }

        if (event.getClass().equals(ScrollMouseEvent.class)) {
            ScrollMouseEvent scrollMouseEvent = (ScrollMouseEvent) event;
            CursorModule.getInstance().mouseScroll(scrollMouseEvent.getScroll());
            return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
        }

        if (event.getClass().equals(ResponseEvent.class)) {
            ResponseEvent responseEvent = (ResponseEvent) event;
            if (responseEvent.getResponse().equals(ResponseEvent.OK))
                return null;
            if (responseEvent.getResponse().equals(ResponseEvent.SERVICE_SHUTDOWN)) {
                Controller.controller.restartServer();
                return null;
            }
            if (responseEvent.getResponse().equals(ResponseEvent.FAILURE)) {
                return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
            }
        }

        if (event.getClass().equals(ResolutionEvent.class)) {
            ResolutionEvent resolutionEvent = (ResolutionEvent) event;
            CursorModule.getInstance().setDeviceHeight(resolutionEvent.getHeight());
            CursorModule.getInstance().setDeviceWidth(resolutionEvent.getWidth());
            System.out.println("Device on event reception : Height:" + resolutionEvent.getHeight() + " Width:" + resolutionEvent.getWidth());
        }

        if (event.getClass().equals(ProjectorEvent.class)) {
            ProjectorEvent projectorEvent = (ProjectorEvent) event;
            if (projectorEvent.getAction() == ProjectorEvent.POWER_ON)
                ProjectorModule.getInstance().sendPowerOn();
            if (projectorEvent.getAction() == ProjectorEvent.POWER_OFF)
                ProjectorModule.getInstance().sendPowerOff();

        }

        return new EventWrapper(new ResponseEvent(ResponseEvent.FAILURE));
        //throw new ActionException("Incorrect object received");
    }

    private TCPServer tcpServer;
    private UDPServer udpServer;
    private MainView mainView;
    private Thread t;
    private EventHandler enAttenteHandler;
    private EventHandler connecteHandler;

    class LanceurRunnable implements Runnable {

        @Override
        public void run() {
            lancerServers();
        }
    }

    public Controller() throws IOException {
        Controller.controller = this;
        t = new Thread(new LanceurRunnable(), "LanceurThread") {
            @Override
            public void interrupt() {
                super.interrupt();
                disconnect();
            }
        };
        t.start();
        mainView = new MainView(event -> {
            t.interrupt();
            Platform.exit();
        });
        enAttenteHandler = mainView.getEnAttenteEvent();
        connecteHandler = mainView.getConnecteEvent();
    }

    public void restartServer() {
        disconnect();
        t = new Thread(new LanceurRunnable(), "LanceurThread") {
            @Override
            public void interrupt() {
                super.interrupt();
                disconnect();
            }
        };
        t.start();
    }

    public void disconnect() {
        if (udpServer != null)
            udpServer.close();
        if (tcpServer != null) {
            tcpServer.setOnSendFinished(new SendFinished() {
                @Override
                public void onSendFinished() {
                    try {
                        tcpServer.stop();
                    } catch (IOException e) {
                        System.err.println("Impossible de déconnecter le serveur");
                    }
                }
            });
            tcpServer.send(new EventWrapper(new ResponseEvent(ResponseEvent.SERVER_SHUTDOWN)));
        }
        System.out.println("Serveur déconnecté");
        enAttenteHandler.handle(null);
    }

    public void lancerServers() {
        try {
            if (!t.isInterrupted()) {
                udpServer = new UDPServer();
                udpServer.attendreRequete();
            }
            if (!t.isInterrupted()) {
                tcpServer = new TCPServer();
                tcpServer.startServer();
                connecteHandler.handle(null);
            }
        } catch (IOException e) {
            System.err.println("Impossible de connecter le serveur");
            //e.printStackTrace();
        }
    }
}
