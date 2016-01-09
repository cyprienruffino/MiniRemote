package model;

import java.awt.*;
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
    public void hitKey(int charCode) {
        /*System.out.println("CHAR : "+ charCode);
        int keycode = KeyEvent.getExtendedKeyCodeForChar(charCode);
        if(charCode==(char)8)keycode=8; //Isolation du backspace
        System.out.println("KEY : " + keycode);
        try{
            robot.keyPress(keycode);
            robot.keyRelease(keycode);
        }catch(IllegalArgumentException e){}*/
        try {
            int keycode=KeyEvent.getExtendedKeyCodeForChar((char)charCode);
            robot.keyPress(keycode);
            robot.keyRelease(keycode);
        } catch (IllegalArgumentException e){
            System.out.println(charCode+" non supporté par "+robot.getClass().toString());
        }
    }

    /**
     * Hold a keyboard key
     *
     * @param charCode A standard Java keycode
     */
    public void keyPress(int charCode){/*
        int keycode = KeyEvent.getExtendedKeyCodeForChar(charCode);
        try {
            robot.keyPress(keycode);
        }catch(IllegalArgumentException e){}*/
        try {
            int keycode=KeyEvent.getExtendedKeyCodeForChar((char)charCode);
            robot.keyPress(keycode);
        } catch (IllegalArgumentException e){
            System.out.println(charCode+" non supporté par "+robot.getClass().toString());
        }
    }

    /**
     * Release a holded keyboard key
     *
     * @param charCode A standard java keycode
     */
    public void keyRelease(int charCode) {
       /* int keycode = KeyEvent.getExtendedKeyCodeForChar(charCode);
        try{
            robot.keyRelease(keycode);
        }catch(IllegalArgumentException e){}*/
        try {
            int keycode=KeyEvent.getExtendedKeyCodeForChar((char)charCode);
            robot.keyRelease(keycode);
        } catch (IllegalArgumentException e){
            System.out.println(charCode+" non supporté par "+robot.getClass().toString());
        }
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