package json;

import java.util.ArrayList;

import simulate.Critter;
import simulate.Position;

/**
 * Define all the Json classes
 */
public class JsonClasses {

	
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
		public int level;
		public String password;
		
		public Password(int l, String pw) {
			level = l;
			password = pw;
		}
	}
	
	/**
	 * wrap the information of rock and food
	 */
	public static class FoodOrRock {
		int row;
		int col;
		String type;
		int amount;
		public FoodOrRock(int r, int c, String s, int a) {
			row = r;
			col = c;
			type = s;
			amount = a;
		}
	}
	
	/**
	 * wrap the information of a critter
	 */
	public static class CreateCritter {
		String species_id;
		String program;
		int[] mem;
		Position[] positions;
		public CreateCritter(Critter c, ArrayList<Position> a) {
			species_id = c.getName();
			program = c.getProgram().toString();
			mem = c.getMeMArray();
			positions = new Position[a.size()];
			int index = 0;
			for(Position p : a)
				positions[index++] = p;
		}
	}
	
	/**
	 * wrap the information of a critter and the number of the criter
	 */
	public static class CreateRandomPositionCritter {
		String program;
		int[] mem;
		int num;
		CreateRandomPositionCritter(Critter c, int number) {
			program = c.getProgram().toString();
			mem = c.getMeMArray();
			num = number;
		}
	}
	
	/**
	 * wrap the information of response of server to create new critters
	 */
	public static class ResponseToCreateCritters {
		String species_id;
		int[] ids;
		ResponseToCreateCritters(String s, int[] ids) {
			species_id = s;
			this.ids = ids;
		}
	}
	
	/**
	 * a class used in the get world method
	 */
	public static class States {
		String type;
		
		States(String s){
			type = s;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String s) {
			type = s;
		}
	}
	
	/**
	 * wrap information of the position of a rock
	 */
	public static class RockState extends States {
		
		public int row;
		public int col;
		
		public RockState(Position p) {
			super("rock");
			row = p.getRow();
			col = p.getColumn();
		}
		
		public String toString() {
			return "Rock at (" + col + "," + row + ")";
		}
	}
	
	/**
	 * wrap the information of the food and its position
	 */
	public static class FoodState extends States {
		public int row;
		public int col;
		public int value;
		
		public FoodState(int v, Position p) {
			super("food");
			value = v;
			row = p.getRow();
			col = p.getColumn();
		}
		
		public String toString() {
			return "Food at (" + col + "," + row + ") with value " + value;
		}
	}
	
	/**
	 * the information of an element being removed
	 */
	public static class NothingState extends States {
		int row;
		int col;
		public NothingState(Position p) {
			super("nothing");
			row = p.getRow();
			col = p.getColumn();
		}
		
		public String toString() {
			return "Nothing at (" + col + "," + row + ")";
		}
	}
	
	/**
	 * wrap the information of a critter and its position
	 */
	public static class CritterState extends States{
		public int id;
		public String species_id;
		public int row;
		public int col;
		public int direction;
		public int[] mem;
		public String program;
		public Integer recently_executed_rule;
		public String recently_executed_rule_string;
		public CritterState(Critter c) {
			super("critter");
			id = c.ID;
			species_id = c.getName();
			Position p = c.getPosition();
			if(p != null) {
				row = p.getRow();
				col = p.getColumn();
			}
			direction = c.getDir();
			mem = c.getMeMArray();
			program = c.getProgram().toString();
			recently_executed_rule = c.getLastRuleIndex();
			recently_executed_rule_string = c.getLastRuleExe().toString();
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Critter at (" + col + "," + row + "), "
					+ "direction: " + direction + "\n");
			sb.append("Species: " + species_id + "\n");
			sb.append("Memory: ");
			for (int i = 0; i < mem.length; ++i) 
				sb.append("mem[" + i + "] = " + mem[i] + "\n");
			sb.append("Program: " + program + "\n");
			sb.append("recently_executed_rule is rule " + 
			recently_executed_rule + ": " + recently_executed_rule_string
			+ "\n");
			return sb.toString();
		}
	}
	
	/**
	 * the state of the world
	 */
	public static class worldState {
		public int current_timestep;
		public int current_version_number;
		public int update_since;
		public int rate;
		public String name;
		public int population;
		public int row;
		public int col;
		public int[] dead_critters;
		public States[] state;
	}
}