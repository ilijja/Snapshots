package cli.command;


import servent.message.StopMessage;
import servent.processing.MessageHandler;

public class StopCommand implements CLICommand {


	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		MessageHandler.handle(new StopMessage());
	}

}
