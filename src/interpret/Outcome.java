package interpret;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An list of command for the critter to execute
 */
public class Outcome implements Iterable<String> {

	// all the interpreted and parsed commands
	ArrayList<String> commands;
	
	/**
	 * Parse the String of commands and store them in the ArrayList
	 * @param command
	 */
	public Outcome(String command) {
		commands = new ArrayList<String> ();
		String[] temp = command.split(" ");
		for(String i : temp)
			commands.add(i);
	}
	
	
	@Override
	public Iterator<String> iterator() {
		return commands.iterator();
	}

}
