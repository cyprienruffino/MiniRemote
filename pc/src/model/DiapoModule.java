package model;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Valentin on 21/01/2016.
 */
public class DiapoModule {
    private static DiapoModule instance;
    private Robot robot;

    private DiapoModule() {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Impossible de créer DiapoModule");
        }
    }

    public static DiapoModule getInstance() {
        if (instance == null)
            instance = new DiapoModule();
        return instance;
    }

    public void go_to(int soft, int numPage) throws NotFunctionnalException {
        switch (soft) {
            case 0:
                //Evince
                throw new NotFunctionnalException();
            case 1:
                //LibreOffice
                char[] charArray = String.valueOf(numPage).toCharArray();
                for (char aCharArray : charArray) {
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    hitKey(KeyEvent.getExtendedKeyCodeForChar(aCharArray));
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                }
                hitKey(KeyEvent.VK_ENTER);
                break;
        }
    }

    public void last(int soft) {
        switch (soft) {
            case 0:
                hitKey(KeyEvent.VK_END);
                break;
            case 1:
                hitKey(KeyEvent.VK_END);
                break;
        }
    }

    public void next(int soft) {
        switch (soft) {
            case 0:
                hitKey(KeyEvent.VK_RIGHT);
                break;
            case 1:
                hitKey(KeyEvent.VK_RIGHT);
                break;
        }
    }

    public void origin(int soft) {
        switch (soft) {
            case 0:
                hitKey(KeyEvent.VK_HOME);
                break;
            case 1:
                hitKey(KeyEvent.VK_HOME);
                break;
        }
    }

    public void prec(int soft) {
        switch (soft) {
            case 0:
                hitKey(KeyEvent.VK_LEFT);
                break;
            case 1:
                hitKey(KeyEvent.VK_LEFT);
                break;
        }
    }

    public void start(int soft) {
        switch (soft) {
            case 0:
                hitKey(KeyEvent.VK_F5);
                hitKey(KeyEvent.VK_HOME);
                break;
            case 1:
                hitKey(KeyEvent.VK_F5);
                break;
        }
    }

    public void startHere(int soft) {
        switch (soft) {
            case 0:
                hitKey(KeyEvent.VK_F5);
                break;
            case 1:
                robot.keyPress(KeyEvent.VK_SHIFT);
                hitKey(KeyEvent.VK_F5);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
        }
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
