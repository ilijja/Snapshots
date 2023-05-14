package servent.message;

import app.App;
import app.Config;
import app.Servent;
import servent.state.ServentState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Message implements Serializable {

    private static final long serialVersionUID = -9075856313609777945L;
    private static final AtomicInteger messageCounter = new AtomicInteger(0);

    private int id;
    private Type type;
    private String text;
    private Servent sender;
    private Servent receiver;
    private List<Servent> route;
    private Map<Servent, Integer> clock;

    public Message(int id, Type type, String text, Servent sender, Servent receiver, Map<Servent, Integer> clock) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        this.clock = clock;
        route = new ArrayList<>();
        route.add(sender);

//        App.print(String.format(" id: %s , ;creadet; clock curr:%s ", id, clock.toString()));
    }

    public Message(Type type, String text) {
        this(messageCounter.getAndIncrement(), type, text, Config.CURRENT_SERVENT, Config.CURRENT_SERVENT, ServentState.incrementClock(Config.CURRENT_SERVENT));
    }

    public Message(Type type) {
        this(type, null);
    }

    public Message(Message message) {
        this(message.id, message.type, message.text, message.sender, message.receiver, message.clock);

        route = message.route;
    }

    public Type getType() {
        return type;
    }

    public Servent getSender() {
        return sender;
    }

    public Servent getLastSender() {
        return route.get(route.size() - 1);
    }

    public boolean containsSender(Servent sender) {
        return route.contains(sender);
    }

    public Servent getReceiver() {
        return receiver;
    }

    public Message setReceiver(Servent receiver) {
        Message message = copy();
        message.receiver = receiver;
        message.route.add(Config.CURRENT_SERVENT);
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            Message message = (Message) obj;
            return id == message.id && sender.equals(message.sender) && receiver.equals(message.receiver);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender);
    }

    @Override
    public String toString() {
        return this.sender + " " + receiver + " " + type + " " + text;
    }

    public Map<Servent, Integer> getClock() {
        return clock;
    }


    protected Message copy() {
        return new Message(this);
    }


    public enum Type {

        TRANSACTION,
        ASK,
        TELL,
        DONE,
        TERMINATE,
        STOP
    }
}
