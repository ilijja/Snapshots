package servent.message;

public class StopMessage extends Message{

    public StopMessage() {
        super(Type.STOP);
    }

    public StopMessage(StopMessage m) {
        super(m);
    }

    @Override
    protected Message copy() {
        return new StopMessage(this);
    }

    @Override
    public String toString() {
        return "MESSAGE: " + getType().toString();
    }

}
