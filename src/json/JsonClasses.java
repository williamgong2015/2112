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
		int level;
		String password;
		
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
	 * wrap the information of a critter and its position
	 */
	public static class GetCritter {
		public int id;
		public String species_id;
		int row;
		int col;
		int direction;
		int[] mem;
		public GetCritter(Critter c, Position p) {
			id = c.ID;
			species_id = c.getName();
			row = p.getRow();
			col = p.getColumn();
			direction = c.getDir();
			mem = c.getMeMArray();
		}
	}
	
	/**
	 * wrap the information of a critter(including its program and the last
	 * rule it executed) and its position
	 */
	public static class critterWithAllFields extends GetCritter{
		
		private String program;
		private int recently_executed_rule;
		public critterWithAllFields(Critter c, Position p) {
			super(c, p);
			program = c.getProgram().toString();
			recently_executed_rule = c.getLastRuleIndex();
		}

	}

	/**
	 * wrap the information of a critter and the locations
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
	}
	
	/**
	 * wrap information of the position of a rock
	 */
	public static class RockStates extends States {
		
		int row;
		int col;
		
		RockStates(Position p) {
			super("rock");
			row = p.getRow();
			col = p.getColumn();
		}
	}
	
	/**
	 * wrap the information of a critter and its position
	 */
	public static class CritterStates extends States {
		
		critterWithAllFields cr;
		
		CritterStates(Position p, Critter c) {
			super("critter");
			cr = new critterWithAllFields(c, p);
		}
	}
	
	/**
	 * wrap the information of the food and its position
	 */
	public static class FoodState extends States {
		int row;
		int col;
		int value;
		
		FoodState(int v, Position p) {
			super("food");
			value = v;
			row = p.getRow();
			col = p.getColumn();
		}
	}
	
	/**
	 * the information of an element being removed
	 */
	public static class NotingState extends States {
		int row;
		int col;
		NotingState(Position p) {
			super("nothing");
			row = p.getRow();
			col = p.getColumn();
		}
	}
	
	/**
	 * the state of the world
	 */
	public class worldState {
		int current_timestep;
		int current_version_number;
		int update_since;
		int rate;
		String name;
		int population;
		int row;
		int col;
		int[] dead_critters;
		States[] state;
	}
	
	public class listOfCritters {
		ArrayList<critterWithAllFields> array;
	}
}