package servent.message;

import app.Servent;

public class TransactionMessage extends Message{

    private static final long serialVersionUID = -333251402058492901L;

    private final int amount;

    private final Servent destination;

    public TransactionMessage(int amount, Servent destination) {
        super(Message.Type.TRANSACTION);

        this.amount = amount;
        this.destination = destination;
    }

    public TransactionMessage(TransactionMessage m) {
        super(m);

        amount = m.amount;
        destination = m.destination;
    }

    @Override
    protected Message copy() {
        return new TransactionMessage(this);
    }

    @Override
    public String toString() {
        return "MESSAGE: " + getType() + " of " + amount + " bitcakes to " + destination;
    }

    public int getAmount() {
        return amount;
    }

    public Servent getDestination() {
        return destination;
    }
}
