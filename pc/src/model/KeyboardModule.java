package model;

import controller.communication.events.KeyboardEvent;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by cyprien on 24/10/15.
 */
public class KeyboardModule {
    //Singleton
    private static KeyboardModule instance;
    private Robot robot;

    private KeyboardModule() throws AWTException {
        robot = new Robot();
    }

    /**
     * Singleton getter
     *
     * @return Instance of the module
     * @throws AWTException
     */
    public static KeyboardModule getInstance() throws AWTException {
        if (instance == null) instance = new KeyboardModule();
        return instance;
    }

    /**
     * Press one time a keyboard key
     *
     * @param charCode A standard Java keycode
     */
    public void hitKey(int charCode) {
        try {
            int keycode = KeyEvent.getExtendedKeyCodeForChar((char) charCode);
            robot.keyPress(keycode);
            robot.keyRelease(keycode);
        } catch (IllegalArgumentException e) {
            System.out.println(charCode + " non supporté par " + robot.getClass().toString());
        }
    }

    public void hitKey(KeyboardEvent.SpecialKey key) {
        if (key != null) {
            robot.keyPress(getIntToSend(key));
            robot.keyRelease(getIntToSend(key));
        }
    }

    /**
     * Hold a keyboard key
     *
     * @param charCode A standard Java keycode
     */
    public void keyPress(int charCode) {
        try {
            int keycode = KeyEvent.getExtendedKeyCodeForChar((char) charCode);
            robot.keyPress(keycode);
        } catch (IllegalArgumentException e) {
            System.out.println(charCode + " non supporté par " + robot.getClass().toString());
        }
    }

    public void keyPress(KeyboardEvent.SpecialKey key) {
        if (key != null)
            robot.keyPress(getIntToSend(key));
    }

    /**
     * Release a holded keyboard key
     *
     * @param charCode A standard java keycode
     */
    public void keyRelease(int charCode) {
        try {
            int keycode = KeyEvent.getExtendedKeyCodeForChar((char) charCode);
            robot.keyRelease(keycode);
        } catch (IllegalArgumentException e) {
            System.out.println(charCode + " non supporté par " + robot.getClass().toString());
        }
    }

    public void keyRelease(KeyboardEvent.SpecialKey key) {
        if (key != null)
            robot.keyRelease(getIntToSend(key));
    }

    private int getIntToSend(KeyboardEvent.SpecialKey key) {
        switch (key) {
            case Alt:
                return KeyEvent.VK_ALT;
            case Alt_Gr:
                return KeyEvent.VK_ALT_GRAPH;
            case Caps_Lock:
                return KeyEvent.VK_CAPS_LOCK;
            case Context:
                return KeyEvent.VK_CONTEXT_MENU;
            case Ctrl:
                return KeyEvent.VK_CONTROL;
            case Del:
                return KeyEvent.VK_BACK_SPACE;
            case Enter:
                return KeyEvent.VK_ENTER;
            case Forward_Del:
                return KeyEvent.VK_DELETE;
            case Shift:
                return KeyEvent.VK_SHIFT;
            case Tab:
                return KeyEvent.VK_TAB;
            default:
                return 0;
        }
    }
}