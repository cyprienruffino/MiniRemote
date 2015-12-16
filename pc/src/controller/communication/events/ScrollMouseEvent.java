package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class ScrollMouseEvent extends RemoteEvent {
    private int scroll;

    public ScrollMouseEvent(int scroll) {
        this.scroll = scroll;
    }

    public int getScroll() {
        return scroll;
    }
}
