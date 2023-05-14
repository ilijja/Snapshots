package servent.processing;

import app.App;
import app.Cancellable;
import app.Config;
import servent.message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

public class MessageListener implements Runnable, Cancellable {

    private volatile boolean working = true;

    @Override
    public void run() {
        ServerSocket server = null;

        try {
            server = new ServerSocket(Config.CURRENT_SERVENT.getPort());
            server.setSoTimeout(1000);
        } catch (IOException e) {
            App.error("Cannot open listener socket on port " + Config.CURRENT_SERVENT.getPort());
            System.exit(0);
        }

        while (working) {
            try {
                Message message = App.read(server.accept());

                MessageHandler.handle(message);
            } catch (SocketTimeoutException timeoutEx) {

            } catch (IOException e) {
                App.error("Error while listening socket");
            }
        }
    }

    public void stop() {
        working = false;
    }

}
