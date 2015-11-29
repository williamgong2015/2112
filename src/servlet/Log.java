package servlet;

import java.util.Hashtable;
import servlet.element.Element;
import servlet.world.Position;

/**
 * Log record of the updates happened during one version number
 *
 */
public class Log {

	public Hashtable<Position, Element> updates = 
			new Hashtable<>();
	
	public Log() {
		
	}
	
}
