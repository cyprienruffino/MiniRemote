package controller.communication.events;

/**
 * Created by Valentin on 21/01/2016.
 */
public class DiapoEvent extends RemoteEvent {
    private DiapoEventType type;
    private int numPage;

    public DiapoEvent(int numPage) {
        type = DiapoEventType.Goto;
        this.numPage = numPage;
    }

    public DiapoEvent(DiapoEventType type) {
        this.type = type;
    }

    public DiapoEventType getType() {
        return type;
    }

    public int getNumPage() {
        return numPage;
    }

    @Override
    public String toString() {
        return "DiapoEvent{" +
                "type=" + type +
                ", numPage=" + numPage +
                '}';
    }

    public enum DiapoEventType {
        Goto, Prec, Start, Next, Origin, StartHere, Last;
    }
}