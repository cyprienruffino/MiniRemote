package main;

import controller.communication.events.*;
import controller.communication.wifi.TCPServer;
import controller.communication.wifi.UDPServer;
import javafx.application.Platform;
import model.CursorModule;
import model.KeyboardModule;
import model.ProjectorModule;
import model.ShellModule;
import view.MainView;

import java.awt.*;
import java.io.IOException;

/**
 * Created by cyprien on 05/11/15.
 */
public class Controller {

    private static Controller controller;

    public static EventWrapper handleControl(Object recv) throws AWTException, IOException, ActionException, InterruptedException {

        EventWrapper wrapper = ((EventWrapper) recv);
        RemoteEvent event = wrapper.getTypeOfEvent().cast(wrapper.getRemoteEvent());

        System.out.println(event);

        if (event.getClass().equals(CommandEvent.class)) {
            CommandEvent commandEvent = (CommandEvent) event;
            ShellModule.getInstance().execute(commandEvent.getCommand());
            return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
        }

        if (event.getClass().equals(KeyboardEvent.class)) {
            KeyboardEvent keyboardEvent = (KeyboardEvent) event;
            KeyboardModule module = KeyboardModule.getInstance();
            System.out.println(keyboardEvent.getKeycode());
            switch (keyboardEvent.getAction()) {
                case KeyHit:
                    if (keyboardEvent.getKeycode() == -1)
                        module.hitKey(keyboardEvent.getSpecialKey());
                    else
                        module.hitKey(keyboardEvent.getKeycode());
                    break;
                case KeyPress:
                    if (keyboardEvent.getKeycode() == -1)
                        module.keyPress(((KeyboardEvent) event).getSpecialKey());
                    else
                        module.keyPress(((KeyboardEvent) event).getKeycode());
                    break;
                case KeyRelease:
                    if (keyboardEvent.getKeycode() == -1)
                        module.keyPress(keyboardEvent.getSpecialKey());
                    else
                        module.keyPress(keyboardEvent.getKeycode());
            }
            return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
        }

        if (event.getClass().equals(MouseClickEvent.class)) {
            MouseClickEvent mouseClickEvent = (MouseClickEvent) event;
            CursorModule cursorModule = CursorModule.getInstance();
            switch (mouseClickEvent.getAction()) {
                case MouseClickEvent.MOUSE_PRESS:
                    switch (mouseClickEvent.getClick()) {
                        case MouseClickEvent.MOUSE_LEFT:
                            cursorModule.mouseLeftPress();
                            break;
                        case MouseClickEvent.MOUSE_MIDDLE:
                            //TODO a voir à l'intégration du bouton middle
                            break;
                        case MouseClickEvent.MOUSE_RIGHT:
                            cursorModule.mouseRightPress();
                            break;
                    }
                    break;
                case MouseClickEvent.MOUSE_RELEASE:
                    switch (mouseClickEvent.getClick()) {
                        case MouseClickEvent.MOUSE_LEFT:
                            cursorModule.mouseLeftRelease();
                            break;
                        case MouseClickEvent.MOUSE_MIDDLE:
                            //TODO a voir à l'intégration du bouton middle
                            break;
                        case MouseClickEvent.MOUSE_RIGHT:
                            cursorModule.mouseRightRelease();
                            break;
                    }
                    break;
                case MouseClickEvent.MOUSE_HIT:
                    switch (mouseClickEvent.getClick()) {
                        case MouseClickEvent.MOUSE_LEFT:
                            cursorModule.mouseLeftClick();
                            break;
                        case MouseClickEvent.MOUSE_MIDDLE:
                            //TODO a voir à l'intégration du bouton middle
                            break;
                        case MouseClickEvent.MOUSE_RIGHT:
                            cursorModule.mouseRightClick();
                            break;
                    }
                    break;
            }
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
            CursorModule.getInstance().setDeviceSize(resolutionEvent.getHeight(), resolutionEvent.getWidth());
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
    private boolean stop = false;

    class LanceurRunnable implements Runnable {

        @Override
        public void run() {
            lancerServers();
        }
    }

    public Controller() throws IOException {
        mainView = new MainView(event -> {
            disconnect();
            Platform.exit();
        });
        Controller.controller = this;
        t = new Thread(new LanceurRunnable(), "LanceurThread");
        t.start();
    }

    public void restartServer() {
        disconnect();
        t = new Thread(new LanceurRunnable(), "LanceurThread");
        t.start();
    }

    public void disconnect() {
        stop = true;
        if (udpServer != null)
            udpServer.close();
        if (tcpServer != null) {
            try {
                tcpServer.stop();
            } catch (IOException e) {
                System.err.println("Impossible de déconnecter le serveur");
            }
        }
        System.out.println("Serveur déconnecté");
        mainView.setEnAttente();
    }

    public void lancerServers() {
        try {
            stop = false;
            if (!stop) {
                udpServer = new UDPServer();
                udpServer.attendreRequete();
            }
            if (!stop) {
                tcpServer = new TCPServer();
                tcpServer.startServer();
                mainView.setConnecte();
            }
        } catch (IOException e) {
            System.err.println("Impossible de connecter le serveur");
            //e.printStackTrace();
        }
    }

    public void send(EventWrapper event) {
        tcpServer.send(event);
    }

    public static Controller getInstance() {
        return controller;
    }
}
