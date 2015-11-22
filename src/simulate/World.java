package simulate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import constant.Constant;
import constant.IDX;
import execute.Executor;
import execute.ResultList;
import gui.HexToUpdate;
import gui.HexToUpdate.HEXType;
import interpret.InterpreterImpl;
import interpret.Outcome;
import json.JsonClasses;
import util.RandomGen;

/**
 * The critter world
 * Placing all the elements in the world at proper position, provide methods
 * to set and get these elements
 * 
 * Class invariant:
 * 0. row > 0, column > 0, turns >= 0
 * 1. All the elements in the world are within the boundary of the world
 * 2. All the elements in the world has a position property that stores the 
 *    its position in world
 * 3. No two elements can be at the same position (based on 2, also have no
 *    two elements can have the same position property)
 */
public class World {

	// how many turns has passed in the world
	public int turns;
	
	// the scale of the world
	private int row;
	private int column;
	private String name;
	private int size;
	public int version_number;
	public int rate;
	
	// maps position to element in the world
	private Hashtable<Position, Element> hexes;
	
	// order of critters in the world to take actions
	public ArrayList<Critter> order;
	
	// record the change of state in each turn
	private HashMap<Position, HexToUpdate> hexToUpdate;
	
	/**
	 * Initialize a world
	 * Check: {@code r} > 0, {@code c} > 0, 
	 * 
	 * @param r row
	 * @param c column
	 * @param n name of the world
	 */
	public World(int c, int r, String n) {
		if (!Constant.hasBeenInitialized()) {
	    	try {
	    		Constant.init();
	    	} catch (Exception e) {
	    		System.out.println("can't find constant.txt,"
	    				+ "the constant has not been initialized");
	    	}
		}
		if (r <= 0 || c <= 0) {
			System.out.println("the world has an unproper size");
			row = 1;
			column = 1;
		}
		else {
			row = r;
			column = c;
		}
		setSize();
		name = n;
		turns = 0;
		hexes = new Hashtable<Position, Element>();
		order = new ArrayList<Critter>();
		hexToUpdate = new HashMap<>();
	}
	
	/**
	 * Initialize a default world using constants 
	 * and put rocks in random position
	 * 
	 * Check: all the constants in Constant class has been initailized
	 */
	public World() {
		if (!Constant.hasBeenInitialized()) {
	    	try {
	    		Constant.init();
	    	} catch (Exception e) {
	    		System.out.println("can't find constant.txt,"
	    				+ "the constant has not been initialized");
	    	}
		}
		row = Constant.ROWS;
		column = Constant.COLUMNS; 
		setSize();
		hexes = new Hashtable<Position, Element>();
		turns = 0;
		name = "Default World";
		order = new ArrayList<Critter>();
		hexToUpdate = new HashMap<>();
		// initialize some rocks into the world
		for(int i = 0;i < Math.abs(RandomGen.randomNumber(row * column / 10)); 
				i++) {
			int a = Math.abs(RandomGen.randomNumber(row));
			int b = Math.abs(RandomGen.randomNumber(column));
			Position pos = new Position(b,a);
			if(checkPosition(pos) && hexes.get(pos) == null) {
				hexes.put(pos, new Rock());
				hexToUpdate.put(pos, new HexToUpdate(HEXType.ROCK, pos, 
						0, 0, 0));
			}
		}
	}
	
	/**
	 * Create and return a world with a world file
	 */
	public static World loadWorld(File filename) {
		World world;
    	try{
    		FileReader r = new FileReader(filename);
			BufferedReader br = new BufferedReader(r);
    		String s = br.readLine();
    		String name = s.substring(5);
    		s = br.readLine();
    		String[] temp = s.split(" ");
    		int column = Integer.parseInt(temp[1]);
    		int row = Integer.parseInt(temp[2]);
    		world = new World(column,row,name);
    		world.hexToUpdate = new HashMap<>();
    		while((s = br.readLine()) != null) {
    			if(s.startsWith("//"))
    				continue;
    			temp = s.split(" ");
    			if(temp.length == 0)
    				continue;
    			Position pos;
    			if(temp[0].equals("rock")) {
    				pos = new Position(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
    				if (world.checkPosition(pos) && 
    						world.getElemAtPosition(pos) == null) {
    					world.setElemAtPosition(new Rock(), pos);
    				}
    			}
    			if(temp[0].equals("food")) {
    				int amount = Integer.parseInt(temp[3]);
    				pos = new Position(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
    				if (world.checkPosition(pos) && 
    						world.getElemAtPosition(pos) == null) {
	    				world.setElemAtPosition(new Food(amount), pos);
    				}
    			}
    			if(temp[0].equals("critter")) {
    				String file = temp[1];
    				pos = new Position(Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
    				int dir = Integer.parseInt(temp[4]);
    				Critter c = new Critter(file);
    				c.setDir(dir);
    				if (world.checkPosition(pos) && 
    						world.getElemAtPosition(pos) == null) {
	    				world.setElemAtPosition(c, pos);
	    				world.addCritterToList(c);
    				}
    			}
    		}
    		r.close();
    		return world;
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.err.println("No such file");
    		return null;
    	}
    	
    }
	
	public static World loadWorld(String filename) {
		return loadWorld(new File(filename));
	}
	
	/**
	 * add a critter to the arraylist
	 */
	public void addCritterToList(Critter c) {
		order.add(c);
	}
	
	/**
	 * @return all the update to hex should be enforced after this turn
	 */
	public HashMap<Position, HexToUpdate> getHexToUpdate() {
		HashMap<Position, HexToUpdate> tmp = hexToUpdate;
		hexToUpdate = new HashMap<>();
		return tmp;
	}
	
	
	/**
	 * A new turn of the world 
	 * 
	 */
	synchronized public void lapse() {
		turns++;
		ArrayList<Critter> toDelete = new ArrayList<>();
		// update every critter until it execute a action or has being 
		// updated for 999 PASS (for the second one, take a wait action)
		int i = 0;
		while (i < order.size()) {
			Critter c = order.get(i++);
			InterpreterImpl interpret = new InterpreterImpl(this,c);
			Executor executor = new Executor(this, c);
			boolean hasAction = false;
			c.setWantToMate(false);
			// for each critter, while it hasn't act and hasn't come to MAXPASS
			// keep interpret it and execute its commands
			while (c.getMem(IDX.PASS) <= Constant.MAX_PASS && 
					hasAction == false) {
				Outcome outcomes = interpret.interpret(c.getProgram());
				
				c.setMem(IDX.PASS, c.getMem(IDX.PASS) + 1);
				if (outcomes.hasAction())
					hasAction = true;
				
				ResultList tmp = executor.execute(outcomes, hexToUpdate);
				toDelete.addAll(tmp.toDelete);
				// insert the new born critters
				for (Critter critter : tmp.toInsert)
					order.add(critter);
			}
			c.setMem(IDX.PASS, Constant.INIT_PASS);
			// if after the loop, the critter still does not take any action
			if (!hasAction) 
				executor.execute(new Outcome("wait"), hexToUpdate);
			
		}
		
		// remove the critter need to be delete and insert the critter need 
		// to be inserted
		for (Critter critter : toDelete) {
			order.remove(critter);
		}
	}
	
	/**
	 * Check the position is within the world boundary
	 * @param position the position to check
	 * @return true if the position is within the boundary,
	 *         false otherwise
	 */
	public boolean checkPosition(Position position) {
		return Position.checkPosition(position, column, row);
	}
	
	/**
	 * Get the element at {@code pos} in the world if 
	 * the position is within the boundary of the world, otherwise
	 * return a rock
	 */
	public Element getElemAtPosition(Position pos) {
		if (checkPosition(pos))
			return hexes.get(pos);
		return new Rock();
	}
	
	/**
	 * Set element at {@code pos} 
	 * @param elem element to set
	 * @param pos
	 * @return {@code false} if the {@code pos} is out of the world boundary
	 *         {@code true} otherwise
	 */
	public boolean setElemAtPosition(Element elem, Position pos) {
		if (!checkPosition(pos))
			return false;
		if(hexes.get(pos) != null)
			removeElemAtPosition(pos);
		switch (elem.getType()) {
			case "CRITTER":
				Critter tmp = (Critter) elem;
				hexToUpdate.put(pos, new HexToUpdate(HEXType.CRITTER, pos, 
						tmp.getDir(), tmp.getSize(), tmp.getMem(IDX.POSTURE)));
				break;
			case "FOOD":
				hexToUpdate.put(pos, new HexToUpdate(HEXType.FOOD, pos, 
						0, 0, 0));
				break;
			case "ROCK":
				hexToUpdate.put(pos, new HexToUpdate(HEXType.ROCK, pos, 
						0, 0, 0));
				break;
			default:
				System.out.println("can't resolve the type for update");
				break;
		}
		elem.setPosition(pos);
		hexes.put(pos, elem);
		return true;
	}

	
	/**
	 * Remove the element at the {@code pos} in the world 
	 * this remove affect both the underlying critter program and the 
	 * GUI display of the world
	 * @param pos the position to check
	 * @return {@code false} if the position is out of the boundary, 
	 *         {@code false} if there is no element at the {@code pos}
	 *         {@code true} otherwise
	 *
	 */
	public boolean removeElemAtPosition(Position pos) {
		if (!checkPosition(pos))
			return false;
		if (!hexes.containsKey(pos))
			return false;
		hexToUpdate.put(pos, new HexToUpdate(HEXType.EMPTY, pos, 0, 0, 0));
		hexes.remove(pos);
		return true;
	}
	
	/**
	 * Delete a critter in the world ( remove it from {@code order} )
	 * @param pos
	 * @return
	 */
	public boolean removeCritter(Critter critter) {
		if (order.contains(critter)) {
			order.remove(critter);
			return true;
		}
		else 
			return false;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTurns() {
		return turns;
	}
	
	/**
	 * Effect: Print the world 
	 * 
	 * Notation: {@code r} row index
	 *           {@code c} column index
	 *           {@code h} horizontal index
	 *           {@code v} vertical index
	 * Formula:  h = 2r-c, v = c, 
	 *           r = (v+h+1)/2, c = v
	 *           
	 * @param printElement {@code true} when printing ASCII-art map
	 *                     {@code false} when printing coordinate
	 * @param indent       the indent being used
	 */
	public String printASCII(boolean printElement, String indent) {
		StringBuilder s = new StringBuilder();
		int h; int v;
		int horizonalBound = Position.getH(column, row);
		int verticalBound = Position.getV(column, row);
		for (h = horizonalBound-1; h >= 0; --h) {
			if (h % 2 == 1) {
				s.append(indent);
				v = 1;
			}
			else 
				v = 0;
			
			for (; v < verticalBound; v += 2) {
				if (printElement)
					s.append(
							enquery(Position.getC(v,h),Position.getR(v,h)));
				else 
					s.append("(" + Position.getC(v,h) + "," + 
							Position.getR(v,h) + ")");
				s.append(indent);
			}
			s.append("\n");
		}
		return s.toString();
	}
	
	/**
	 * Effect: Print an ASCII-art map of the world
	 */
	public String printASCIIMap() {
		String indent = " ";
		return printASCII(true, indent);
	}
	
	/**
	 * Effect: Print the coordinate representation of
	 * the ASCII-art map of the world
	 */
	public String printCoordinatesASCIIMap() {
		String indent = "      ";
		return printASCII(false, indent);
	}
	
	private String enquery(int c, int r) {
		Position pos = new Position(c,r);
		Element e = hexes.get(pos);
		if(e == null)
			return "-";
		if(e.getType().equals("FOOD"))
			return "F";
		if(e.getType().equals("CRITTER"))
			return "" + ((Critter)e).getDir();
		if(e.getType().equals("ROCK"))
			return "#";
		return null;
	}
	
	/**
	 * @return how many rows in the world
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * @return how many columns in the world
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * Print a description of the contents of 
	 * the hex at coordinate (column, row{@code c, r}). 
	 */
	public void hex(int r,int c) {
		Position pos = new Position(c,r);
		Element e = hexes.get(pos);
		if(e == null || e.getType().equals("ROCK"))
			return;
		if(e.getType().equals("FOOD"))
			System.out.println(((Food)e).getAmount());
		if(e.getType().equals("CRITTER")) {
			Critter temp = (Critter)e;
			System.out.println(temp.toString());
		}
	}
	
	/**
	 * Calculate and set the size of the world
	 */
	private void setSize() {
		int s = 0;
		int h; int v;
		int horizonalBound = Position.getH(column, row);
		int verticalBound = Position.getV(column, row);
		for (h = horizonalBound-1; h >= 0; --h) {
			if (h % 2 == 1) 
				v = 1;
			else 
				v = 0;
			for (; v < verticalBound; v += 2) {
				s++;
			}
		}
		size = s;
	}
	
	/**
	 * @return the position that critter exists
	 */
	public Position getPositionFromCritter(Critter c) {
		Set<Map.Entry<Position, Element>> e = hexes.entrySet();
		for(Map.Entry<Position, Element> m : e) {
			if(m.getValue() == c) {
				return m.getKey();
			}
		}
		return null;
	}
	
	/**
	 * Get the number of empty slot in this world
	 * @return
	 */
	public int availableSlot() {
		return size - hexes.size();
	}
	
	/**
	 * For GUI, return a string to inform user how many turn the world has
	 * step through and how many critter are alive in the world
	 * @return
	 */
	public String getWorldInfo() {
		return "The world has step " + turns + " turns.\n" 
				+ "There are " + order.size() + " critters living "
						+ "in this world.";
	}
	//TODO
	public JsonClasses.worldState getWorldState(int session_id) {
		JsonClasses.worldState s = new JsonClasses.worldState();
		s.col = column;
		s.current_timestep = turns;
		s.current_version_number = version_number;
		s.name = this.name;
		s.population = order.size();
		s.row = this.row;
		s.update_since       =0;
		s.rate               =0;
		s.state = new JsonClasses.States[hexes.size()];
		int index = 0;
		Set<Map.Entry<Position, Element>> set = hexes.entrySet();
		for(Map.Entry<Position, Element> m : set) {
			Element e = m.getValue();
			Position p = m.getKey();
			switch(e.getType()) {
			case "ROCK" :
				s.state[index++] = new JsonClasses.RockStates(p);
				break;
			case "FOOD" :
				s.state[index++] = new JsonClasses.FoodState(((Food)e).getAmount(), p);
				break;
			case "CRITTER" :
				s.state[index++] = new JsonClasses.CritterStates(p, ((Critter)e));
				break;
			}
		}
		return s;
	}
}