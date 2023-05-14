package servent.message;

import app.Servent;
import snapshot.Snapshot;

public class TellMessage extends Message{

    Snapshot snapshot;
    Servent destination;
    public TellMessage(Snapshot snapshot, Servent destination) {
        super(Type.TELL);

        this.snapshot = snapshot;
        this.destination = destination;

    }

    public TellMessage(TellMessage m) {
        super(m);

        snapshot = m.snapshot;
        destination = m.destination;
    }

    @Override
    protected Message copy() {
        return new TellMessage(this);
    }

    @Override
    public String toString() {
        return "MESSAGE: " +  getType();
    }

    public Servent getDestination() {
        return destination;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }
}