package servent.message;

public class AskMessage extends Message{

    public AskMessage() {
        super(Type.ASK);
    }

    public AskMessage(AskMessage m) {
        super(m);
    }

    @Override
    protected Message copy() {
        return new AskMessage(this);
    }

    @Override
    public String toString() {
        return "MESSAGE: " + getType();
    }

}
