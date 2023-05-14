package snapshot;

import app.App;
import app.Cancellable;
import app.Config;
import app.Servent;

import servent.message.AskMessage;
import servent.message.TerminateMessage;
import servent.processing.MessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SnapshotCollector implements Runnable {

    private final Map<Servent, Snapshot> state;
    private final AtomicBoolean collecting;
    private final AtomicInteger doneCount;
    private volatile boolean working = true;

    public SnapshotCollector() {
        state = new ConcurrentHashMap<>();
        collecting = new AtomicBoolean(false);
        doneCount = new AtomicInteger(0);
    }

    @Override
    public void run() {
        while (working) {
            if (!collecting.get()) {
                continue;
            }

            sendAskMessage();

            if (Config.SNAPSHOT_TYPE == SnapshotType.AB) {
                receiveTellMessages();
                calculateState();
            }

            if (Config.SNAPSHOT_TYPE == SnapshotType.AV) {
                recieveDoneMessages();
                sendTerminateMessage();
            }

            stopSnapshot();
        }
    }

    public void addSnapshot(Servent servent, Snapshot snapshot) {
        if (collecting.get()) {
            state.put(servent, snapshot);
        }
    }

    public void startCollecting() {
        if (collecting.getAndSet(true)) {
            App.error("Already snapshotting");
        }
    }

    private void sendAskMessage() {
        MessageHandler.handle(new AskMessage());
    }

    private void sendTerminateMessage() {
        MessageHandler.handle(new TerminateMessage());
    }

    public void done(){
        this.doneCount.getAndIncrement();
    }

    private void recieveDoneMessages(){
        while (doneCount.get() < Config.SERVENT_COUNT) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (!working) {
                return;
            }
        }

    }

    private void receiveTellMessages() {
        while (state.size() < Config.SERVENT_COUNT) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (!working) {
                return;
            }
        }
    }

    private void printState(){

    }

    private void calculateState() {
        int sum = 0;

        for (Map.Entry<Servent, Snapshot> e : state.entrySet()) {
            int balance = e.getValue().getBalance();
            sum += balance;
            App.print(String.format("Servent %s has %d bitcakes", e.getKey(), balance));
        }

        if (Config.SNAPSHOT_TYPE == SnapshotType.AB) {
            for (int i = 0; i < Config.SERVENT_COUNT; i++) {
                for (int j = 0; j < Config.SERVENT_COUNT; j++) {
                    if (i == j) {
                        continue;
                    }

                    Servent from = Config.SERVENTS.get(i);
                    Servent to = Config.SERVENTS.get(j);

                    int amount = state.get(from).getMinusHistory().get(to) - state.get(to).getPlusHistory().get(from);

                    if (amount > 0) {
                        App.print(String.format("Servent %s has unreceived %d bitcakes from %s", to, amount, from));
                        sum += amount;
                    }
                }
            }
        }

        App.print("Total bitcakes: " + sum);

    }

    private void stopSnapshot() {
        state.clear();
        doneCount.getAndSet(0);

        collecting.set(false);
    }

    public void stop() {
        working = false;
    }

}