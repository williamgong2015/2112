package execute;

import interpret.Outcome;
import intial.Constant;
import simulate.Element;
import simulate.Food;
import simulate.Mediator;
import simulate.Position;

/**
 * A executor have a Mediator to communicate to execute critter commands
 *
 */
public class Executor {

	private Mediator mediator;
	
	public Executor(Mediator m) {
		mediator = m;
	}
	
	
	public  void execute(Outcome out) {
		for(String i : out) {
			char ch = i.charAt(0);
			int e;
			switch(ch) {
			case 'u':
				String[] temp = i.substring(1).split(",");
				int e1 = Integer.parseInt(temp[0]);
				int e2 = Integer.parseInt(temp[1]);
				mediator.setCritterMem(e1, e2);
				break;
			case 's':
				e = Integer.parseInt(i.substring(1));//TODO
				break;
			case 't':
				e = Integer.parseInt(i.substring(1));//TODO
				break;
			default:
				if(i.equals("wait")) 
					critterWait();
				if(i.equals("forward"))
					critterMove(true);
				if(i.equals("backward"))
					critterMove(false);
				if(i.equals("left"))
					critterTurn(true);
				if(i.equals("right"))
					critterTurn(false);
				if(i.equals("eat"))
					critterEat();
			}
			
		}
	}
	private void critterEat() {
		int e = mediator.getCritterAhead(1);
		//TODOmediator.increaseCritterEnergy(- Constant.e);
		if(e < -1) {
			Position pos = mediator.getCritterPosition();
			int dir = mediator.getCritterDirection();
			Food f= (Food) mediator.getWorldElemAtPosition(pos.get(dir));
			if(f.getEnergy() > mediator.getCritter().maxEnergy()) {
				mediator.getCritter().setMem(3, mediator.getCritter().maxEnergy());
				f.setAmount();
			}
		}
		
	}


	/**
	 * The critter uses some energy to move forward to the hex in front of
	 * it or backward to the hex behind it. If it attempts to move and 
	 * there is a critter, food, or a rock in the destination hex, 
	 * the move fails but still takes energy
	 * @param forward true denotes move forward, 
	 *                false denotes move backward
	 * @return true if the move succeed
	 *         false otherwise
	 */
	public void critterMove(boolean forward) {
		mediator.increaseCritterEnergy(-Constant.MOVE_COST * mediator.getCritterMem(3));
		Position nextPos = mediator.getCritterPosition();
		int dir = mediator.getCritterDirection();
		if(forward) 
			nextPos.move(1, dir);
		else
			nextPos.move(1, Math.abs(3 - dir));
		Element e =mediator.getWorldElemAtPosition(nextPos);
		if(e == null) {
			mediator.removeWorldElemAtPosition(mediator.getCritterPosition());
			mediator.setWorldElemAtPosition(mediator.getCritter(), nextPos);
		}
	}
	
	/**
	 * The critter in the mediator wait to absorb solar energy
	 */
	public void critterWait() {
		mediator.increaseCritterEnergy(Constant.SOLAR_FLUX);
	}
	
	/**
	 * The critter rotate 60 degrees right or left. This takes little energy
	 * @param direction true denotes turn left, 
	 *                  false denotes turn right
	 */
	public void critterTurn(boolean left) {
		mediator.increaseCritterEnergy(-);
		mediator.critterTurn(left);
	}
	
	// TODO some more methods
	//energy more than possible ,wait
}
