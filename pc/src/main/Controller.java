package main;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;

import controller.communication.events.ActionException;
import controller.communication.events.CommandEvent;
import controller.communication.events.EventWrapper;
import controller.communication.events.KeyboardEvent;
import controller.communication.events.MouseClickEvent;
import controller.communication.events.MoveMouseEvent;
import controller.communication.events.RemoteEvent;
import controller.communication.events.ResponseEvent;
import controller.communication.events.ScrollMouseEvent;
import model.CursorModule;
import model.KeyboardModule;
import model.ShellModule;

import static controller.communication.events.KeyboardEvent.KEY_HIT;
import static controller.communication.events.KeyboardEvent.KEY_PRESS;
import static controller.communication.events.KeyboardEvent.KEY_RELEASE;

/**
 * Created by cyprien on 05/11/15.
 */
public class Controller {

    public static EventWrapper handleControl(Object recv) throws AWTException, IOException, ActionException {

        EventWrapper wrapper = ((EventWrapper) recv);
        RemoteEvent event = wrapper.getTypeOfEvent().cast(wrapper.getRemoteEvent());

        if (event instanceof CommandEvent) {
            CommandEvent commandEvent = (CommandEvent) event;
            ShellModule.getInstance().execute(commandEvent.getCommand());
            return new EventWrapper(new ResponseEvent());
        }

        if (event instanceof KeyboardEvent) {
            KeyboardEvent keyboardEvent = (KeyboardEvent) event;
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
            return new EventWrapper(new ResponseEvent());
        }

        if (event instanceof MouseClickEvent) {
            MouseClickEvent mouseClickEvent = (MouseClickEvent) event;
            if (mouseClickEvent.getClick() == 1) CursorModule.getInstance().mouseRightClick();
            else CursorModule.getInstance().mouseLeftClick();
        }

        if (event instanceof MoveMouseEvent) {
            MoveMouseEvent moveMouseEvent = (MoveMouseEvent) event;
            CursorModule.getInstance().moveCursor(moveMouseEvent.getXmove(), moveMouseEvent.getYmove());
        }

        if (event instanceof ScrollMouseEvent) {
            ScrollMouseEvent scrollMouseEvent = (ScrollMouseEvent) event;
            CursorModule.getInstance().mouseScroll(scrollMouseEvent.getScroll());
        }

        throw new ActionException("Incorrect object received");
    }
}
