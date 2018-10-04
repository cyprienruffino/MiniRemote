package controller.communication.events;

import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by whiteshad on 12/11/15.
 */
public class RemoteEventHandler extends Observable{
    public final static int NEW_REMOTE_EVENT=0x1;
    private ConcurrentLinkedQueue<RemoteEvent> eventsQueue;

    public RemoteEventHandler() {
        this.eventsQueue = new ConcurrentLinkedQueue<RemoteEvent>();
    }

    public void addRemoteEvent(RemoteEvent event){
        this.eventsQueue.add(event);
        this.notifyAll();
        notify(NEW_REMOTE_EVENT);
    }

    public RemoteEvent getRemoteEvent(){
        RemoteEvent e=this.eventsQueue.poll();
        this.notifyAll();
        return e;
    }

    private void notify(int event){
        setChanged();
        notifyObservers(event);
    }
}
