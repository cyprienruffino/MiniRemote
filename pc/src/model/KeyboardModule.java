package model;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

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
     * @param charCode A standard Java keycode
     */
    public void hitKey(char charCode) {
        System.out.println("CHAR : "+ charCode);
        int keycode = KeyEvent.getExtendedKeyCodeForChar(charCode);
        System.out.println("KEY : " + keycode);
        try{
            robot.keyPress(keycode);
            robot.keyRelease(keycode);
        }catch(IllegalArgumentException e){}
    }

    /**
     * Hold a keyboard key
     *
     * @param charCode A standard Java keycode
     */
    public void keyPress(char charCode) {
        int keycode = KeyEvent.getExtendedKeyCodeForChar(charCode);
        try {
            robot.keyPress(keycode);
        }catch(IllegalArgumentException e){}
    }


    /**
     * Release a holded keyboard key
     *
     * @param charCode A standard java keycode
     */
    public void keyRelease(char charCode) {
        int keycode = KeyEvent.getExtendedKeyCodeForChar(charCode);
        try{
            robot.keyRelease(keycode);
        }catch(IllegalArgumentException e){}
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