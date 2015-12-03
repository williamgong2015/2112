package servlet.connection;

import java.util.ArrayList;
import java.util.Hashtable;
import servlet.element.Element;
import servlet.world.Position;

/**
 * Log record of the updates happened during one version number
 *
 */
public class Log {

	// store the updates of element at certain position
	public Hashtable<Position, Element> updates = new Hashtable<>();
	
	// store the critter died during this version number
	public ArrayList<Integer> deadCritterID = new ArrayList<>();
	
	public Log() {
		
	}
	
}
