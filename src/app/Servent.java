package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Servent implements Serializable {

    private static final long serialVersionUID = 5304170042791281555L;

    private final int id;
    private final String ip;
    private final int port;
    private final List<Servent> neighbors;

    public Servent(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;

        neighbors = new ArrayList<>();
    }


    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public List<Servent> getNeighbors() {
        return neighbors;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Servent) {
            Servent s = (Servent) obj;
            return id == s.id;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}
