package interpret;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An list of command for the critter to execute
 */
public class Outcome implements Iterable {

	// all the interpreted and parsed commands
	ArrayList<String> commands;
	
	/**
	 * Parse the String of commands and store them in the ArrayList
	 * @param command
	 */
	public Outcome(String command) {
		
	}
	
	
	@Override
	public Iterator iterator() {
		return commands.iterator();
	}

}
