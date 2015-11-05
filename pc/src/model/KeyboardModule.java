package model;

import java.awt.AWTException;
import java.awt.Robot;

/**
 * Created by cyprien on 24/10/15.
 */
public class KeyboardModule {
    //Singleton
    private static KeyboardModule instance;

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

    private Robot robot;

    /**
     * Press one time a keyboard key
     *
     * @param keycode A standard Java keycode
     */
    public void hitKey(int keycode) {
        robot.keyPress(keycode);
        robot.keyRelease(keycode);
    }

    /**
     * Hold a keyboard key
     *
     * @param keycode A standard Java keycode
     */
    public void keyPress(int keycode) {
        robot.keyPress(keycode);
    }

    /**
     * Release a holded keyboard key
     *
     * @param keycode A standard java keycode
     */
    public void keyRelease(int keycode) {
        robot.keyRelease(keycode);
    }

    /**
     * Type, key by key, a string
     *
     * @param toType Command to type
     */
    public void type(String toType) {
        for (int i = 0; i < toType.length(); i++) {
            robot.keyPress(toType.charAt(i));
            robot.keyRelease(toType.charAt(i));
        }
    }

}
