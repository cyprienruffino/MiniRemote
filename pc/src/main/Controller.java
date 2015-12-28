package main;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;

import controller.communication.LanceurThread;
import controller.communication.events.ActionException;
import controller.communication.events.CommandEvent;
import controller.communication.events.EventWrapper;
import controller.communication.events.KeyboardEvent;
import controller.communication.events.MouseClickEvent;
import controller.communication.events.MoveMouseEvent;
import controller.communication.events.RemoteEvent;
import controller.communication.events.ResolutionEvent;
import controller.communication.events.ResponseEvent;
import controller.communication.events.ScrollMouseEvent;
import controller.communication.wifi.TCPServer;
import controller.communication.wifi.UDPServer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import model.CursorModule;
import model.KeyboardModule;
import model.ShellModule;
import view.MainView;

import static controller.communication.events.KeyboardEvent.KEY_HIT;
import static controller.communication.events.KeyboardEvent.KEY_PRESS;
import static controller.communication.events.KeyboardEvent.KEY_RELEASE;

/**
 * Created by cyprien on 05/11/15.
 */
public class Controller {

    private static Controller controller;

    public static EventWrapper handleControl(Object recv) throws AWTException, IOException, ActionException {

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
                Controller.controller.clientShutdown();
                return null;
            }
            if (responseEvent.getResponse().equals(ResponseEvent.FAILURE)) {
                return new EventWrapper(new ResponseEvent(ResponseEvent.OK));
            }
        }

        if (event.getClass().equals(ResolutionEvent.class)){
            ResolutionEvent resolutionEvent = (ResolutionEvent)event;
            CursorModule.getInstance().setDeviceWidth(resolutionEvent.getHeight());
            CursorModule.getInstance().setDeviceWidth(resolutionEvent.getWidth());
        }

        return new EventWrapper(new ResponseEvent(ResponseEvent.FAILURE));
        //throw new ActionException("Incorrect object received");
    }

    private void clientShutdown() {
        t.interrupt();
        enAttenteHandler.handle(null);
        t.start();
    }

    private TCPServer tcpServer;
    private UDPServer udpServer;
    private MainView mainView;
    private Thread t;
    private EventHandler enAttenteHandler;
    private EventHandler connecteHandler;

    public Controller() throws IOException {
        Controller.controller = this;
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                lancerServers();
            }
        }, "LanceurThread"){
            @Override
            public void interrupt() {
                disconnect();
                super.interrupt();
            }
        };
        t.start();
        mainView = new MainView(event -> {t.interrupt(); Platform.exit();});
        enAttenteHandler = mainView.getEnAttenteEvent();
        connecteHandler = mainView.getConnecteEvent();
    }

    public void disconnect() {
        System.out.println("TEST1");
        try {
            if (tcpServer != null)
                tcpServer.stop();
            if(udpServer!=null)
                udpServer.close();
            System.out.println("Serveur déconnecté");
        } catch (IOException e) {
            System.err.println("Impossible de déconnecter le serveur");
            //e.printStackTrace();
        }
    }

    public void lancerServers() {
        try {
            udpServer = new UDPServer();
            udpServer.attendreRequete();
            tcpServer = new TCPServer();
            tcpServer.startServer();
            connecteHandler.handle(null);
        } catch (IOException e) {
            System.err.println("Impossible de connecter le serveur");
            //e.printStackTrace();
        }
    }
}
