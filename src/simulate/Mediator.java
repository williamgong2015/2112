package simulate;

import ast.ProgramImpl;
import ast.Rule;
import intial.Constant;

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
	
	public World getWorld() {
		return world;
	}
	
	/**
	 * Handle set mem out of bound here
	 * @param index
	 * @param val
	 */
	public void setCritterMem(int index, int val) {
		// immutable
		if (index <= 2)
			return;
		// val out of bound
		else if (index == 6 || index == 7) {
			if (val < 0 || val > 99)
				return;
		}
		// index out of bound
		if (index >= critter.getMem(0))
			return;
		critter.setMem(index, val);
	}
	
	/**
	 * Set the last rule being exeuted of that critter
	 * @param r
	 */
	public void setCritterLastRuleExe(Rule r) {
		critter.setLastRuleExe(r);
	}
	
	public void increaseCritterEnergy(int val) {
		int energy = getCritterMem(4);
		setCritterMem(4, energy + val);
	}
	
	public int getCritterMem(int index) {
		return critter.getMem(index);
	}
	
	public ProgramImpl getCritterProgram() {
		return critter.getProgram();
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
	 * Add a new critter into the world
	 * @param elem
	 * @param pos
	 * @return
	 */
	public boolean addCritterAtPosition(Critter elem, Position pos) {
		return world.addCritterAtPosition(elem, pos);
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
	
	public boolean removeCritterAtPosition(Position pos) {
		return world.removeCritterAtPostion(pos);
	}

	public Element getElementNearby(int val) {
		Position pos = critter.getPosition().getNextStep(val);
		Element e = world.getElemAtPosition(pos);
		return e;
	}
	
	public Element getElementAhead(int val) {
		Position pos = critter.getPosition();
		pos = pos.getRelativePos(val, critter.getDir());
		Element e = world.getElemAtPosition(pos);
		return e; 
	}
	
	public int elementDistinguisher(Element e) {
		if(e == null)
			return 0;
		if(e.getType().equals("ROCK"))
			return -1;
		else if(e.getType().equals("CRITTER"))
			return ((Critter)e).appearance();
		else if(e.getType().equals("FOOD"))
			return -1 - ((Food)e).getEnergy();
		return 0;
	}
	
	public Position getCritterPosition() {
		return critter.getPosition();
	}
	
	public int getCritterDirection() {
		return critter.getDir();
	}
	
	public Critter getCritter() {
		return critter;
	}
	
	public int getCritterComplex() {
		return critter.getProgram().getChildren().size() * 
				Constant.RULE_COST + (critter.getMem(1) + critter.getMem(2)) *
				Constant.ABILITY_COST;
	}
	
	public void critterTurn(boolean left) {
		critter.Turn(left);
	}
	
	public void setWantToMate(boolean wantToMate) {
		critter.setWantToMate(wantToMate);
	}
	
	public boolean getWantToMate() {
		return critter.getWantToMate();
	}
	
	public boolean critterAlive() {
		return critter.stillAlive();
	}
	
	/**
	 * @return the position which is in front of the critter
	 */
	public Position getCritterFront() {
		return critter.inFront();
	}
	
	public void handleCritterDeath() {
		Food food = new Food(this.getCritterMem(3) * 
				Constant.FOOD_PER_SIZE);
		Position pos = this.getCritterPosition();
		this.removeCritterAtPosition(pos);
		this.setWorldElemAtPosition(food, pos);
	}
}
