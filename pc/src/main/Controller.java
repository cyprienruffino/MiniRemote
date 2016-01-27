package main;

import java.awt.AWTException;
import java.io.IOException;

import controller.communication.callbackInterface.SendFinished;
import controller.communication.events.ActionException;
import controller.communication.events.CommandEvent;
import controller.communication.events.CommandKillEvent;
import controller.communication.events.DiapoEvent;
import controller.communication.events.EventWrapper;
import controller.communication.events.KeyboardEvent;
import controller.communication.events.MediaEvent;
import controller.communication.events.MouseClickEvent;
import controller.communication.events.MoveMouseEvent;
import controller.communication.events.ProjectorEvent;
import controller.communication.events.RemoteEvent;
import controller.communication.events.ResolutionEvent;
import controller.communication.events.ResponseEvent;
import controller.communication.events.ScrollMouseEvent;
import controller.communication.wifi.TCPServer;
import controller.communication.wifi.UDPServer;
import controller.communication.wifi.exception.NoTcpServerException;
import javafx.application.Platform;
import model.CursorModule;
import model.DiapoModule;
import model.KeyboardModule;
import model.MediaModule;
import model.ProjectorModule;
import model.ShellModule;
import view.MainView;

/**
 * Created by cyprien on 05/11/15.
 */
public class Controller {

    private static Controller controller;
    private MainView mainView;
    private TCPServer tcpServer;
    private UDPServer udpServer;

    public Controller() throws IOException {
        mainView = new MainView(event -> {
            try {
                send(new EventWrapper(new ResponseEvent(ResponseEvent.Response.ServerShutdown)), () -> {
                    disconnect();
                    Platform.exit();
                });
            } catch (NoTcpServerException e) {
                disconnect();
                Platform.exit();
            }
        });
        Controller.controller = this;
        new Thread(new LanceurRunnable(), "LanceurThread").start();
    }

    public static EventWrapper handleControl(Object recv) throws AWTException, IOException, ActionException, InterruptedException {

        EventWrapper wrapper = ((EventWrapper) recv);
        RemoteEvent event = wrapper.getTypeOfEvent().cast(wrapper.getRemoteEvent());

        System.out.println(event);

        if (event.getClass().equals(CommandEvent.class)) {
            CommandEvent commandEvent = (CommandEvent) event;
            ShellModule.getInstance().execute(commandEvent.getCommand());
            return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Ok));
        }


        if (event.getClass().equals(CommandKillEvent.class)) {
            ShellModule.getInstance().killCurrentProcess();
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
            return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Ok));
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
            return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Ok));
        }

        if (event.getClass().equals(MoveMouseEvent.class)) {
            MoveMouseEvent moveMouseEvent = (MoveMouseEvent) event;
            CursorModule.getInstance().moveCursor(moveMouseEvent.getXmove(), moveMouseEvent.getYmove());
            return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Ok));
        }

        if (event.getClass().equals(ScrollMouseEvent.class)) {
            ScrollMouseEvent scrollMouseEvent = (ScrollMouseEvent) event;
            CursorModule.getInstance().mouseScroll(scrollMouseEvent.getScroll());
            return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Ok));
        }

        if (event.getClass().equals(ResponseEvent.class)) {
            ResponseEvent responseEvent = (ResponseEvent) event;
            if (responseEvent.getResponse().equals(ResponseEvent.Response.Ok))
                return null;
            if (responseEvent.getResponse().equals(ResponseEvent.Response.ServiceShutdown)) {
                Controller.controller.restartServer();
                return null;
            }
            if (responseEvent.getResponse().equals(ResponseEvent.Response.Failure)) {
                return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Ok));
            }
        }

        if (event.getClass().equals(ResolutionEvent.class)) {
            ResolutionEvent resolutionEvent = (ResolutionEvent) event;
            CursorModule.getInstance().setDeviceSize(resolutionEvent.getHeight(), resolutionEvent.getWidth());
        }

        if (event.getClass().equals(ProjectorEvent.class)) {
            ProjectorEvent projectorEvent = (ProjectorEvent) event;
            switch (projectorEvent.getAction()) {
                case ProjectorEvent.POWER_ON:
                    ProjectorModule.getInstance().sendPowerOn();
                    break;
                case ProjectorEvent.POWER_OFF:
                    ProjectorModule.getInstance().sendPowerOff();
                    break;
                case ProjectorEvent.SET_SOURCE_PC:
                    ProjectorModule.getInstance().sendSetSource(ProjectorModule.SET_SOURCE_PC);
                    break;
                case ProjectorModule.SET_SOURCE_HDMI:
                    ProjectorModule.getInstance().sendSetSource(ProjectorModule.SET_SOURCE_HDMI);
                    break;
                case ProjectorModule.SET_SOURCE_VIDEO:
                    ProjectorModule.getInstance().sendSetSource(ProjectorModule.SET_SOURCE_VIDEO);
                    break;
            }
        }
        if (event.getClass().equals(DiapoEvent.class)) {
            DiapoEvent diapoEvent = (DiapoEvent) event;
            DiapoModule module = DiapoModule.getInstance();
            switch (diapoEvent.getType()) {
                case Goto:
                    module.go_to(diapoEvent.getNumPage());
                    break;
                case Last:
                    module.last();
                    break;
                case Next:
                    module.next();
                    break;
                case Origin:
                    module.origin();
                    break;
                case Prec:
                    module.prec();
                    break;
                case Start:
                    module.start();
                    break;
                case StartHere:
                    module.startHere();
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
        return new EventWrapper(new ResponseEvent(ResponseEvent.Response.Failure));
    }

    public static Controller getInstance() {
        return controller;
    }

    public void send(EventWrapper eventWrapper) {
        if (tcpServer != null)
            tcpServer.send(eventWrapper);
    }

    public void send(EventWrapper eventWrapper, SendFinished callback) throws NoTcpServerException {
        System.out.println(eventWrapper.getTypeOfEvent().cast(eventWrapper.getRemoteEvent()).toString());
        if (tcpServer != null)
            tcpServer.send(eventWrapper, callback);
        else
            throw new NoTcpServerException();
    }

    public void restartServer() {
        disconnect();
        new Thread(new LanceurRunnable(), "LanceurThread").start();
    }

    public void disconnect() {
        if (udpServer != null)
            udpServer.close();
        if (tcpServer != null)
            tcpServer.close();
        try {
            ShellModule.getInstance().closeInputThread();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void lancerServers() {
        mainView.setEnAttente();
        udpServer = new UDPServer((port, address) -> {
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
