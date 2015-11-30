package servlet.world;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import api.JsonClasses;
import api.JsonClasses.CreateRandomPositionCritter;
import api.JsonClasses.State;
import game.constant.Constant;
import game.constant.IDX;
import game.exceptions.SyntaxError;
import game.utils.RandomGen;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import servlet.Log;
import servlet.element.Critter;
import servlet.element.Element;
import servlet.element.Food;
import servlet.element.Nothing;
import servlet.element.Rock;
import servlet.executor.Executor;
import servlet.executor.ResultList;
import servlet.interpreter.InterpreterImpl;
import servlet.interpreter.Outcome;
import test.testsA5.critterTest;

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
	public int version_number = 1; // be 1 after it has been constructed
	private int rate;  // how many steps per second the world is simulating
	public int critterIDCount = 0;

	// maps position to element in the world
	public Hashtable<Position, Element> hexes = new Hashtable<>();

	// order of critters in the world to take actions
	public ArrayList<Critter> order = new ArrayList<>();

	private ArrayList<Log> logs = new ArrayList<>();
	
	// stepup a timeline to trigger the world to step another step 
	public Timeline timeline;
	private int intialStepsPerSecond = 1;
	
	// - if the speed <= 30, each cycle lapse the world, draw the world, 
	//   so counterWorldLapse = counterWorldDraw
	// - if the speed > 30, each cycle lapse the world but 
	//   draw the world only when 30*counterWorldLapse/speed > counterWorldDraw
	//   and have counterWorldDraw++ after drawing the world
	private volatile int speed;
	private volatile int counterWorldLapse;
	private volatile int counterWorldDraw;

	
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
				String file = critterTest.class.getResource("critter1.txt").getPath();
				Critter c;
				try {
					c = new Critter(file, 1, 0);
					hexes.put(pos, c);
					Log logTmp = logs.get(logs.size()-1);
					logTmp.updates.put(pos, c);
				} catch (IOException | SyntaxError e) {
					e.printStackTrace();
				}
				
			}
		}
	}

	/**
	 * Create and return a world with a world file
	 * @param session_id - id of user who load the world
	 */
	public static World loadWorld(File filename, int session_id) {
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
			world.logs = new ArrayList<>();
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
					String file = temp[1];
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
			r.close();
			return world;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("No such file");
			return null;
		}

	}

	/**
	 * @param filename
	 * @param session_id - id of user who load the world
	 * @return
	 */
	public static World loadWorld(String filename, int session_id) {
		return loadWorld(new File(filename), session_id);
	}
	
	/**
	 * Return the timeline which controls the world simulation
	 */
	private Timeline setupTimeline() {
		// set the iteration count to be as large as it can
		Timeline tmp = new Timeline();
		tmp.setCycleCount(Integer.MAX_VALUE);
		// recounts cycle count every time it plays again
		tmp.setAutoReverse(false);  
		tmp.getKeyFrames().add(
				getWorldSimulationKeyFrame(intialStepsPerSecond));
		return tmp;
	}
	
	/**
	 * Change the simulation rate of the world and store it in 
	 * {@code rate}
	 * @param rate
	 */
	public void changeSimulationSpeed(int rate) {
		boolean wasRunning = false;
		if (timeline.statusProperty().get() == Status.RUNNING)
			wasRunning = true;
		timeline.stop();
		timeline.getKeyFrames().setAll(
				getWorldSimulationKeyFrame(rate)
				);
		if (wasRunning)
			timeline.play();
		this.rate = rate;
	}
	
	/**
	 * Get the KeyFrame for the timeline which controls the world running speed
	 * @param stepsPerSecond
	 * @return
	 */
	private KeyFrame getWorldSimulationKeyFrame(int stepsPerSecond) {
		speed = stepsPerSecond;
		counterWorldLapse = 0;
		counterWorldDraw = 0;
		KeyValue tmp = null;
		return new KeyFrame(Duration.seconds(1 / stepsPerSecond), 
				"world lapse",
				new EventHandler<ActionEvent>() {
			@Override 
			public void handle(ActionEvent e) {
				worldRunAhead();
			}
		}, tmp);
	}
	
	/**
	 * Have the underlying world proceed for one turn and update the GUI
	 * with a maximum speed limitation of 30
	 */
	private void worldRunAhead() {
		// no need to bother with the counter if speed <= 30
		// because always lapse and draw the world at the same time
		if (speed <= 30) {
			lapse();
			return;
		}
		// detect overflow, lose a little precision of interval here
		if (counterWorldLapse == Integer.MAX_VALUE) {
			counterWorldLapse = 0;
			counterWorldDraw = 0;
		}
		lapse();
	}
	
	public int getSimulationRate() {
		return rate;
	}
	
	/**
	 * add a critter to the arraylist
	 */
	public void addCritterToList(Critter c) {
		order.add(c);
	}
	


	/**
	 * A new turn of the world 
	 * 
	 */
	public void lapse() {
		turns++;
		version_number++;
		ArrayList<Critter> toDelete = new ArrayList<>();
		// update every critter until it execute a action or has being 
		// updated for 999 PASS (for the second one, take a wait action)
		int i = 0;
		while (i < order.size()) {
			logs.add(new Log()); // create a new log for this critter
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
		}

		// remove the critter need to be delete and insert the critter need 
		// to be inserted
		for (Critter critter : toDelete) {
			order.remove(critter);
			logs.get(version_number).deadCritterID.add(critter.ID);
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
		Log logTmp = logs.get(logs.size()-1);
		logTmp.updates.put(pos, new Nothing());
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
		Hashtable<Position, Element> result = new Hashtable<>();
		for (int i = update_since; i < this.version_number; ++i) {
			Set<Map.Entry<Position, Element>> set = 
					logs.get(i).updates.entrySet();
			for(Map.Entry<Position, Element> m : set) {
				Element e = m.getValue();
				Position p = m.getKey();
				if (p.getColumn() >= from_col && p.getColumn() <= to_col &&
					p.getRow() >= from_row && p.getRow() <= to_row)
					result.put(p, e);
			}
		}
		return result;
	}
	
	/**
	 * Get all the critter that died since {@code update_since}
	 * @param update_since
	 * @return
	 */
	public ArrayList<Integer> getDeadCritterIDSince(int update_since) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int i = update_since; i< this.version_number; ++i) {
			for (Integer id : logs.get(i).deadCritterID)
				result.add(id);
		}
		return result;
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
		JsonClasses.WorldState s = new JsonClasses.WorldState();
		s.col = column;
		s.current_timestep = turns;
		s.current_version_number = version_number;
		s.name = this.name;
		s.population = order.size();
		s.row = this.row;
		s.update_since = 0;
		s.rate = rate;
		s.state = new State[table.size()];
		s.dead_critters = new Integer[deadCritters.size()];
		for (int i = 0; i < deadCritters.size(); ++i) 
			s.dead_critters[i] = deadCritters.get(i);
		//TODO dead critters.
		int index = 0;
		Set<Map.Entry<Position, Element>> set = table.entrySet();
		for(Map.Entry<Position, Element> m : set) {
			Element e = m.getValue();
			Position p = m.getKey();
			// for cleaning up the hex
			if (e == null) {
				s.state[index++] = new State(p);
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
				critter.setCriiter(c);
				if(c.session_id == session_id || isAdmin == true) {
					critter.program = c.getProgram().toString();
					critter.recently_executed_rule = c.getLastRuleIndex();
					s.state[index++] = critter;
				} else {
					s.state[index++] = critter;
				}
				break;
			case Element.NOTHING :
				State nothing = new State(p);
				nothing.setNothing();
				s.state[index++] = nothing;
				break;
			}
		}
		return s;
	}
	
	/**
	 * add a critter to the world
	 */
	public void addCritter(Critter c, Position pos) {
		this.setElemAtPosition(c, pos);
		addCritterToList(c);
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
		int num = c.num;
		ArrayList<Integer> idTmp = new ArrayList<>();
		for(int i = 0; i < num;) {
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
	}
	
	public void setName(String s) {
		name = s;
	}
}