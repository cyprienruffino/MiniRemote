package model;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 * Created by cyprien on 24/10/15.
 */
public class CursorModule {

    private float screenHeight;
    private float screenWidth;

    public void setDeviceHeight(float deviceHeight) {
        this.deviceHeight = deviceHeight;
        System.out.println("Height set");
    }

    public void setDeviceWidth(float deviceWidth) {
        this.deviceWidth = deviceWidth;
        System.out.println("Width set");
    }

    private float deviceHeight;
    private float deviceWidth;


    //Singleton
    private static CursorModule instance;

    private CursorModule() throws AWTException {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();
        System.out.println("Device : Height:"+deviceHeight+" Width:"+deviceWidth);
        System.out.println("Screen : Height:"+screenHeight+" Width:"+screenWidth);
        robot = new Robot();
    }

    /**
     * Singleton getter
     *
     * @return Instance of the module
     * @throws AWTException
     */
    public static CursorModule getInstance() throws AWTException {
        if (instance == null) instance = new CursorModule();
        return instance;
    }

    private Robot robot;

    /**
     * Move cursor to [x,y] pixels
     *  @param x Horizontal position
     * @param y Vertical position
     */
    public void moveCursor(float x, float y) {
        System.out.println("X:"+x+" Y:"+y);
        System.out.println("Screen : Height:"+screenHeight+" Width:"+screenWidth);
        System.out.println("Device : Height:"+deviceHeight+" Width:"+deviceWidth);
        System.out.println("Rapports : Height"+ (deviceHeight / screenHeight)+" Width:"+ deviceWidth / screenWidth);

        if (deviceWidth>screenWidth) y = y * (deviceWidth / screenWidth);
        else y = y * (screenWidth / deviceWidth);

        if (deviceHeight>screenHeight) x = x * (deviceHeight / screenHeight);
        else x = x * (screenHeight / deviceHeight);

        System.out.println("X':"+x+" Y':"+y);
        robot.mouseMove((int)x,(int)y);
    }

    /**
     * Click on left mouse button
     */
    public void mouseLeftClick() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    /**
     * Click on right mouse button
     */
    public void mouseRightClick() {
        robot.mousePress(InputEvent.BUTTON3_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
    }

    /**
     * Hold on left mouse button
     */
    public void mouseLeftPress() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
    }

    /**
     * Hold on right mouse button
     */
    public void mouseRightPress() {
        robot.mousePress(InputEvent.BUTTON3_MASK);
    }

    /**
     * Release the left mouse button
     */
    public void mouseLeftRelease() {
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    /**
     * Release the right mouse button
     */
    public void mouseRightRelease() {
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
    }


    /**
     * Scroll wheel by z pixels
     * @param z Scroll
     */
    public void mouseScroll(int z) {
        robot.mouseWheel(z);
    }
}
