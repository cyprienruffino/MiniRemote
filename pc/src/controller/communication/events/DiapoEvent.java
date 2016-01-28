package controller.communication.events;

/**
 * Created by Valentin on 21/01/2016.
 */
public class DiapoEvent extends RemoteEvent {
    private DiapoEventType type;
    private int soft;
    private int numPage;

    public DiapoEvent(DiapoEventType type, int soft) {
        this.type = type;
        this.soft = soft;
    }

    public DiapoEvent(int num, int id) {
        type = DiapoEventType.Goto;
        this.numPage = numPage;
        soft = id;
    }

    public DiapoEventType getType() {
        return type;
    }

    public int getNumPage() {
        return numPage;
    }

    public int getSoft() {
        return soft;
    }

    @Override
    public String toString() {
        return "DiapoEvent{" +
                "type=" + type +
                ", soft=" + soft +
                ", numPage=" + numPage +
                '}';
    }

    public enum DiapoEventType {
        Goto, Prec, Start, Next, Origin, StartHere, Last;
    }
}