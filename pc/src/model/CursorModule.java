package model;

import java.awt.AWTException;
import java.awt.Robot;

/**
 * Created by cyprien on 24/10/15.
 */
public class CursorModule {

    //Singleton
    private static CursorModule instance;

    private CursorModule() throws AWTException {
        robot = new Robot();
    }

    public static CursorModule getInstance() throws AWTException {
        if (instance == null) instance = new CursorModule();
        return instance;
    }

    private Robot robot;

    /**
     * Move cursor by [x,y] pixels
     *
     * @param x Horizontal movement
     * @param y Vertical movement
     */
    public void moveCursor(int x, int y) {
        robot.mouseMove(x, y);
    }

    /**
     * Set cursor at position [x,y]
     *
     * @param x Horizontal movement
     * @param y Vertical movement
     */
    public void setCursorPos(int x, int y) {
        //TODO
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
