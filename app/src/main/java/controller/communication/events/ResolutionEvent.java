package controller.communication.events;

/**
 * Created by cyprien on 26/12/15.
 */
public class ResolutionEvent extends RemoteEvent {
    private int height;
    private int width;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public ResolutionEvent(int height, int width){
        this.height=height;
        this.width=width;
    }

    @Override
    public String toString() {
        return "ResolutionEvent{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }
}
