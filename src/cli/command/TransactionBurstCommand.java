package cli.command;

import app.App;
import app.Config;
import app.Servent;
import servent.message.Message;
import servent.message.TransactionMessage;
import servent.processing.MessageHandler;
import servent.state.ServentState;
import snapshot.SnapshotManager;

import java.util.concurrent.ThreadLocalRandom;

public class TransactionBurstCommand implements CLICommand {

	private static final int TRANSACTION_COUNT = 5;

	private static final int BURST_WORKERS = 1;

	private static final int MAX_TRANSFER_AMOUNT = 30;


	@Override
	public String commandName() {
		return "transaction_burst";
	}

	private class TransactionBurstWorker implements Runnable{


		@Override
		public void run() {
			for (int i = 0; i < TRANSACTION_COUNT; i++) {
				for (Servent destination : Config.SERVENTS) {
					if (destination.equals(Config.CURRENT_SERVENT)) {
						continue;
					}

					ServentState.getSnapshotManager().minus(MAX_TRANSFER_AMOUNT, destination);
					MessageHandler.handle(new TransactionMessage(MAX_TRANSFER_AMOUNT, destination));
				}
			}
		}
	}

	@Override
	public void execute(String args) {
		App.print(String.format("Bursting %d transactions", TRANSACTION_COUNT));

		for (int i = 0; i < BURST_WORKERS; i++) {
			Thread t = new Thread(new TransactionBurstWorker());

			t.start();
		}
	}



}
