package app;


import servent.processing.MessageListener;
import cli.CLIParser;
import servent.processing.MessageHandler;
import snapshot.SnapshotCollector;

public class ServentMain {

    private static SnapshotCollector collector;
    private static MessageListener listener;
    private static CLIParser parser;

    private static MessageHandler handler;

    public static void main(String[] args) {
        Config.load(args[0], Integer.parseInt(args[1]));

        App.print("Starting servent " + Config.CURRENT_SERVENT);

        collector = new SnapshotCollector();
        new Thread(collector).start();

        handler = new MessageHandler(collector);
        new Thread(handler).start();

        listener = new MessageListener();
        new Thread(listener).start();

        parser = new CLIParser(collector);
        new Thread(parser).start();

    }

    public static void stop() {
        App.print("Stopping servents");

        collector.stop();
        listener.stop();
        parser.stop();
        handler.stop();

    }
}
