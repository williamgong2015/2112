package execute;

import simulate.Mediator;

/**
 * A executor have a Mediator to communicate to execute critter commands
 *
 */
public class Executor {

	private Mediator mediator;
	
	public Executor(Mediator m) {
		mediator = m;
	}
	
	/**
	 * The critter in the mediator wait to absorb solar energy
	 */
	public void critterWait() {
		
	}
	
	/**
	 * The critter uses some energy to move forward to the hex in front of
	 * it or backward to the hex behind it. If it attempts to move and 
	 * there is a critter, food, or a rock in the destination hex, 
	 * the move fails but still takes energy
	 * @param direction 0 denotes move forward, 
	 *                  1 denotes move backward
	 * @return true if the move succeed
	 *         false otherwise
	 */
	public boolean critterMove(int direction) {
		return false;
	}
	
	/**
	 * The critter rotate 60 degrees right or left. This takes little energy
	 * @param direction 0 denotes turn left, 
	 *                  1 denotes turn right
	 */
	public void critterTurn(int direction) {
		
	}
	
	// TODO some more methods
	
}
