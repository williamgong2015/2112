package gui;

import simulate.Position;

/**
 * A class store the information of which hex need to be updated in GUI and
 * how it should be updated. It is passed from underlying critter world program
 * to controller (Main.java) and tell the controller how to update the view. 
 *
 */
public class HexToUpdate {

	public HEXType type;
	public Position pos;
	public int size;
	public int direction;
	
	public static enum HEXType {
	    CRITTER, ROCK, FOOD, EMPTY
	}
	
	public HexToUpdate(HEXType type, Position pos, int direction, int size) {
		this.type = type;
		this.pos = pos;
		this.direction = direction;
		this.size = size;
	}
	
}
