package servlet.executor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;

import game.constant.Constant;
import game.constant.IDX;
import game.exceptions.SyntaxError;
import game.utils.Formula;
import servlet.ast.ProgramImpl;
import servlet.ast.Rule;
import servlet.connection.Log;
import servlet.element.Critter;
import servlet.element.Element;
import servlet.element.Food;
import servlet.interpreter.Outcome;
import servlet.parser.ParserImpl;
import servlet.parser.Tokenizer;
import servlet.world.Position;
import servlet.world.World;

/**
 * A executor to execute a list of commands on the critter and world
 *
 */
public class Executor {

	private World w;
	private Critter c;
	ResultList resultList;
	
	public Executor(World world,Critter critter) {
		w = world;
		c = critter;
	}
	
	/**
	 * Execute the data update of the world, and add GUI related to the 
	 * size change of critters and turning of the critters. (Other GUI updates
	 * has been handled in {@code setElemAtPosition} and 
	 * {@code removeElemAtPosition} in {@code World} class
	 * @param out
	 * @param hexToUpdate
	 * @return
	 */
	public ResultList execute(Outcome out, Vector<Log> logs) {
		resultList = new ResultList();
		for(String i : out) {
			char ch = i.charAt(0);
			int e;
			switch(ch) {
			case 'u':
				String[] temp = i.substring(1).split(",");
				int e1 = Integer.parseInt(temp[0]);
				int e2 = Integer.parseInt(temp[1]);
				critterUpdate(e1, e2);
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
					critterTurn(true, logs);
				else if (i.equals("right"))
					critterTurn(false, logs);
				else if (i.equals("eat"))
					critterEat();
				else if (i.equals("attack"))
					critterAttack();
				else if (i.equals("grow"))
					critterGrow(logs);
				else if (i.equals("bud"))
					critterBud();
				else if (i.equals("mate"))
					critterAttempToMate();
			}
		}
		return resultList;
	}
	
	/**
	 * Execute the update of critter's memory
	 * Check: mem[e1] is an assignable memory, e2 is within the allow boundary
	 * @param e1
	 * @param e2
	 */
	public void critterUpdate(int e1, int e2) {
		if (e1 < 7 || e1 >= c.getMem(IDX.MEMLEN))
			return;
		if (e1 == 7 && (e2 > 99 || e2 < 0)) 
			return;
		c.setMem(e1, e2);
	}
	
	/**
	 * The critter in the mediator wait to absorb solar energy
	 * 
	 * Formula: increase energy = SOLAR_FLUX * SIZE 
	 */
	public void critterWait() {
		c.increaseEnergy(Constant.SOLAR_FLUX * c.getMem(IDX.SIZE));
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
		c.increaseEnergy(-Constant.MOVE_COST * c.getMem(IDX.SIZE));
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		Position lastPos = c.getPosition();
		int dir = c.getDir();
		Position nextPos;
		if (forward) 
			nextPos = lastPos.getNextStep(dir);
		else
			nextPos = lastPos.getNextStep((3 + dir) % 6);
		Element e = w.getElemAtPosition(nextPos);
		// if the nextPos is empty, move the critter forward
		if (e == null) {
			// don't use delete critter
			w.removeElemAtPosition(lastPos);
			w.setElemAtPosition(c, nextPos);
		}
	}
	
	
	/**
	 * The critter rotate 60 degrees right or left. This takes the energy 
	 * equals to the critter's size
	 * @param direction true denotes turn left, 
	 *                  false denotes turn right
	 * @param hexToUpdate - a list of updates need to be executed
	 *                      in GUI world
	 */
	public void critterTurn(boolean left, Vector<Log> logs) {
		c.increaseEnergy(-c.getMem(IDX.SIZE));
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		c.Turn(left);
		int current = logs.size()-1;
		Log logTmp = logs.get(current);
		logTmp.updates.put(c.getPosition(), c);
	}

	
	/**
	 * The critter may eat some of the food that might be available on the 
	 * hex ahead of it, gaining the same amount of energy as the food it 
	 * consumes. When there is more food available on the hex than the 
	 * critter can absorb, the remaining food is left on the hex.
	 */
	private void critterEat() {
		c.increaseEnergy(-c.getMem(IDX.SIZE));
		// the critter may die
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		Element e = getElementAhead(c, w, 1);
		if (e == null)
			return;
		if (e.getType().equals("FOOD")) {
			int foodEnergy = ((Food) e).getAmount();
			int currentEnergy = c.getMem(IDX.ENERGY);
			int maxEnergy = c.maxEnergy();
			int amountCanToEat = maxEnergy - currentEnergy;
			// if can't eat all the food
			if (foodEnergy > amountCanToEat) {
				c.setMem(IDX.ENERGY, maxEnergy);
				((Food) e).setAmount(foodEnergy - amountCanToEat);
				Log logTmp = w.logs.get(w.logs.size()-1);
				logTmp.updates.put(e.getPosition(), e);
			}
			else {
				c.setMem(IDX.ENERGY, foodEnergy + currentEnergy);
				Position foodPos = 
						c.getPosition().getRelativePos(1, c.getDir());
				w.removeElemAtPosition(foodPos);
			}
		}
	}
	
	/**
	 * A critter may convert some of its own energy into food added to the
	 * hex in front of it, if that hex is either empty or already contains 
	 * some food.
	 * 
	 * The critter still lose energy if the hex in front is occupied
	 */
	public void critterServe(int energyToServe) {
		c.increaseEnergy(-c.getMem(IDX.SIZE));
		// the critter may die
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		int currentEnergy = c.getMem(IDX.ENERGY);
		if (energyToServe > currentEnergy)
			energyToServe = currentEnergy;
		c.setMem(IDX.ENERGY, currentEnergy - energyToServe);
		Element e = getElementAhead(c, w, 1);
		if (e == null) {
			Food newFood = new Food(energyToServe);
			w.setElemAtPosition(newFood, 
					c.getPosition().getRelativePos(1, c.getDir()));
		}
		else if(e.getType().equals("FOOD")) {
			int foodEnergy = ((Food) e).getAmount();
			((Food) e).setAmount(foodEnergy + energyToServe);
		}
		// the critter may die
		if (!c.stillAlive()) 
			handleCritterDeath(c, w);
	}
	
	
	/**
	 * It may attack a critter directly in front of it. The attack removes an 
	 * amount of energy from the attacked critter that is determined by the 
	 * size and offensive ability of the attacker and the defensive ability 
	 * of the victim.
	 */
	public void critterAttack() {
		c.increaseEnergy(-Constant.ATTACK_COST * c.getMem(IDX.SIZE));
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		Element e = getElementAhead(c, w, 1);
		if (e == null || !e.getType().equals("CRITTER"))
			return;
		Critter victim = (Critter) e;
		int damage = (int) (Constant.BASE_DAMAGE * c.getMem(IDX.SIZE) *
				Formula.logistic(Constant.DAMAGE_INC * 
					(c.getMem(IDX.SIZE) * c.getMem(IDX.OFFENSE)
					- victim.getMem(IDX.SIZE) * victim.getMem(IDX.DEFENSE))));
		victim.setMem(IDX.ENERGY, victim.getMem(IDX.ENERGY) - damage);
		if (!victim.stillAlive())
			this.handleCritterDeath(victim, w);
	}
	
	/**
	 * The critter may tag the critter in front of it, e.g., 
	 * to mark that critter as an enemy or a friend
	 * 
	 * Require: {@code v} is a valid tag value: 0 <= v <= 99
	 */
	public void critterTag(int v) {
		c.increaseEnergy(-c.getMem(IDX.SIZE));
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		Element e = getElementAhead(c, w, 1);
		if (e == null || !e.getType().equals("CRITTER"))
			return;
		Critter receiver = (Critter) e;
		receiver.setMem(IDX.TAG, v);
	}
	
	/**
	 * A critter may use energy to increase its size by one unit.
	 */
	public void critterGrow(Vector<Log> logs) {
		c.increaseEnergy(-Constant.GROW_COST * c.getMem(IDX.SIZE)
				* c.getComplexity());
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		c.setMem(IDX.SIZE, c.getMem(IDX.SIZE) + 1);
		int current = logs.size()-1;
		Log logTmp = logs.get(current);
		logTmp.updates.put(c.getPosition(), c);
	}
	
	/**
	 * A critter may use a large amount of its energy to produce a new, 
	 * smaller critter behind it with the same genome (possibly with some 
	 * random mutations).
	 */
	public void critterBud() {
		c.increaseEnergy(-Constant.BUD_COST * c.getComplexity());
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		// the position after the critter has been occupied
		if (getElementAhead(c, w, -1) != null)
			return;
		int[] mem = new int[c.getMem(0)];
		mem[IDX.MEMLEN] = c.getMem(IDX.MEMLEN);
		mem[IDX.DEFENSE] = c.getMem(IDX.DEFENSE);
		mem[IDX.OFFENSE] = c.getMem(IDX.OFFENSE);
		mem[3] = 1;
		mem[IDX.ENERGY] = Constant.INITIAL_ENERGY;
		mem[IDX.PASS] = 1;
		mem[IDX.TAG] = 0;
		mem[IDX.POSTURE] = 0;
		for (int i = 8; i < mem.length; ++i) 
			mem[i] = 0;
		String name = "Child of " + c.getName();
		ProgramImpl pro = c.getProgram();
		// with 1 / CHANCE_OF_MUTATE possibility to mutate it rules
		if (game.utils.RandomGen.randomNumber(Constant.CHANCE_OF_MUTATE) == 0)
			pro.mutate();
		// the critter created from bug has the same session id as its parent
		Critter newCritter = new Critter(mem, name, pro, 
				w.critterIDCount++, c.session_id);
		newCritter.setDir(game.utils.RandomGen.randomNumber(6));
		Position pos = c.getPosition().getRelativePos(-1, c.getDir());
		w.setElemAtPosition(newCritter, pos);
		resultList.toInsert.add(newCritter);
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
		c.increaseEnergy(-c.getMem(IDX.SIZE));
		c.setWantToMate(true);
		if (!c.stillAlive()) {
			handleCritterDeath(c, w);
			return;
		}
		Element e = getElementAhead(c, w, 1);
		if (e == null || !e.getType().equals("CRITTER"))
			return;
		Critter cr = (Critter) e;
		// if the critter it is facing at want to mate
		// and the critter is also facing at it, mate
		if (cr.getWantToMate() && c.getDir() == cr.getInvDir()) 
			critterMate(c, cr);
	}
	
	/**
	 * Two Critter mate
	 * @param first
	 * @param second
	 * @throws SyntaxError 
	 */
	public void critterMate(Critter first, Critter second) {
		// cost more energy than attempting mate if the mate would success
		int energyBalanceFirst = first.getMem(IDX.SIZE) - 
				Constant.MATE_COST * first.getComplexity();
		int energyBalanceSecond = second.getMem(IDX.SIZE) - 
				Constant.MATE_COST * second.getComplexity();
		
		// mate not succeed because one of the critter would die
		if (first.getMem(IDX.ENERGY) <= -energyBalanceFirst ||
				second.getMem(IDX.ENERGY) <= -energyBalanceSecond)
			return;
			
		Position posToSet;
		Position posAfterFirst = first.getPosition().
							getRelativePos(1, first.getInvDir());
		Position posAfterSecond = second.getPosition().
							getRelativePos(1, second.getInvDir());
		// mate not succeed mate because both position 
		// after the critters are occupied
		if (w.getElemAtPosition(posAfterFirst) != null && 
			w.getElemAtPosition(posAfterSecond) != null)
			return;
		
		// get the position to place the new critter
		else if (w.getElemAtPosition(posAfterFirst) != null)
			posToSet = posAfterSecond;
		else if (w.getElemAtPosition(posAfterSecond) != null)
			posToSet = posAfterFirst;
		else {
			if (game.utils.RandomGen.randomNumber(2) == 0)
				posToSet = posAfterFirst;
			else
				posToSet = posAfterSecond;
		}
		
		// this mate will success, reduce the energy
		first.setMem(IDX.ENERGY, first.getMem(IDX.ENERGY) + energyBalanceFirst);
		second.setMem(IDX.ENERGY, second.getMem(IDX.ENERGY) + energyBalanceSecond);
		
		
		// assume the new critter's memory length is inherit randomly chosen 
		// from father and mother
		int[] mem = new int[randomPickMem(first, second, 0)];
		mem[IDX.MEMLEN] = mem.length;
		mem[IDX.DEFENSE] = randomPickMem(first, second, IDX.DEFENSE);
		mem[IDX.OFFENSE] = randomPickMem(first, second, IDX.OFFENSE);
		mem[IDX.SIZE] = 1;
		mem[IDX.ENERGY] = Constant.INITIAL_ENERGY;
		mem[IDX.PASS] = 1;
		mem[IDX.TAG] = 0;
		mem[IDX.POSTURE] = 0;
		for (int i = 8; i < mem.length; ++i) 
			mem[i] = 0;
		String name = "Child of " + first.getName() + 
				" and " + second.getName();
		ArrayList<Rule> firstRules = first.getProgram().getChildren();
		ArrayList<Rule> secondRules = second.getProgram().getChildren();
		int ruleSize;
		if (game.utils.RandomGen.randomNumber(2) == 0)
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
		// critter that mate doesn't belong to any player, 
		// it has session id of 0
		Critter newCritter = new Critter(mem, name, pro, 
				w.critterIDCount++, 0);
		newCritter.setDir(game.utils.RandomGen.randomNumber(6));
		w.setElemAtPosition(newCritter, posToSet);
		resultList.toInsert.add(newCritter);
	}
	
	/**
	 * Randomly pick the rule with index {@code index} from rule list 
	 * {@code r1} or rule list {@code r2}
	 * 
	 * @param r1
	 * @param r2
	 * @param index
	 * @return 
	 */
	public String randomPickRule(ArrayList<Rule> r1, ArrayList<Rule> r2, 
			int index) {
		if (r1.size() <= index)
			return r2.get(index).toString();
		else if (r2.size() <= index)
			return r1.get(index).toString();
		if (game.utils.RandomGen.randomNumber(2) == 0)
			return r1.get(index).toString();
		return r2.get(index).toString();
	}
	
	/**
	 * Random pick a memory with index {@code index} from critter {@code first}
	 * and critter {@code second}
	 * 
	 * @param first
	 * @param second
	 * @param index
	 * @return
	 */
	public int randomPickMem(Critter first, Critter second, int index) {
		if (game.utils.RandomGen.randomNumber(2) == 0)
			return first.getMem(index);
		else
			return second.getMem(index);
	}
	
	/**
	 * Turn dead critter into food, delete it from the world 
	 * add it to the list of critter need to be deleted,
	 * these dead critters will be removed from the list that 
	 * record the order of critters to take action after this turn
	 * of the world
	 */
	public void handleCritterDeath(Critter critter, World world) {
		Food food = new Food(critter.getMem(IDX.SIZE) * 
				Constant.FOOD_PER_SIZE);
		Position pos = critter.getPosition();
		resultList.toDelete.add(critter);
		world.setElemAtPosition(food, pos);
	}
	
	/**
	 * Get the element {@code val} steps in front of the critter
	 * @param val step size
	 * @return the element
	 */
	public Element getElementAhead(Critter critter, World world, int val) {
		Position pos = critter.getPosition();
		pos = pos.getRelativePos(val, critter.getDir());
		Element e = world.getElemAtPosition(pos);
		return e; 
	}
}
