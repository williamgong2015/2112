package simulate;

/**
 * A abstract class denotes object in the world 
 *
 */
public abstract class Element {

	private String type;
	
	// the position of the element in the world
	private Position pos;
	
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
