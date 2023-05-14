package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import app.App;
import app.Cancellable;
import cli.command.BitcakeInfoCommand;
import cli.command.CLICommand;
import cli.command.InfoCommand;
import cli.command.PauseCommand;
import cli.command.StopCommand;
import cli.command.TransactionBurstCommand;
import snapshot.SnapshotCollector;


public class CLIParser implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	private final List<CLICommand> commandList;
	
	public CLIParser(SnapshotCollector snapshotCollector) {
		this.commandList = new ArrayList<>();
		
		commandList.add(new InfoCommand());
		commandList.add(new PauseCommand());
		commandList.add(new TransactionBurstCommand());
		commandList.add(new BitcakeInfoCommand(snapshotCollector));
		commandList.add(new StopCommand());
	}
	
	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);

		while (working) {
			String commandLine = sc.nextLine();
			
			int spacePos = commandLine.indexOf(" ");
			
			String commandName = null;
			String commandArgs = null;
			if (spacePos != -1) {
				commandName = commandLine.substring(0, spacePos);
				commandArgs = commandLine.substring(spacePos+1, commandLine.length());
			} else {
				commandName = commandLine;
			}
			
			boolean found = false;
			
			for (CLICommand cliCommand : commandList) {
				if (cliCommand.commandName().equals(commandName)) {
					cliCommand.execute(commandArgs);
					found = true;
					break;
				}
			}

			if (!found) {
				App.print("Wrong command");
			}
		}
		
		sc.close();
	}
	
	@Override
	public void stop() {
		this.working = false;
		
	}
}
