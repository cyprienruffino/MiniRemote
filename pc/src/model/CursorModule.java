package model;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * Created by cyprien on 24/10/15.
 */
public class CursorModule {

    private float screenHeight;
    private float screenWidth;

    public void setDeviceSize(int height, int width) {
        deviceHeight = height;
        deviceWidth = width;
        landscape = deviceHeight < deviceWidth;
    }

    private float deviceHeight;
    private float deviceWidth;
    private boolean landscape;


    //Singleton
    private static CursorModule instance;

    private CursorModule() throws AWTException {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();
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
        if (landscape) {
            if (deviceWidth > screenWidth) y = y * (deviceWidth / screenWidth);
            else y = y * (screenWidth / deviceWidth);

            if (deviceHeight > screenHeight) x = x * (deviceHeight / screenHeight);
            else x = x * (screenHeight / deviceHeight);
        } else {
            if (deviceWidth > screenWidth) x = x * (deviceWidth / screenWidth);
            else x = x * (screenWidth / deviceWidth);

            if (deviceHeight > screenHeight) y = y * (deviceHeight / screenHeight);
            else y = y * (screenHeight / deviceHeight);

        }
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
