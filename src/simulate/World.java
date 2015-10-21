package simulate;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.experimental.theories.Theories;

/**
 * The critter world
 *
 */
public class World {

	// how many turns has passed in the world
	private int turns;
	
	// the scale of the world
	private int row;
	private int column;
	
	
	// maps position to element in the world
	private Hashtable<Position, Element> hexes;
	
	// order of critters in the world to take actions
	private ArrayList<Critter> order;
	
	
	/**
	 * A new turn of the world
	 */
	public void lapse() {
		turns++;
		// TODO: do something
	}
	
	/**
	 * Check the position is within the world boundary
	 * @param position the position to check
	 * @return true if the position is within the boundary,
	 *         false otherwise
	 */
	private boolean checkPosition(Position position) {
		return false;
	}
	
	/**
	 * Get the element at {@code position} in the world if 
	 * the position is within the boundary of the world, otherwise
	 * return a rock
	 */
	public Element getElemAtPosition(Position pos) {
		if (checkPosition(pos))
			return hexes.get(pos);
		return new Rock();
	}
	
	/**
	 * Set element at {@code pos} 
	 * @param elem element to set
	 * @param pos
	 * @return {@code false} if the {@code pos} is out of the world boundary
	 *         {@code true} otherwise
	 */
	public boolean setElemAtPosition(Element elem, Position pos) {
		if (!checkPosition(pos))
			return false;
		removeElemAtPosition(pos);
		return true;
	}
	
	/**
	 * Remove the element at the {@code pos} in the world
	 * @param pos the position to check
	 * @return {@code false} if the position is out of the boundary, 
	 *         {@code false} if there is no element at the {@code pos}
	 *         {@code true} otherwise
	 */
	public boolean removeElemAtPosition(Position pos) {
		if (!checkPosition(pos))
			return false;
		if (!hexes.containsKey(pos))
			return false;
		hexes.remove(pos);
		return true;
	}
	
	/**
	 * Effect: Print an ASCII-art map of the world
	 */
	public void printASCIIMap() {
		
	}
	
	
	
	
}
