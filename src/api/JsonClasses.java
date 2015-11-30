package api;

import java.util.ArrayList;

import com.google.gson.Gson;

import client.element.ClientElement;
import client.world.ClientPosition;
import servlet.element.Critter;
import servlet.world.Position;

/**
 * Define all the Json classes
 */
public class JsonClasses {

	static Gson gson = new Gson();
	
	public final static String ROCK = "rock";
	public final static String FOOD = "food";
	public final static String NOTHING = "nothing";
	public final static String CRITTER = "critter";
	
	
	/**
	 * Wrap session id
	 */
	public static class SessionID {
		int session_id;
		
		public SessionID(int a) {
			session_id = a;
		}
	}
	
	/**
	 * wrap password and level
	 */
	public static class Password {
		public String level;
		public String password;
		
		public Password(String l, String pw) {
			level = l;
			password = pw;
		}
	}
	
	/**
	 * wrap the information of rock and food
	 */
	public static class FoodOrRock {
		public int row;
		public int col;
		public String type;
		public int amount;
		public FoodOrRock(int r, int c, String s, int a) {
			row = r;
			col = c;
			type = s;
			amount = a;
		}
	}
	
	/**
	 * wrap the information of a critter
	 * Client pack it and Server unpack it 
	 */
	public static class CreateCritter {
		public String species_id;
		public String program;
		public int[] mem;
		public Position[] positions;
		/**
		 * Client create this package
		 * @param c - ClientElement that is a critter
		 * @param a - ClientPosition that will place a such critter
		 */
		public CreateCritter(ClientElement c, ArrayList<ClientPosition> a) {
			if (c.type != "critter")
				return;
			species_id = c.species_id;
			program = c.program;
			mem = c.mem;
			positions = new Position[a.size()];
			int index = 0;
			for(ClientPosition p : a)
				positions[index++] = PositionInterpreter.clientToServer(p);
		}
	}
	
	/**
	 * wrap the information of a critter and the number of the critter
	 * Client pack it and Server unpack it
	 */
	public static class CreateRandomPositionCritter {
		public String program;
		public int[] mem;
		public int num;
		/**
		 * Client create this package
		 * @param c - ClientElement that is a critter
		 * @param number - number of critter to create
		 */
		CreateRandomPositionCritter(ClientElement c, int number) {
			if (c.type != "critter")
				return;
			program = c.program;
			mem = c.mem;
			num = number;
		}
	}
	
	/**
	 * wrap the information of response of server to create new critters
	 */
	public static class ResponseToCreateCritters {
		String species_id;
		int[] ids;
		public ResponseToCreateCritters(String s, int[] ids) {
			species_id = s;
			this.ids = ids;
		}
	}
	
	/**
	 * a class used in the get world method
	 */
	public static class State {
		String type;
		public int row;
		public int col;
		public Integer food_value;
		
		public Integer id;
		public String species_id;
		public Integer direction;
		public int[] mem;
		public String program;
		public Integer recently_executed_rule;
		
		public State(Position p) {
			row = p.getRow();
			col = p.getColumn();
		}
		
		public void setRock() {
			type = ROCK;
		}
		
		public void setFood(int v) {
			food_value = v;
			type = FOOD;
		}
	
		public void setNothing() {
			type = NOTHING;
		}
		
		public void setCriiter(Critter c) {
			type = CRITTER;
			id = c.ID;
			species_id = c.getName();
			Position p = c.getPosition();
			if(p != null) {
				row = p.getRow();
				col = p.getColumn();
			}
			direction = c.getDir();
			mem = c.getMeMArray();
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String s) {
			type = s;
		}
	}
	
	/**
	 * wrap the state of the world
	 */
	public static class WorldState {
		public int current_timestep;
		public int current_version_number;
		public int update_since;
		public double rate;
		public String name;
		public int population;
		public int row;
		public int col;
		public Integer[] dead_critters;
		public State[] state;
	}
	
	/**
	 * wrap create a new world with description 
	 */
	public static class CreateNewWorld {
		public String description;
		public CreateNewWorld(String s) {
			description = s;
		}
	}
	
	/**
	 * wrap advance world count  
	 */
	public static class AdvanceWorldCount {
		public int count;
		public AdvanceWorldCount(int count) {
			this.count = count;
		}
	}
	
	/**
	 * wrap advance world rate  
	 */
	public static class AdvanceWorldRate {
		public int rate;
		public AdvanceWorldRate(int rate) {
			this.rate = rate;
		}
	}
}