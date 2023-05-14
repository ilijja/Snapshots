package cli.command;

import app.App;

public class PauseCommand implements CLICommand {

	@Override
	public String commandName() {
		return "pause";
	}

	@Override
	public void execute(String args) {
		int pauseTime = Integer.parseInt(args);

		App.print("Pausing servent for " + pauseTime);

		try {
			Thread.sleep(pauseTime);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
