package app;

import servent.state.ServentState;
import snapshot.SnapshotType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Config {

    public static List<Servent> SERVENTS;

    public static Servent CURRENT_SERVENT;
    public static int SERVENT_COUNT;
    public static SnapshotType SNAPSHOT_TYPE;

    public static Properties load(String path) {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(new File(path)));
        } catch (IOException e) {
            App.error("Cannot open properties file");
            System.exit(0);
        }

        SERVENT_COUNT = Integer.parseInt(properties.getProperty("servent_count"));
        SNAPSHOT_TYPE = SnapshotType.valueOf(properties.getProperty("snapshot").toUpperCase());

        return properties;
    }

    public static void load(String path, int localServent) {
        Properties properties = load(path);

        SERVENTS = new ArrayList<>();
        List<List<Integer>> globalNeighbors = new ArrayList<>();

        for (int i = 0; i < SERVENT_COUNT; i++) {
            int serventPort = Integer.parseInt(properties.getProperty("servent" + i + ".port"));
            String neighbors = properties.getProperty("servent" + i + ".neighbors");

            List<Integer> localNeighbors = new ArrayList<>();

            for (String neighbor : neighbors.split(",")) {
                localNeighbors.add(Integer.parseInt(neighbor));
            }

            globalNeighbors.add(localNeighbors);

            SERVENTS.add(new Servent(i, "localhost", serventPort));
        }

        for (int i = 0; i < SERVENT_COUNT; i++) {
            List<Servent> neighbors = SERVENTS.get(i).getNeighbors();

            for (Integer neighbor : globalNeighbors.get(i)) {
                neighbors.add(SERVENTS.get(neighbor));
            }
        }

        CURRENT_SERVENT = SERVENTS.get(localServent);

        ServentState.initializeVectorClock();
    }

}
