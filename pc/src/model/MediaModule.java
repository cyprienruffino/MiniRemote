package model;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

/**
 * Created by cyprien on 26/01/2016.
 */
public class MediaModule {
    private static MediaModule instance;

    public static MediaModule getInstance() {
        if (instance == null)
            instance = new MediaModule();
        return instance;
    }

    private Robot robot;

    private MediaModule() {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Impossible de créer DiapoModule");
        }
    }

    public void next() {
        robot.keyPress(KeyEvent.VK_ALT);
        hitKey(KeyEvent.VK_RIGHT);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    public void origin() {
        hitKey(KeyEvent.VK_P);
    }

    public void prec() {
        robot.keyPress(KeyEvent.VK_ALT);
        hitKey(KeyEvent.VK_LEFT);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    public void start() {
        hitKey(KeyEvent.VK_F5);
    }

    public void volup() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        hitKey(KeyEvent.VK_UP);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void voldown() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        hitKey(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void mute() {
        hitKey(KeyEvent.VK_M);
    }

    public void play() {
        hitKey(KeyEvent.VK_SPACE);
    }

    public void fullscreen() {
        hitKey(KeyEvent.VK_F);
    }
    public void last() {
        hitKey(KeyEvent.VK_S);
    }

    private void hitKey(int keycode) {
        try {
            robot.keyPress(keycode);
            robot.keyRelease(keycode);
        } catch (IllegalArgumentException e) {
            System.err.println(keycode + " non supporté par " + robot.getClass());
        }
    }


}
