package model;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Valentin on 21/01/2016.
 */
public class DiapoModule {
    private static DiapoModule instance;

    public static DiapoModule getInstance() {
        if (instance == null)
            instance = new DiapoModule();
        return instance;
    }

    private Robot robot;

    public DiapoModule() {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Impossible de créer DiapoModule");
        }
    }

    public void go_to(int numPage) {
        char[] charArray = String.valueOf(numPage).toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            hitKey(KeyEvent.getExtendedKeyCodeForChar(charArray[i]));
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }
        hitKey(KeyEvent.VK_ENTER);
    }

    public void last() {
        //TODO trouver une alternative
        hitKey(KeyEvent.VK_FINAL);
    }

    public void next() {
        hitKey(KeyEvent.VK_RIGHT);
    }

    public void origin() {
        //TODO trouver la touche KeyEvent pour Début
    }

    public void prec() {
        hitKey(KeyEvent.VK_LEFT);
    }

    public void start() {
        hitKey(KeyEvent.VK_F5);
    }

    public void startHere() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        hitKey(KeyEvent.VK_F5);
        robot.keyRelease(KeyEvent.VK_SHIFT);
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
