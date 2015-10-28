package interpret;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An list of command for the critter to execute
 */
public class Outcome implements Iterable<String> {

	// all the interpreted and parsed commands
	ArrayList<String> commands;
	
	// if the array of commands has an action in it
	boolean hasAction;
	
	/**
	 * Parse the String of commands and store them in the ArrayList
	 * @param command
	 */
	public Outcome(String command) {
		hasAction = false;
		commands = new ArrayList<String> ();
		String[] temp = command.split(" ");
		for(String i : temp) {
			checkHasAction(i);
			commands.add(i);
		}
	}
	
	private void checkHasAction(String next) {
		if (next.charAt(0) != 'u')
			hasAction = true;
	}
	
	public boolean hasAction() {
		return hasAction;
	}
	
	@Override
	public Iterator<String> iterator() {
		return commands.iterator();
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (String str : this) {
			s.append(str + "\n");
		}
		return s.toString();
	}

}
