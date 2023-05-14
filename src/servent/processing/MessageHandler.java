package servent.processing;

import app.*;
import servent.message.*;
import servent.state.ServentState;
import snapshot.Snapshot;
import snapshot.SnapshotCollector;
import snapshot.SnapshotType;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageHandler implements Runnable,Cancellable {


    private static MessageHandler handler;

    private final SnapshotCollector collector;
    private final BlockingQueue<Message> messageQueue;
    private final Set<Message> messages;
    private volatile boolean working = true;
    private Snapshot checkpoint;
    private Map<Servent, Integer> startCheckpointClock;
    private Map<Servent, Integer> endCheckpointClock;


    public MessageHandler(SnapshotCollector collector) {
        this.collector = collector;

        messageQueue = new LinkedBlockingDeque<>();
        messages = Collections.newSetFromMap(new ConcurrentHashMap<>());

        handler = this;
    }

    public static void handle(Message message) {
        try {
            handler.messageQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (working) {
            try {
                process(messageQueue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void process(Message message) throws InterruptedException {
        if (messages.add(message)) {

            App.print(String.format("Received via %s: %s", message.getLastSender(), message));

            ServentState.addPendingMessage(message);
            ServentState.checkPendingMessages(this);

            for (Servent neighbor : Config.CURRENT_SERVENT.getNeighbors()) {
                if (!message.containsSender(neighbor)) {
                    App.print(String.format("Redirecting %s from servent%s to servent%s with [original sender: servent%s]", message.getType(), Config.CURRENT_SERVENT, neighbor, message.getSender()));
                    message.setReceiver(neighbor);
                    App.send(message.setReceiver(neighbor));
                }
            }


            if (message.getType() == Message.Type.STOP) {
                ServentMain.stop();
            }
        }
    }

    public void onCommitted(Message message) {
        switch (message.getType()) {
            case TRANSACTION -> onTransaction(message);
            case ASK -> onAsk(message);
            case TELL -> onTell(message);
            case DONE -> onDone(message);
            case TERMINATE -> onTerminate();
        }
    }

    private void onTransaction(Message message) {
        TransactionMessage transaction = (TransactionMessage) message;

        if (transaction.getDestination().equals(Config.CURRENT_SERVENT)) {
            ServentState.getSnapshotManager().plus(transaction.getAmount(), transaction.getSender());
        }

    }

    private void onAsk(Message message) {
        AskMessage ask = (AskMessage) message;

        Snapshot snapshot = ServentState.getSnapshotManager().getSnapshot();

        if (Config.SNAPSHOT_TYPE == SnapshotType.AB) {
            handle(new TellMessage(snapshot, ask.getSender()));
        }

        if (Config.SNAPSHOT_TYPE == SnapshotType.AV) {
            checkpoint = new Snapshot(snapshot);
            startCheckpointClock = new ConcurrentHashMap<>(ServentState.getClock());
            handle(new DoneMessage(ask.getSender()));
        }

    }

    private void onTell(Message message) {
        TellMessage tell = (TellMessage) message;

        if (tell.getDestination().equals(Config.CURRENT_SERVENT)) {
            collector.addSnapshot(tell.getSender(), tell.getSnapshot());
        }
    }

    private void onDone(Message message){
        DoneMessage done = (DoneMessage) message;
        if(done.getDestination().equals(Config.CURRENT_SERVENT)){
            collector.done();
        }
    }

    private void onTerminate() {

        endCheckpointClock = new ConcurrentHashMap<>(ServentState.getClock());

        Snapshot snapshot = this.checkpoint;
        List<Message> messageHistory = snapshot.getMessageHistory();

        int currentBitcakes = snapshot.getBalance();

        int totalBitcakes = currentBitcakes;

        for (Servent servent : Config.SERVENTS) {
            if (servent.equals(Config.CURRENT_SERVENT)) {
                continue;
            }

            int amount = 0;

            for (Message m : ServentState.getCommittedMessages()) {
                if (m.getType() == Message.Type.TRANSACTION) {
                    TransactionMessage t = (TransactionMessage) m;
                    if (inRange(t) && !messageHistory.contains(t) && t.getSender().equals(servent) && t.getDestination().equals(Config.CURRENT_SERVENT)) {
                        amount += t.getAmount();
                    }
                }
            }

            totalBitcakes += amount;
            App.print(String.format("Servent %s has %d bitcakes, with additional %d unreceived bitcakes from %s", Config.CURRENT_SERVENT, currentBitcakes, amount, servent));
        }

        App.print(String.format("Total bitcakes: %s", totalBitcakes));
        checkpoint = null;

    }

    private boolean inRange(Message message){
        Map<Servent, Integer> messageClock = message.getClock();
        Servent sender = message.getSender();
        return messageClock.get(sender) >= startCheckpointClock.get(sender) && messageClock.get(sender) <= endCheckpointClock.get(sender);

    }


    @Override
    public void stop() {
        working = false;
    }
}
