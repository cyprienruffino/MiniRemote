package main;

import controller.communication.callbackInterface.SendFinished;
import controller.communication.events.*;
import controller.communication.wifi.TCPServer;
import controller.communication.wifi.UDPServer;
import controller.communication.wifi.exception.NoTcpServerException;
import javafx.application.Platform;
import model.*;
import view.MainView;

import java.awt.*;
import java.io.IOException;

/**
 * Created by cyprien on 05/11/15.
 */
public class Controller {
    private static Controller controller;
    private int port = 1337;
    private MainView mainView;
    private TCPServer tcpServer;
    private UDPServer udpServer;

    public Controller() throws IOException {
        mainView = new MainView(event -> {
            try {
                send(new ResponseEvent(ResponseEvent.Response.ServerShutdown), () -> {
                    disconnect();
                    Platform.exit();
                });
            } catch (NoTcpServerException e) {
                disconnect();
                Platform.exit();
            }
        });
        mainView.setEnAttente();
        Controller.controller = this;
        new Thread(new LanceurRunnable(), "LanceurThread").start();
    }

    public static void handleControl(Object recv) throws AWTException, IOException, ActionException, InterruptedException {

        EventWrapper wrapper = ((EventWrapper) recv);
        RemoteEvent event = wrapper.getTypeOfEvent().cast(wrapper.getRemoteEvent());

        System.out.println(event);

        if (event.getClass().equals(CommandEvent.class)) {
            CommandEvent commandEvent = (CommandEvent) event;
            ShellModule.getInstance().execute(commandEvent.getCommand());
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
                        module.keyRelease(keyboardEvent.getSpecialKey());
                    else
                        module.keyRelease(keyboardEvent.getKeycode());
            }
        }

        if (event.getClass().equals(MouseClickEvent.class)) {
            MouseClickEvent mouseClickEvent = (MouseClickEvent) event;
            CursorModule cursorModule = CursorModule.getInstance();
            switch (mouseClickEvent.getAction()) {
                case Press:
                    switch (mouseClickEvent.getButton()) {
                        case Left:
                            cursorModule.mouseLeftPress();
                            break;
                        case Right:
                            cursorModule.mouseRightPress();
                            break;
                        case Center:
                            //TODO a voir à l'intégration du bouton middle
                            break;
                    }
                    break;
                case Release:
                    switch (mouseClickEvent.getButton()) {
                        case Left:
                            cursorModule.mouseLeftRelease();
                            break;
                        case Right:
                            cursorModule.mouseRightRelease();
                            break;
                        case Center:
                            //TODO a voir à l'intégration du bouton middle
                            break;
                    }
                    break;
                case Hit:
                    switch (mouseClickEvent.getButton()) {
                        case Left:
                            cursorModule.mouseLeftClick();
                            break;
                        case Right:
                            cursorModule.mouseRightClick();
                            break;
                        case Center:
                            //TODO a voir à l'intégration du bouton middle
                            break;
                    }
                    break;
            }
        }

        if (event.getClass().equals(MoveMouseEvent.class)) {
            MoveMouseEvent moveMouseEvent = (MoveMouseEvent) event;
            CursorModule.getInstance().moveCursor(moveMouseEvent.getXmove(), moveMouseEvent.getYmove());
        }

        if (event.getClass().equals(ScrollMouseEvent.class)) {
            ScrollMouseEvent scrollMouseEvent = (ScrollMouseEvent) event;
            CursorModule.getInstance().mouseScroll(scrollMouseEvent.getScroll());
        }

        if (event.getClass().equals(ResponseEvent.class)) {
            ResponseEvent responseEvent = (ResponseEvent) event;
            if (responseEvent.getResponse().equals(ResponseEvent.Response.Ok))
                if (responseEvent.getResponse().equals(ResponseEvent.Response.ServiceShutdown)) {
                    Controller.controller.restartServer();
                }
            if (responseEvent.getResponse().equals(ResponseEvent.Response.Failure)) {
            }
        }

        if (event.getClass().equals(ResolutionEvent.class)) {
            ResolutionEvent resolutionEvent = (ResolutionEvent) event;
            CursorModule.getInstance().setDeviceSize(resolutionEvent.getHeight(), resolutionEvent.getWidth());
        }
        if (event.getClass().equals(DiapoEvent.class)) {
            DiapoEvent diapoEvent = (DiapoEvent) event;
            DiapoModule module = DiapoModule.getInstance();
            switch (diapoEvent.getType()) {
                case Goto:
                    try {
                        module.go_to(diapoEvent.getSoft(), diapoEvent.getNumPage());
                    } catch (NotFunctionnalException e) {
                        Controller.getInstance().send(new ResponseEvent(ResponseEvent.Response.Failure));
                    }
                    break;
                case Last:
                    module.last(diapoEvent.getSoft());
                    break;
                case Next:
                    module.next(diapoEvent.getSoft());
                    break;
                case Origin:
                    module.origin(diapoEvent.getSoft());
                    break;
                case Prec:
                    module.prec(diapoEvent.getSoft());
                    break;
                case Start:
                    module.start(diapoEvent.getSoft());
                    break;
                case StartHere:
                    module.startHere(diapoEvent.getSoft());
                    break;
            }
        }
        if (event.getClass().equals(MediaEvent.class)) {
            MediaEvent mediaEvent = (MediaEvent) event;
            MediaModule module = MediaModule.getInstance();
            switch (mediaEvent.getType()) {
                case Prec:
                    module.prec();
                    break;
                case Start:
                    module.start();
                    break;
                case Next:
                    module.next();
                    break;
                case Origin:
                    module.origin();
                    break;
                case Stop:
                    module.last();
                    break;
                case Volup:
                    module.volup();
                    break;
                case Voldown:
                    module.voldown();
                    break;
                case Mute:
                    module.mute();
                    break;
                case Play:
                    module.play();
                    break;
                case Fullscreen:
                    module.fullscreen();
            }
        }
    }

    public static Controller getInstance() {
        return controller;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void send(RemoteEvent event) {
        if (tcpServer != null)
            tcpServer.send(new EventWrapper(event));
    }

    public void send(RemoteEvent event, SendFinished callback) throws NoTcpServerException {
        if (tcpServer != null)
            tcpServer.send(new EventWrapper(event), callback);
        else
            throw new NoTcpServerException();
    }

    public void restartServer() {
        disconnect();
        new Thread(new LanceurRunnable(), "LanceurThread").start();
    }

    public void restartAfterSend() {
        try {
            send(new ResponseEvent(ResponseEvent.Response.ServerShutdown), () -> {
                restartServer();
                mainView.setEnAttente();
            });
        } catch (NoTcpServerException e) {
            restartServer();
            mainView.setEnAttente();
        }
    }

    public void disconnect() {
        if (udpServer != null)
            udpServer.close();
        if (tcpServer != null)
            tcpServer.close();
        try {
            ShellModule module = ShellModule.getInstance();
            if (module != null)
                module.killCurrentProcess();
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public void lancerServers() {
        mainView.setEnAttente();
        udpServer = new UDPServer(port, (port, address) -> {
            tcpServer = new TCPServer(port, x -> mainView.setConnecte(address.getHostName()));
            udpServer.close();
        });
    }

    class LanceurRunnable implements Runnable {

        @Override
        public void run() {
            lancerServers();
        }
    }
}
