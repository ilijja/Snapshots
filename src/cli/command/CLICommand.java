package cli.command;

/**
 * Defines a command on CLI. Each command has a name
 * and an execute, which takes and parses all the args.
 * @author bmilojkovic
 *
 */
public interface CLICommand {


	String commandName();

	void execute(String args);
}
