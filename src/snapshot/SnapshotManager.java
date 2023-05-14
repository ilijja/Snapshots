package snapshot;

import app.Config;
import app.Servent;
import servent.state.ServentState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnapshotManager {

    private final Map<Servent, Integer> plusHistory;
    private final Map<Servent, Integer> minusHistory;

    public SnapshotManager() {
        plusHistory = new ConcurrentHashMap<>();
        minusHistory = new ConcurrentHashMap<>();

        for (Servent neighbor : Config.SERVENTS) {
            plusHistory.put(neighbor, 0);
            minusHistory.put(neighbor, 0);
        }
    }

    public void plus(int value, Servent servent) {
        plusHistory.compute(servent, (k, v) -> v + value);
    }

    public void minus(int value, Servent servent) {
        minusHistory.compute(servent, (k, v) -> v + value);
    }

    public Snapshot getSnapshot() {
        switch (Config.SNAPSHOT_TYPE) {
            case AB:
                return new Snapshot(Config.CURRENT_SERVENT, plusHistory, minusHistory);
            case AV:
                return new Snapshot(Config.CURRENT_SERVENT, ServentState.getCommittedMessages());
            default:
                return null;
        }
    }
}
