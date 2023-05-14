package servent.message;


import app.Servent;

public class DoneMessage extends Message{

    Servent destination;

    public DoneMessage(Servent servent) {
        super(Type.DONE);
        this.destination = servent;

    }

    public DoneMessage(DoneMessage m) {
        super(m);
        this.destination = m.destination;
    }

    @Override
    protected Message copy() {
        return new DoneMessage(this);
    }

    @Override
    public String toString() {
        return "MESSAGE: " +  getType();
    }

    public Servent getDestination() {
        return destination;
    }

}
