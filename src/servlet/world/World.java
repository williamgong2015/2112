package servlet.world;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import api.JsonClasses;
import api.JsonClasses.CreateRandomPositionCritter;
import api.JsonClasses.State;
import api.PositionInterpreter;
import game.constant.Constant;
import game.constant.IDX;
import game.exceptions.SyntaxError;
import game.utils.RandomGen;
import servlet.connection.Log;
import servlet.element.Critter;
import servlet.element.Element;
import servlet.element.Food;
import servlet.element.Nothing;
import servlet.element.Rock;
import servlet.executor.Executor;
import servlet.executor.ResultList;
import servlet.interpreter.InterpreterImpl;
import servlet.interpreter.Outcome;

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

	Timer timer = null;

	// how many turns has passed in the world
	public int turns;

	// the scale of the world
	private int row;
	private int column;
	private String name;
	private int size;
	public int version_number = 1; // be 1 after it has been constructed
	private int rate;  // how many steps per second the world is simulating
	public int critterIDCount = 0;

	// maps position to element in the world
	public Hashtable<Position, Element> hexes = new Hashtable<>();

	// order of critters in the world to take actions
	public ArrayList<Critter> order = new ArrayList<>();

	public Vector<Log> logs = new Vector<>();


	// - if the speed <= 30, each cycle lapse the world, draw the world, 
	//   so counterWorldLapse = counterWorldDraw
	// - if the speed > 30, each cycle lapse the world but 
	//   draw the world only when 30*counterWorldLapse/speed > counterWorldDraw
	//   and have counterWorldDraw++ after drawing the world

	// read writer lock for server thread safety
	private ReentrantReadWriteLock rwLock = 
			new ReentrantReadWriteLock();
	
	// change this path to correctly load critter files
	private final static String PATH = "/Users/yuxin/Documents/workspace/2112/";


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
		logs.add(new Log());
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
		turns = 0;
		name = "Default World";
		// create a new log when the world is created
		logs.add(new Log());
		// initialize some rocks into the world
		for(int i = 0;i < Math.abs(RandomGen.randomNumber(row * column / 10)); 
				i++) {
			int a = Math.abs(RandomGen.randomNumber(row));
			int b = Math.abs(RandomGen.randomNumber(column));
			Position pos = new Position(b,a);
			if(checkPosition(pos) && hexes.get(pos) == null) {
				Rock rock;
				rock = new Rock();
				hexes.put(pos, rock);
				Log logTmp = logs.get(logs.size()-1);
				logTmp.updates.put(pos, rock);
			}
		}
	}

	public static World loadWorld(File filename, int session_id)
			throws IOException {
		FileReader r = new FileReader(filename);
		BufferedReader br = new BufferedReader(r);
		World tmp = loadWorldHelper(br, session_id);
		r.close();
		return tmp;
	}

	/**
	 * Create and return a world with a world file
	 * @param session_id - id of user who load the world
	 * @throws Exception 
	 */
	public static World loadWorldHelper(BufferedReader br, int session_id) {
		World world;
		try {
			String s = br.readLine();
			String name = s.substring(5);
			s = br.readLine();
			// skip new line
			while (s.length() < 3)
				s = br.readLine();
			String[] temp = s.split(" ");
			int column = Integer.parseInt(temp[1]);
			int row = Integer.parseInt(temp[2]);
			world = new World(column,row,name);
			world.logs = new Vector<>();
			// create a new log when the world is created
			world.logs.add(new Log());
			while((s = br.readLine()) != null) {
				if(s.startsWith("//"))
					continue;
				temp = s.split(" ");
				if(temp.length == 0)
					continue;
				Position pos;
				if(temp[0].equals("rock")) {
					pos = new Position(Integer.parseInt(temp[1]),
							Integer.parseInt(temp[2]));
					if (world.checkPosition(pos) && 
							world.getElemAtPosition(pos) == null) {
						world.setElemAtPosition(new Rock(), pos);
					}
				}
				if(temp[0].equals("food")) {
					int amount = Integer.parseInt(temp[3]);
					pos = new Position(Integer.parseInt(temp[1]), 
							Integer.parseInt(temp[2]));
					if (world.checkPosition(pos) && 
							world.getElemAtPosition(pos) == null) {
						world.setElemAtPosition(new Food(amount), pos);
					}
				}
				if(temp[0].equals("critter")) {
					String file = PATH + temp[1];
					pos = new Position(Integer.parseInt(temp[2]), 
							Integer.parseInt(temp[3]));
					int dir = Integer.parseInt(temp[4]);
					Critter c = new Critter(file, 
							world.critterIDCount++, session_id);
					c.setDir(dir);
					if (world.checkPosition(pos) && 
							world.getElemAtPosition(pos) == null) {
						world.setElemAtPosition(c, pos);
						world.addCritterToList(c);
					}
				}
			}
			return world;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param filename
	 * @param session_id - id of user who load the world
	 * @return
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static World loadWorld(String filename, 
			int session_id) throws IOException {
		return loadWorld(new File(filename), session_id);
	}


	/**
	 * Change the simulation rate of the world and store it in 
	 * {@code rate}
	 * @param rate
	 */
	public void changeSimulationSpeed(int rate) {
		rwLock.writeLock().lock();
		try {
			this.rate = rate;
			if (rate <= 0) {
				if (timer != null)
					timer.cancel();
				timer = null;
				return;
			}
			System.out.println("Change rate");
			if (timer == null)
				timer = new Timer();
			timer.scheduleAtFixedRate(
					new TimerTask()
					{
						public void run()
						{
							lapse();
						}
					},
					0,      // run first occurrence immediately
					1000/rate);  // run every three seconds
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	public int getSimulaionRate() {
		rwLock.readLock().lock();
		try {
			return rate;
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * add a critter to the arraylist
	 */
	public void addCritterToList(Critter c) {
		rwLock.writeLock().lock();
		try {
			order.add(c);
		} finally {
			rwLock.writeLock().unlock();
		}
	}


	/**
	 * A new turn of the world 
	 * 
	 */
	public void lapse() {
		rwLock.writeLock().lock();
		try {
			turns++;
			version_number++;
			logs.add(new Log());
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

					ResultList tmp = executor.execute(outcomes, logs);
					toDelete.addAll(tmp.toDelete);
					// insert the new born critters
					for (Critter critter : tmp.toInsert)
						order.add(critter);
				}
				c.setMem(IDX.PASS, Constant.INIT_PASS);
				// if after the loop, the critter still does not take any action
				if (!hasAction) 
					executor.execute(new Outcome("wait"), logs);
				// need to send back the critter final state because
				// its memory and last rule got executed could be update.
				// however, if it has dead, a food would replace it
				if (c.getMem(IDX.ENERGY) > 0)
					logs.get(logs.size()-1).updates.put(c.getPosition(), c);
			}

			// remove the critter need to be delete and insert the critter need 
			// to be inserted (they had been marked dead in the world beforehand 
			// but not deleted from the arraylist)
			for (Critter critter : toDelete) {
				order.remove(critter);
				logs.get(logs.size()-1).deadCritterID.add(critter.ID);
			}
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	/**
	 * Check the position is within the world boundary
	 * @param position the position to check
	 * @return true if the position is within the boundary,
	 *         false otherwise
	 */
	public boolean checkPosition(Position position) {
		rwLock.readLock().lock();
		try {
			return Position.checkPosition(position, column, row);
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * Get the element at {@code pos} in the world if 
	 * the position is within the boundary of the world, otherwise
	 * return a rock
	 */
	public Element getElemAtPosition(Position pos) {
		rwLock.readLock().lock();
		try {
			if (checkPosition(pos))
				return hexes.get(pos);
			return new Rock();
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * Set element at {@code pos} 
	 * @param elem element to set
	 * @param pos
	 * @return {@code false} if the {@code pos} is out of the world boundary
	 *         {@code true} otherwise
	 */
	public boolean setElemAtPosition(Element elem, Position pos) {
		rwLock.writeLock().lock();
		try {
			if (!checkPosition(pos))
				return false;
			if(hexes.get(pos) != null)
				removeElemAtPosition(pos);
			Log logTmp;
			switch (elem.getType()) {
			case "CRITTER":
				logTmp = logs.get(logs.size()-1);
				logTmp.updates.put(pos, elem);
				break;
			case "FOOD":
				logTmp = logs.get(logs.size()-1);
				logTmp.updates.put(pos, elem);
				break;
			case "ROCK":
				logTmp = logs.get(logs.size()-1);
				logTmp.updates.put(pos, elem);
				break;
			default:
				System.out.println("can't resolve the type for update");
				break;
			}
			elem.setPosition(pos);
			hexes.put(pos, elem);
			return true;
		} finally {
			rwLock.writeLock().unlock();
		}
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
		rwLock.writeLock().lock();
		try {
			if (!checkPosition(pos))
				return false;
			if (!hexes.containsKey(pos))
				return false;
			Log logTmp = logs.get(logs.size()-1);
			logTmp.updates.put(pos, new Nothing());
			hexes.remove(pos);
			return true;
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	/**
	 * Delete a critter in the world ( remove it from {@code order} )
	 * @param pos
	 * @return
	 */
	public boolean removeCritter(Critter critter) {
		rwLock.writeLock().lock(); 
		try {
			if (order.contains(critter)) {
				order.remove(critter);
				return true;
			}
			else 
				return false;
		} finally {
			rwLock.writeLock().unlock();
		}
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
		int horizonalBound = PositionInterpreter.getX(column, row);
		int verticalBound = PositionInterpreter.getY(column, row);
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
							enquery(PositionInterpreter.getC(v,h),
									PositionInterpreter.getR(v,h)));
				else 
					s.append("(" + PositionInterpreter.getC(v,h) + "," + 
							PositionInterpreter.getR(v,h) + ")");
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
		if(e.getType().equals(Element.FOOD))
			return "F";
		if(e.getType().equals(Element.CRITTER))
			return "" + ((Critter)e).getDir();
		if(e.getType().equals(Element.ROCK))
			return "#";
		return null;
	}

	/**
	 * @return how many rows in the world
	 */
	public int getRow() {
		rwLock.readLock().lock();
		try {
			return row;
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * @return how many columns in the world
	 */
	public int getColumn() {
		rwLock.readLock().lock();
		try {
			return column;
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * Print a description of the contents of 
	 * the hex at coordinate (column, row{@code c, r}). 
	 */
	public void hex(int r,int c) {
		Position pos = new Position(c,r);
		Element e = hexes.get(pos);
		if(e == null || e.getType().equals(Element.ROCK))
			return;
		if(e.getType().equals(Element.FOOD))
			System.out.println(((Food)e).getAmount());
		if(e.getType().equals(Element.CRITTER)) {
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
		int horizonalBound = PositionInterpreter.getX(column, row);
		int verticalBound = PositionInterpreter.getY(column, row);
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
	 * Get the number of empty slot in this world
	 * @return
	 */
	public int availableSlot() {
		rwLock.readLock().lock();
		try {
			return size - hexes.size();
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * For GUI, return a string to inform user how many turn the world has
	 * step through and how many critter are alive in the world
	 * @return
	 */
	public String getWorldInfo() {
		rwLock.readLock().lock();
		try {
			return "The world has step " + turns + " turns.\n" 
					+ "There are " + order.size() + " critters living "
					+ "in this world.";
		} finally {
			rwLock.readLock().unlock();
		}
	}


	/**
	 * Get a Hashtable contains updates within the range since {@code 
	 * update_since}
	 * @param update_since
	 * @param from_col
	 * @param from_row
	 * @param to_col
	 * @param to_row
	 * @return
	 */
	public Hashtable<Position, Element> getUpdatesSinceMap(int update_since,
			int from_col, int from_row, int to_col, int to_row) {
		rwLock.readLock().lock();
		try {
			Hashtable<Position, Element> result = new Hashtable<>();
			for (int i = update_since; i < this.version_number; ++i) {
				Set<Map.Entry<Position, Element>> set = 
						logs.get(i).updates.entrySet(); 
				for(Map.Entry<Position, Element> m : set) {
					Element e = m.getValue();
					Position p = m.getKey();
					int x = PositionInterpreter.getX(p.getColumn(), p.getRow());
					int y = PositionInterpreter.getY(p.getColumn(), p.getRow());
					int from_x = PositionInterpreter.getX(from_col, from_row);
					int from_y = PositionInterpreter.getY(from_col, from_row);
					int to_x = PositionInterpreter.getX(to_col, to_row);
					int to_y = PositionInterpreter.getY(to_col, to_row);
					if (x <= to_x && x >= from_x && y <= to_y && y >= from_y) {
						result.put(p, e);
					}
				}
			}
			return result;
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * Get all the critter that died since {@code update_since}
	 * @param update_since
	 * @return
	 */
	public ArrayList<Integer> getDeadCritterIDSince(int update_since) {
		rwLock.readLock().lock();
		try {
			ArrayList<Integer> result = new ArrayList<>();
			for (int i = update_since; i< this.version_number; ++i) {
				for (Integer id : logs.get(i).deadCritterID)
					result.add(id);
			}
			return result;
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * Pack the world state info with {@code table}, which contains the 
	 * information of updates need to be packed
	 * @param session_id
	 * @param isAdmin
	 * @param table
	 * @return 
	 */
	public JsonClasses.WorldState getWorldState(int session_id, 
			boolean isAdmin, Hashtable<Position, Element> table,
			ArrayList<Integer> deadCritters) {
		rwLock.readLock().lock();
		try {
			JsonClasses.WorldState s = new JsonClasses.WorldState();
			s.cols = column;
			s.current_timestep = turns;
			s.current_version_number = version_number;
			s.name = this.name;
			s.population = order.size();
			s.rows = this.row;
			s.update_since = 0;
			s.rate = rate;
			s.state = new State[table.size()];
			s.dead_critters = new Integer[deadCritters.size()];
			for (int i = 0; i < deadCritters.size(); ++i) 
				s.dead_critters[i] = deadCritters.get(i);
			int index = 0;
			Set<Map.Entry<Position, Element>> set = table.entrySet();
			for(Map.Entry<Position, Element> m : set) {
				Element e = m.getValue();
				Position p = m.getKey();
				// for cleaning up the hex
				if (e == null) {
					State nothing = new State(p);
					nothing.setNothing();
					s.state[index++] = nothing;
					continue;
				}
				switch(e.getType()) {
				case Element.ROCK :
					State rock = new State(p);
					rock.setRock();
					s.state[index++] = rock;
					break;
				case Element.FOOD :
					State food = new State(p);
					food.setFood(((Food)e).getAmount());
					s.state[index++] = food;
					break;
				case Element.CRITTER :
					Critter c = (Critter)e;
					State critter = new State(c.getPosition());
					critter.setCriiter(c, c.session_id == session_id || isAdmin == true);
					s.state[index++] = critter;
					break;
				case Element.NOTHING :
					State nothing = new State(p);
					nothing.setNothing();
					s.state[index++] = nothing;
					break;
				}
			}
			return s;
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * add a critter to the world
	 */
	public void addCritter(Critter c, Position pos) {
		rwLock.writeLock().lock();
		try {
			if (this.getElemAtPosition(pos) != null)
				return;
			this.setElemAtPosition(c, pos);
			addCritterToList(c);
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	/**
	 * Set {@code c.num} of critters at random position at the world
	 * @param c
	 * @param session_id - the id of user who set the critter
	 * @return an array of critter {@code ID} just created
	 */
	public ArrayList<Integer> 
	setCritterAtRandomPosition(CreateRandomPositionCritter c, 
			String species_id, int session_id) {
		rwLock.writeLock().lock();
		try {
			int num = c.num;
			ArrayList<Integer> idTmp = new ArrayList<>();
			for(int i = 0; i < num; ++i) {
				try {
					String critterName = species_id;
					idTmp.add(critterIDCount);
					Critter critter = new Critter(c, critterName,
							critterIDCount++, session_id);
					int a = Math.abs(RandomGen.randomNumber(row));
					int b = Math.abs(RandomGen.randomNumber(column));
					Position pos = new Position(b,a);
					while (!checkPosition(pos) || hexes.get(pos) != null) {
						a = Math.abs(RandomGen.randomNumber(row));
						b = Math.abs(RandomGen.randomNumber(column));
						pos = new Position(b,a);
					}
					critter.setPosition(pos);
					this.setElemAtPosition(critter, pos);
					addCritterToList(critter);
				} catch (SyntaxError e) {
					e.printStackTrace();
					return null;
				}
			}
			return idTmp;
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	public void setName(String s) {
		rwLock.writeLock().lock();
		try {
			name = s;
		} finally {
			rwLock.writeLock().unlock();
		}
	}
}