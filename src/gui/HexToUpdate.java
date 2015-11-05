package gui;

import simulate.Position;

/**
 * A class to denote which hex need to be updated in GUI and how it 
 * should be updated
 *
 */
public class HexToUpdate {

	public HEXType type;
	public Position pos;
	public int direction;
	
	public static enum HEXType {
	    CRITTER, ROCK, FOOD, EMPTY
	}
	
	public HexToUpdate(HEXType type, Position pos, int direction) {
		this.type = type;
		this.pos = pos;
		this.direction = direction;
	}
	
}
