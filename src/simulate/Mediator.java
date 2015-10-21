package simulate;

/**
 * A communication module between the world, all elements in the world,
 * the interpreter and the executor
 *
 */
public class Mediator {

	private Critter critter;
	private World world;
	
	public Mediator(Critter c, World w) {
		critter = c;
		world = w;
	}
	
	public void setCritterMem(int index, int val) {
		critter.setMem(index, val);
	}
	
	public int getCritterMem(int index) {
		return critter.getMem(index);
	}
	
	/**
	 * Get the element at {@code pos} in the world if 
	 * the position is within the boundary of the world, otherwise
	 * return a rock
	 */
	public Element getWorldElemAtPosition(Position pos) {
		return world.getElemAtPosition(pos);
	}
	
	/**
	 * Set element at {@code pos} 
	 * @param elem element to set
	 * @param pos
	 * @return {@code false} if the {@code pos} is out of the world boundary
	 *         {@code true} otherwise
	 */
	public boolean setWorldElemAtPosition(Element elem, Position pos) {
		return world.setElemAtPosition(elem, pos);
	}
	
	/**
	 * Remove the element at the {@code pos} in the world
	 * @param pos the position to check
	 * @return {@code false} if the position is out of the boundary, 
	 *         {@code false} if there is no element at the {@code pos}
	 *         {@code true} otherwise
	 */
	public boolean removeWorldElemAtPosition(Position pos) {
		return world.removeElemAtPosition(pos);
	}
}
