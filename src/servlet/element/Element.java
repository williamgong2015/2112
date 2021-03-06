package servlet.element;

import servlet.world.Position;

/**
 * A abstract class denotes object in the world 
 *
 */
public abstract class Element {

	private String type;
	
	// the position of the element in the world
	private Position pos;
	
	public final static String FOOD = "FOOD";
	public final static String ROCK = "ROCK";
	public final static String CRITTER = "CRITTER";
	public final static String NOTHING = "NOTHING";
	
	public Element(String t) {
		type = t;
	}
	
	/**
	 * @return the type of the element
	 */
	public String getType() {
		return type;
	}
	
	public Position getPosition() {
		return pos;
	}
	
	public void setPosition(Position pos) {
		this.pos = pos;
	}
}
