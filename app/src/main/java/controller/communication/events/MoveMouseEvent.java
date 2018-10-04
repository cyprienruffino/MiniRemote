package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class MoveMouseEvent extends RemoteEvent {

    private float xmove;
    private float ymove;

    public MoveMouseEvent(float xmove, float ymove) {
        this.xmove = xmove;
        this.ymove = ymove;
    }

    public float getXmove() {
        return xmove;
    }

    public float getYmove() {
        return ymove;
    }

    @Override
    public String toString() {
        return "MoveMouseEvent : x : " + xmove + " y : " + ymove;
    }
}
