package snapshot;

import app.Servent;
import servent.message.Message;
import servent.message.TransactionMessage;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Snapshot implements Serializable {

    private static final long serialVersionUID = -1934709107142909111L;

    private static final int STARTING_BALANCE = 1000;

    private final Servent servent;
    private final Map<Servent, Integer> plusHistory;
    private final Map<Servent, Integer> minusHistory;
    private final List<Message> messageHistory;

    public Snapshot(Servent servent, Map<Servent, Integer> plusHistory, Map<Servent, Integer> minusHistory) {
        this.servent = servent;
        this.plusHistory = new ConcurrentHashMap<>(plusHistory);
        this.minusHistory = new ConcurrentHashMap<>(minusHistory);

        messageHistory = null;
    }

    public Snapshot(Servent servent, List<Message> messageHistory) {
        this.servent = servent;
        this.messageHistory = messageHistory;

        plusHistory = null;
        minusHistory = null;
    }

    public Snapshot(Snapshot snapshot){
        this.servent = snapshot.servent;
        this.messageHistory = snapshot.messageHistory;

        this.plusHistory = snapshot.plusHistory;
        this.minusHistory = snapshot.minusHistory;
    }

    public int getBalance() {
        int balance = STARTING_BALANCE;

        if (messageHistory != null) {
            for (Message message : messageHistory) {
                if (message.getType() == Message.Type.TRANSACTION) {
                    TransactionMessage transaction = (TransactionMessage) message;

                    if (transaction.getDestination().equals(servent)) {
                        balance += transaction.getAmount();
                    } else if (transaction.getSender().equals(servent)) {
                        balance -= transaction.getAmount();
                    }
                }
            }
        } else if (plusHistory != null && minusHistory != null) {
            for (Map.Entry<Servent, Integer> e : plusHistory.entrySet()) {
                balance += e.getValue();
            }

            for (Map.Entry<Servent, Integer> e : minusHistory.entrySet()) {
                balance -= e.getValue();
            }
        }

        return balance;
    }

    public Map<Servent, Integer> getPlusHistory() {
        return plusHistory;
    }

    public Map<Servent, Integer> getMinusHistory() {
        return minusHistory;
    }

    public List<Message> getMessageHistory() {
        return messageHistory;
    }
}
