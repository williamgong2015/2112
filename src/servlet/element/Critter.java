package servlet.element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import api.JsonClasses.*;
import game.constant.Constant;
import game.constant.IDX;
import game.exceptions.SyntaxError;
import game.utils.RandomGen;
import servlet.ast.ProgramImpl;
import servlet.ast.Rule;
import servlet.parser.ParserImpl;
import servlet.parser.Tokenizer;
import servlet.world.Position;
import servlet.world.World;

/**
 * A critter object in the world, store the properties of the critter
 */
public class Critter extends Element {

	// require: 0 <= mem[5] <= Constant.MAX_PASS
	private int[] mem;
	private int orientation;
	private String name;
	private ProgramImpl pro;
	// the last rule being executed
	private int lastRuleIndex;
	private boolean wantToMate = false;
	// if still -1, it hasn't been initialized
	private int complexity = -1;
	public final int ID;
	public final int session_id;

	/**
	 * Create a new Critter with the given memory {@code mem}, 
	 * name of the critter {@code name}, and the AST program
	 * Randomly set the orientation of the critter
	 * @param len
	 * @param m
	 * @param name
	 * @param pro
	 */
	public Critter(int[] mem, String name, ProgramImpl pro, int id,
			int session_id) {
		super(Element.CRITTER);
		this.mem = mem;
		this.name = name;
		this.pro = pro;
		this.orientation = game.utils.RandomGen.randomNumber(Constant.ORI_RANGE);
		this.ID = id;
		setComplexity();
		this.session_id = session_id;
	}

	/**
	 * Create a new Critter object by reading a critter file, 
	 * randomly set the {@code orientation} of the critter 
	 * @param file
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public Critter(File file, int id, int session_id) throws IOException, 
	SyntaxError {
		super(Element.CRITTER);
		FileReader f = new FileReader(file);
		BufferedReader br = new BufferedReader(f);
		String n = br.readLine();
		int i = 0;
		for(;n.charAt(i) != ':';i++);
		i++;
		name = n.substring(i).trim();
		String s = br.readLine();
		i = 0;
		while(s.charAt(i) > '9' || s.charAt(i) < '0')
			i++;
		int temp = Integer.parseInt(s.substring(i).trim());
		if(temp < Constant.MIN_MEMORY)
			temp = Constant.MIN_MEMORY;
		mem = new int[temp];
		mem[IDX.MEMLEN] = temp; // memsize
		for(int j = 1;j < 5;j++) {
			s = br.readLine();
			i = 0;
			while(s.charAt(i) > '9' || s.charAt(i) < '0')
				i++;
			temp = Integer.parseInt(s.substring(i).trim());
			mem[j] = temp;
		}
		mem[IDX.PASS] = Constant.INIT_PASS; // pass = 1
		s = br.readLine();
		i = 0;
		while(s.charAt(i) > '9' || s.charAt(i) < '0')
			i++;
		temp = Integer.parseInt(s.substring(i).trim());
		mem[IDX.POSTURE] = temp;
		Tokenizer t = new Tokenizer(br);
		pro = ParserImpl.parseProgram(t);
		setComplexity();
		orientation = game.utils.RandomGen.randomNumber(Constant.ORI_RANGE);
		ID = id;
		this.session_id = session_id;
		f.close();
	}

	/**
	 * Create a new Critter object by reading a critter file, 
	 * randomly set the {@code orientation} of the critter 
	 * @param file
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public Critter(String file, int id, int session_id) throws IOException, 
	SyntaxError {
		this(new File(file), id, session_id);
	}

	public Critter(State c, int session_id) throws SyntaxError {
		super(Element.CRITTER);
		this.ID = c.id;
		this.session_id = session_id;
		name = c.species_id;
		setDir(c.direction);
		this.setPosition(new Position(c.col, c.row));
		this.mem = new int[c.mem.length];
		for (int i = 0; i < c.mem.length; ++i)
			this.mem[i] = c.mem[i];
		if(c.program == null) {
			this.pro = null;
		} else {
			StringReader s = new StringReader(c.program);
			Tokenizer t = new Tokenizer(s);
			pro = ParserImpl.parseProgram(t);
		}
	}

	public Critter(CreateCritter c, int id, int session_id) 
			throws SyntaxError {
		super(Element.CRITTER);
		this.mem = new int[c.mem.length];
		for (int i = 0; i < c.mem.length; ++i)
			this.mem[i] = c.mem[i];
		StringReader s = new StringReader(c.program);
		Tokenizer t = new Tokenizer(s);
		pro = ParserImpl.parseProgram(t);
		name = c.species_id;
		ID = id;
		this.session_id = session_id;
	}

	public Critter(CreateRandomPositionCritter c, String name, int id,
			int session_id) throws SyntaxError {
		super(Element.CRITTER);
		this.mem = new int[c.mem.length];
		for (int i = 0; i < c.mem.length; ++i)
			this.mem[i] = c.mem[i];
		StringReader s = new StringReader(c.program);
		Tokenizer t = new Tokenizer(s);
		pro = ParserImpl.parseProgram(t);
		this.name = name;
		ID = id;
		this.session_id = session_id;
	}

	/**
	 * Load a critter file, insert {@code n} number of critter created with 
	 * the critter file {@code filename} into the world {@code world}
	 * @param world
	 * @param filename
	 * @param n
	 * @param session_id - id of the user who load critter into the world
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public static void loadCrittersIntoWorld(World world, File filename, 
			int n, int session_id) throws IOException, SyntaxError {
		// check there are enough slot to put the critter 
		if (n > world.availableSlot())
			n = world.availableSlot();
		for(int i = 0;i < n;) {
			Critter c = new Critter(filename, world.critterIDCount++, 
					session_id);
			int a = RandomGen.randomNumber(world.getRow());
			int b = RandomGen.randomNumber(world.getColumn());
			Position pos = new Position(b, a);
			if(world.checkPosition(pos) &&
					world.getElemAtPosition(pos) == null) {
				world.setElemAtPosition(c, pos);
				world.addCritterToList(c);
				i++;
			}
		}
	}

	/**
	 * Load a critter file, insert {@code n} number of critter created with 
	 * the critter file {@code filename} into the world {@code world}
	 * @param world
	 * @param filename
	 * @param n
	 * @param session_id - user who load critter into the world
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public static void
	loadCrittersIntoWorld(World world, String filename, 
			int n, int session_id) throws IOException, SyntaxError {
		loadCrittersIntoWorld(world, new File(filename), n, session_id);
	}

	/**
	 * Insert a critter to a specified location into the world
	 * @param world
	 * @param filename
	 * @param column
	 * @param row
	 * @param session_id - id of user who insert the critter
	 * @return true if the insertion succeed
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public static void
		insertCritterIntoWorld(World world, File filename, int column, 
			int row, int session_id) throws IOException, SyntaxError {
		Critter c = new Critter(filename, world.critterIDCount++, session_id);
		Position pos = new Position(column, row);
		if(world.checkPosition(pos) &&
				world.getElemAtPosition(pos) == null) {
			world.setElemAtPosition(c, pos);
			world.addCritterToList(c);
		}
	}


	/**
	 * set the specified memory {@code mem[index]} to
	 * specified value {@code val}, 
	 * some memory are immutable and couldn't be set
	 */
	public void setMem(int index, int val) {
		if (index >= mem[IDX.MEMLEN])
			return;
		if(index >= 0 && index <= 2)
			return;
		if(index == 7 || index == 6) {
			if(val < 0 || val > 99)
				return;
		}
		mem[index] = val;
	}

	/**
	 * Set the complexity when creating the critter
	 * the complexity should be >= 0
	 */
	private void setComplexity() {
		complexity = pro.getChildren().size() * Constant.RULE_COST 
				+ (mem[IDX.DEFENSE] + mem[IDX.OFFENSE]) * Constant.ABILITY_COST;
	}

	/**
	 * Get the calculated complexity of the critter
	 * @return
	 */
	public int getComplexity() {
		if (complexity == -1)
			setComplexity();
		return complexity;
	}

	/**
	 * @return the size of the critter
	 */
	public int getSize() {
		return mem[IDX.SIZE];
	}

	public void setWantToMate(boolean wantToMate) {
		this.wantToMate = wantToMate;
	}

	public boolean getWantToMate() {
		return this.wantToMate;
	}

	/** 
	 * @return the specified memory{@code mem[index]}
	 */
	public int getMem(int index) {
		if(index >= mem[IDX.MEMLEN] || index < 0)
			return 0;
		return mem[index];
	}


	public ProgramImpl getProgram() {
		return this.pro;
	}

	/**
	 * Effect: set the last rule being executed
	 * @param r the last rule being executed
	 */
	public void setLastRuleExe(int r) {
		lastRuleIndex = r;
	}

	public Rule getLastRuleExe() {
		return pro.getChildren().get(lastRuleIndex);
	}

	/**
	 * @return the index of last rule being executed
	 */
	public int getLastRuleIndex() {
		return lastRuleIndex;
	}

	/**
	 * @return a position which is at direction
	 * {@code index} of the critter
	 */
	public Position nearby(int index) {
		int temp = (index + orientation) % 6;
		if(temp < 0)
			temp += 6;
		Position p = this.getPosition().getNextStep(temp);
		return p;
	}

	/**
	 * @return the direction of the critter
	 */
	public int getDir() {
		return orientation;
	}

	/**
	 * @return inverse direction of the critter
	 */
	public int getInvDir() {
		return (3 + orientation) % 6;
	}

	/**
	 * @return the calculated appearance of the critter
	 */
	public int appearance() {
		return mem[IDX.SIZE] * 100000 + mem[IDX.TAG] * 1000 + 
				mem[IDX.POSTURE] * 10 + orientation;
	}

	/**
	 * if left is true,the critter will turn left;
	 * otherwise it will turn right
	 */
	public void Turn(boolean left) {
		if(left == true)
			orientation--;
		else orientation++;
		if(orientation > 5)
			orientation %= 6;
		if(orientation < 0)
			orientation += 6;
	}

	/**
	 * check if the critter has used up its energy
	 */
	public boolean stillAlive() {
		return mem[IDX.ENERGY] > 0;
	}

	/**
	 * set the direction of the critter
	 */
	public void setDir(int dir) {
		orientation = dir;
	}

	/**
	 * return the name of the critter
	 */
	public String getName() {
		return name;
	}

	/**
	 * return the maximum energy that this critter could have
	 */
	public int maxEnergy() {
		return mem[IDX.SIZE] * Constant.ENERGY_PER_SIZE;
	}

	/**
	 * Increase a certain amount of energy
	 * @param amount the amount of energy to increase, may be negative
	 */
	public void increaseEnergy(int amount) {
		mem[IDX.ENERGY] += amount;
	}

	/**
	 * return a description of the critter
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder ();
		//		sb.append("at position: " + getPosition()); // check if position update
		sb.append("SPECIES:" + name + "\n");
		sb.append("MEMSIZE:" + mem[IDX.MEMLEN] + "\n");
		sb.append("DEFENSE:" + mem[IDX.DEFENSE] + "\n");
		sb.append("OFFENSE:" + mem[IDX.OFFENSE] + "\n");
		sb.append("SIZE:" + mem[IDX.SIZE] + "\n");
		sb.append("ENERGY:" + mem[IDX.ENERGY] + "\n");
		sb.append("PASS:" + mem[IDX.PASS] + "\n");
		sb.append("TAG:" + mem[IDX.TAG] + "\n");
		sb.append("POSTURE:" + mem[IDX.POSTURE] + "\n");
		sb.append(this.pro.toString() + "Last Rule: \n");
		if (getLastRuleExe() != null)
			sb.append(getLastRuleExe());
		else
			sb.append("none of this critter's "
					+ "rule has been executed");
		return sb.toString();
	}

	public int[] getMeMArray() {
		return mem;
	}

	/**
	 * Remove a critter from the world and update the GUI
	 * @param world
	 * @param pos
	 * @param session_id
	 */
	public static void deleteCritterFromWorld(World world, 
			Position pos, int session_id) {
		Element e = world.getElemAtPosition(pos);
		world.removeElemAtPosition(pos);
		world.order.remove(e);
	}
}
