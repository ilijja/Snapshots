package cli.command;

import app.App;
import snapshot.SnapshotCollector;

public class BitcakeInfoCommand implements CLICommand {

	private SnapshotCollector collector;
	
	public BitcakeInfoCommand(SnapshotCollector collector) {
		this.collector = collector;
	}
	
	@Override
	public String commandName() {
		return "bitcake_info";
	}

	@Override
	public void execute(String args) {
		App.print("Starting Bitcake Info command");

		collector.startCollecting();
	}

}
