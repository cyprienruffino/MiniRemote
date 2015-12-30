package model;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;

/**
 * Created by cyprien on 24/10/15.
 */
public class CursorModule {

    private float screenHeight;
    private float screenWidth;

    public void setDeviceHeight(int deviceHeight) {
        this.deviceHeight = deviceHeight;
    }

    public void setDeviceWidth(int deviceWidth) {
        this.deviceWidth = deviceWidth;
    }

    private float deviceHeight;
    private float deviceWidth;


    //Singleton
    private static CursorModule instance;

    private CursorModule() throws AWTException {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();
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
        robot.mousePress(0);
        robot.mouseRelease(0);
    }

    /**
     * Click on right mouse button
     */
    public void mouseRightClick() {
        robot.mousePress(1);
        robot.mouseRelease(1);
    }

    /**
     * Hold on left mouse button
     */
    public void mouseLeftPress() {
        robot.mousePress(0);
    }

    /**
     * Hold on right mouse button
     */
    public void mouseRightPress() {
        robot.mousePress(1);
    }

    /**
     * Release the left mouse button
     */
    public void mouseLeftRelease() {
        robot.mouseRelease(0);
    }

    /**
     * Release the right mouse button
     */
    public void mouseRightRelease() {
        robot.mouseRelease(1);
    }


    /**
     * Scroll wheel by z pixels
     *
     * @param z Scroll
     */
    public void mouseScroll(int z) {
        robot.mouseWheel(z);
    }
}
