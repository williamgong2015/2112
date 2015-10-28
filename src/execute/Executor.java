package execute;

import java.io.StringReader;
import java.util.ArrayList;

import ast.ProgramImpl;
import ast.Rule;
import exceptions.SyntaxError;
import interpret.Outcome;
import intial.Constant;
import parse.ParserImpl;
import parse.Tokenizer;
import simulate.Critter;
import simulate.Element;
import simulate.Food;
import simulate.Mediator;
import simulate.Position;
import util.Formula;

/**
 * A executor have a Mediator to communicate to execute critter commands
 *
 */
public class Executor {

	private Mediator mediator;
	
	public Executor(Mediator m) {
		mediator = m;
	}
	
	public void execute(Outcome out) {
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
			// serve
			case 's':
				e = Integer.parseInt(i.substring(1));
				if (e < 0)
					e = 0;
				critterServe(e);
				break;
			// tag
			case 't':
				e = Integer.parseInt(i.substring(1));
				if (e < 0 || e > 99)
					break;
				critterTag(e);
				break;
			default:
				if (i.equals("wait")) 
					critterWait();
				else if (i.equals("forward"))
					critterMove(true);
				else if (i.equals("backward"))
					critterMove(false);
				else if (i.equals("left"))
					critterTurn(true);
				else if (i.equals("right"))
					critterTurn(false);
				else if (i.equals("eat"))
					critterEat();
				else if (i.equals("attack"))
					critterAttack();
				else if (i.equals("grow"))
					critterGrow();
				else if (i.equals("bud"))
					critterBud();
				else if (i.equals("mate"))
					critterAttempToMate();
			}
		}
	}
	
	/**
	 * The critter in the mediator wait to absorb solar energy
	 */
	public void critterWait() {
		mediator.increaseCritterEnergy(Constant.SOLAR_FLUX * 
				mediator.getCritterMem(3));
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
		mediator.increaseCritterEnergy(-Constant.MOVE_COST * 
				mediator.getCritterMem(3));
		if (!mediator.critterAlive()) {
			mediator.handleCritterDeath();
			return;
		}
		Position lastPos = mediator.getCritterPosition();
		int dir = mediator.getCritterDirection();
		Position nextPos;
		if (forward) 
			nextPos = lastPos.getNextStep(dir);
		else
			nextPos = lastPos.getNextStep(Math.abs(3 - dir));
		Element e = mediator.getWorldElemAtPosition(nextPos);
		// if the nextPos is empty, move the critter forward
		if (e == null) {
			// don't use delete critter
			mediator.removeWorldElemAtPosition(lastPos);
			mediator.setWorldElemAtPosition(mediator.getCritter(), nextPos);
		}
	}
	
	
	/**
	 * The critter rotate 60 degrees right or left. This takes the energy 
	 * equals to the critter's size
	 * @param direction true denotes turn left, 
	 *                  false denotes turn right
	 */
	public void critterTurn(boolean left) {
		mediator.increaseCritterEnergy(-mediator.getCritterMem(3));
		if (!mediator.critterAlive()) {
			mediator.handleCritterDeath();
			return;
		}
		mediator.critterTurn(left);
	}

	
	/**
	 * The critter may eat some of the food that might be available on the 
	 * hex ahead of it, gaining the same amount of energy as the food it 
	 * consumes. When there is more food available on the hex than the 
	 * critter can absorb, the remaining food is left on the hex.
	 */
	private void critterEat() {
		Element e = mediator.getElementAhead(1);
		if (e == null)
			return;
		if (e.getType().equals("FOOD")) {
			int foodEnergy = mediator.elementDistinguisher(e);
			int currentEnergy = mediator.getCritter().getMem(4);
			int maxEnergy = mediator.getCritter().maxEnergy();
			if (foodEnergy + currentEnergy > maxEnergy) {
				mediator.getCritter().setMem(4, maxEnergy);
				((Food) e).setAmount(foodEnergy + currentEnergy - maxEnergy);
			}
			else {
				mediator.getCritter().setMem(4, foodEnergy + currentEnergy);
				Position foodPos = mediator.getCritterFront();
				mediator.removeWorldElemAtPosition(foodPos);
			}
		}
	}
	
	/**
	 * A critter may convert some of its own energy into food added to the
	 * hex in front of it, if that hex is either empty or already contains 
	 * some food.
	 */
	public void critterServe(int energyToServe) {
		mediator.increaseCritterEnergy(-mediator.getCritterMem(3));
		if (!mediator.critterAlive()) {
			mediator.handleCritterDeath();
			return;
		}
		Element e = mediator.getElementAhead(1);
		int currentEnergy = mediator.getCritter().getMem(4);
		if (energyToServe > currentEnergy)
			energyToServe = currentEnergy;
		if (e == null) {
			Food newFood = new Food(energyToServe);
			mediator.setWorldElemAtPosition(newFood, 
					mediator.getCritterFront());
		}
		else if(e.getType().equals("FOOD")) {
			int foodEnergy = mediator.elementDistinguisher(e);
			((Food) e).setAmount(foodEnergy + energyToServe);
		}
		mediator.getCritter().setMem(4, currentEnergy - energyToServe);
	}
	
	
	/**
	 * It may attack a critter directly in front of it. The attack removes an 
	 * amount of energy from the attacked critter that is determined by the 
	 * size and offensive ability of the attacker and the defensive ability 
	 * of the victim.
	 */
	public void critterAttack() {
		mediator.increaseCritterEnergy(-Constant.ATTACK_COST * 
				mediator.getCritterMem(3));
		if (!mediator.critterAlive()) {
			mediator.handleCritterDeath();
			return;
		}
		Element e = mediator.getElementAhead(1);
		if (e == null || !e.getType().equals("CRITTER"))
			return;
		Critter victim = (Critter) e;
		int damage = (int) (Constant.BASE_DAMAGE * mediator.getCritterMem(3) *
				Formula.logistic(Constant.DAMAGE_INC * 
						(mediator.getCritterMem(3) * mediator.getCritterMem(2)
						- victim.getMem(3) * victim.getMem(1))));
		victim.setMem(4, victim.getMem(4) - damage);
	}
	
	/**
	 * The critter may tag the critter in front of it, e.g., 
	 * to mark that critter as an enemy or a friend
	 * 
	 * Require: {@code v} is a valid tag value: 0 <= v <= 99
	 */
	public void critterTag(int v) {
		mediator.increaseCritterEnergy(-mediator.getCritterMem(3));
		if (!mediator.critterAlive()) {
			mediator.handleCritterDeath();
			return;
		}
		Element e = mediator.getElementAhead(1);
		if (e == null || !e.getType().equals("CRITTER"))
			return;
		Critter receiver = (Critter) e;
		receiver.setMem(6, v);
	}
	
	/**
	 * A critter may use energy to increase its size by one unit.
	 */
	public void critterGrow() {
		mediator.increaseCritterEnergy(-Constant.GROW_COST * 
				mediator.getCritterMem(3) * mediator.getCritterComplex());
		if (!mediator.critterAlive()) {
			mediator.handleCritterDeath();
			return;
		}
		mediator.setCritterMem(3, mediator.getCritterMem(3) + 1);
	}
	
	/**
	 * A critter may use a large amount of its energy to produce a new, 
	 * smaller critter behind it with the same genome (possibly with some 
	 * random mutations).
	 */
	public void critterBud() {
		mediator.increaseCritterEnergy(-Constant.BUD_COST * 
				mediator.getCritterComplex());
		if (!mediator.critterAlive()) {
			mediator.handleCritterDeath();
			return;
		}
		Critter p = mediator.getCritter();
		// the position after the critter has been occupied
		if (mediator.getElementAhead(-1) != null)
			return;
		int[] mem = new int[p.getMem(0)];
		mem[0] = p.getMem(0);
		mem[1] = p.getMem(1);
		mem[2] = p.getMem(2);
		mem[3] = 1;
		mem[4] = Constant.INITIAL_ENERGY;
		mem[5] = 1;
		mem[6] = 0;
		mem[7] = 0;
		for (int i = 8; i < mem.length; ++i) 
			mem[i] = 0;
		String name = "Child of " + p.getName();
		ProgramImpl pro = p.getProgram();
		pro.mutate();
		Critter newCritter = new Critter(mem, name, pro);
		newCritter.setDir(util.RandomGen.randomNumber(6));
		Position pos = p.getPosition().getRelativePos(1, 3);
		mediator.addCritterAtPosition(newCritter, pos);
	}
	
	/**
	 * A critter may attempt to mate with another critter in front of it. 
	 * For this to be successful, the critter in front must also be facing 
	 * toward it and attempting to mate in the same time step. If mating 
	 * is successful, both critters use energy to create a new size-1 critter 
	 * whose genome is theresult of merging the genomes of its parents. 
	 * Unsuccessful mating uses little energy.
	 * @throws SyntaxError 
	 */
	public void critterAttempToMate() {
		mediator.increaseCritterEnergy(-mediator.getCritterMem(3));
		if (!mediator.critterAlive()) {
			mediator.handleCritterDeath();
			return;
		}
		Element e = mediator.getElementAhead(1);
		if (e == null || !e.getType().equals("CRITTER"))
			return;
		Critter c = (Critter) e;
		mediator.setWantToMate(true);
		if (c.getWantToMate()) 
			critterMate(mediator.getCritter(), c);
	}
	
	/**
	 * Two Critter mate
	 * @param first
	 * @param second
	 * @throws SyntaxError 
	 */
	public void critterMate(Critter first, Critter second) {
		// cost more energy than attempting mate if the mate would success
		int energyBalanceFirst = first.getMem(3) - 
				Constant.MATE_COST * first.getComplexity();
		int energyBalanceSecond = second.getMem(3) - 
				Constant.MATE_COST * second.getComplexity();
		
		// mate not succeed because one of the critter would die
		if (first.getMem(4) <= energyBalanceFirst ||
				second.getMem(4) <= energyBalanceSecond)
			return;
			
		first.setMem(4, first.getMem(4) + energyBalanceFirst);
		second.setMem(4, first.getMem(4) + energyBalanceSecond);
		
		Position posToSet;
		// mate not succeed mate because both position 
		// after the critters are occupied
		if (mediator.getWorldElemAtPosition(
				first.getPosition().getRelativePos(1, 3)) == null && 
				mediator.getWorldElemAtPosition(
						second.getPosition().getRelativePos(1, 3)) == null)
			return;
		// get the position to place the new critter
		else if (mediator.getWorldElemAtPosition(
				first.getPosition().getRelativePos(1, 3)) == null)
			posToSet = second.getPosition().getRelativePos(1, 3);
		else if (mediator.getWorldElemAtPosition(
						second.getPosition().getRelativePos(1, 3)) == null)
			posToSet = first.getPosition().getRelativePos(1, 3);
		else {
			if (util.RandomGen.randomNumber(2) == 0)
				posToSet = first.getPosition().getRelativePos(1, 3);
			else
				posToSet = second.getPosition().getRelativePos(1, 3);
		}
			
		// assume the new critter's memory length is inherit randomly chosen 
		// from father and mother
		int[] mem = new int[randomPickMem(first, second, 0)];
		mem[0] = mem.length;
		mem[1] = randomPickMem(first, second, 1);
		mem[2] = randomPickMem(first, second, 2);
		mem[3] = 1;
		mem[4] = Constant.INITIAL_ENERGY;
		mem[5] = 1;
		mem[6] = 0;
		mem[7] = 0;
		for (int i = 8; i < mem.length; ++i) 
			mem[i] = 0;
		String name = "Child of " + first.getName() + 
				" and " + second.getName();
		ArrayList<Rule> firstRules = first.getProgram().getChildren();
		ArrayList<Rule> secondRules = second.getProgram().getChildren();
		int ruleSize;
		if (util.RandomGen.randomNumber(2) == 0)
			ruleSize = firstRules.size();
		else
			ruleSize = secondRules.size();
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < ruleSize; ++i) 
			b.append(randomPickRule(firstRules, secondRules, i));
		StringReader r = new StringReader(b.toString());
		Tokenizer t = new Tokenizer(r);
		ProgramImpl pro = null;
		try {
			pro = ParserImpl.parseProgram(t);
		} catch (Exception e) {
			System.out.println("can't parse the mate program");
		}
		Critter newCritter = new Critter(mem, name, pro);
		newCritter.setDir(util.RandomGen.randomNumber(6));
		mediator.addCritterAtPosition(newCritter, posToSet);
	}
	
	public String randomPickRule(ArrayList<Rule> r1, ArrayList<Rule> r2, 
			int index) {
		if (r1.size() <= index)
			return r2.get(index).toString();
		else if (r2.size() <= index)
			return r1.get(index).toString();
		if (util.RandomGen.randomNumber(2) == 0)
			return r1.get(index).toString();
		return r2.get(index).toString();
	}
	
	
	public int randomPickMem(Critter first, Critter second, int index) {
		if (util.RandomGen.randomNumber(2) == 0)
			return first.getMem(index);
		else
			return second.getMem(index);
	}
	
	// TODO some more methods
	//energy more than possible ,wait
}
