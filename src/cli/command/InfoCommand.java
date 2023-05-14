package cli.command;

import app.App;
import app.Config;
import app.Servent;

public class InfoCommand implements CLICommand {

	@Override
	public String commandName() {
		return "info";
	}

	@Override
	public void execute(String args) {
		App.print(String.format("My info: ID:%s IP:%s PORT:%s", Config.CURRENT_SERVENT.toString(), Config.CURRENT_SERVENT.getIp(), Config.CURRENT_SERVENT.getPort()));
		App.print(String.format("Neighbours: %s", Config.CURRENT_SERVENT.getNeighbors()));
	}

}
