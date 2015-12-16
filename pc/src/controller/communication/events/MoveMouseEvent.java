package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class MoveMouseEvent extends RemoteEvent {

    private int xmove;
    private int ymove;

    public MoveMouseEvent(int xmove, int ymove) {
        this.xmove = xmove;
        this.ymove = ymove;
    }

    public int getXmove() {
        return xmove;
    }

    public int getYmove() {
        return ymove;
    }
}
