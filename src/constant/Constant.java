package constant;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * A class to read and store all the constants from constant.txt file
 *
 * Class invariant, 
 * {@code hasInitialized} is true only if all the constant has been initialized
 */
public class Constant {
	
	private static boolean hasInitialized = false;
	
	// how many constants are there in this class
	private static final int SIZE = 19;
	private static int bitmap = 0;
	
	// The multiplier for all damage done by attacking
	public static int BASE_DAMAGE;

	// Controls how quickly increased offensive or defensive ability affects damage
	public static double DAMAGE_INC;

	// How much energy a critter can have per point of size
	public static int ENERGY_PER_SIZE;

	// How much food is created per point of size when a critter dies
	public static int FOOD_PER_SIZE;

	// Maximum distance at which food can be sensed
	public static int MAX_SMELL_DISTANCE;

	// The value reported when a rock is sensed
	public static int ROCK_VALUE;

	// Default number of columns in the world map
	public static int COLUMNS;

	// Default number of rows in the world map
	public static int ROWS;

	// The maximum number of rules that can be run per critter turn
	public static int MAX_RULES_PER_TURN;

	// Energy gained from sun by doing nothing
	public static int SOLAR_FLUX;

	// Energy cost of moving (per unit size)
	public static int MOVE_COST;

	// Energy cost of attacking (per unit size)
	public static int ATTACK_COST;

	// Energy cost of growing (per size and complexity)
	public static int GROW_COST;

	// Energy cost of budding (per unit complexity)
	public static int BUD_COST;

	// Energy cost of successful mating (per unit complexity)
	public static int MATE_COST;

	// Complexity cost of having a rule
	public static int RULE_COST;

	// Complexity cost of having an ability point
	public static int ABILITY_COST;

	// Energy of a newly birthed critter
	public static int INITIAL_ENERGY;

	// Minimum number of memory entries in a critter
	public static int MIN_MEMORY;

	public final static int MAX_PASS = 999; 
	public final static int INIT_PASS = 1; // all pass are initialized to 1
	public final static int ORI_RANGE = 6; // the range of critter orientation 
	public final static int INIT_SIZE = 1;
	public final static int CHANCE_OF_MUTATE = 5; // 20% of mutate after bud
	
	/**
	 * Initialize the constant dictionary with a constant.txt file
	 * @throws IOException
	 */
	public static void init() throws IOException {
		Scanner in = new Scanner(new File("txt/constant.txt")); 

		while (in.hasNextLine()) {
			String newConstant = in.nextLine();
			String[] token = newConstant.split(" ", 3);
			switch (token[0]) {
				case "BASE_DAMAGE": BASE_DAMAGE = Integer.parseInt(token[1]);
				bitmap |= 1 << 0;
				break;
	
				case "DAMAGE_INC": DAMAGE_INC = Double.parseDouble(token[1]);
				bitmap |= 1 << 1;
				break;
	
				case "ENERGY_PER_SIZE": ENERGY_PER_SIZE = Integer.parseInt(token[1]);
				bitmap |= 1 << 2;
				break;
	
				case "FOOD_PER_SIZE": FOOD_PER_SIZE = Integer.parseInt(token[1]);
				bitmap |= 1 << 3;
				break;
	
				case "MAX_SMELL_DISTANCE": MAX_SMELL_DISTANCE = Integer.parseInt(token[1]);
				bitmap |= 1 << 4;
				break;
	
				case "ROCK_VALUE": ROCK_VALUE = Integer.parseInt(token[1]);
				bitmap |= 1 << 5;
				break;
	
				case "COLUMNS": COLUMNS = Integer.parseInt(token[1]);
				bitmap |= 1 << 6;
				break;
	
				case "ROWS": ROWS = Integer.parseInt(token[1]);
				bitmap |= 1 << 7;
				break;
	
				case "MAX_RULES_PER_TURN": MAX_RULES_PER_TURN = Integer.parseInt(token[1]);
				bitmap |= 1 << 8;
				break;
	
				case "SOLAR_FLUX": SOLAR_FLUX = Integer.parseInt(token[1]);
				bitmap |= 1 << 9;
				break;
	
				case "MOVE_COST": MOVE_COST = Integer.parseInt(token[1]);
				bitmap |= 1 << 10;
				break;
	
				case "ATTACK_COST": ATTACK_COST = Integer.parseInt(token[1]);
				bitmap |= 1 << 11;
				break;
	
				case "GROW_COST": GROW_COST = Integer.parseInt(token[1]);
				bitmap |= 1 << 12;
				break;
	
				case "BUD_COST": BUD_COST = Integer.parseInt(token[1]);
				bitmap |= 1 << 13;
				break;
	
				case "MATE_COST": MATE_COST = Integer.parseInt(token[1]);
				bitmap |= 1 << 14;
				break;
	
				case "RULE_COST": RULE_COST = Integer.parseInt(token[1]);
				bitmap |= 1 << 15;
				break;
	
				case "ABILITY_COST": ABILITY_COST = Integer.parseInt(token[1]);
				bitmap |= 1 << 16;
				break;
	
				case "INITIAL_ENERGY": INITIAL_ENERGY = Integer.parseInt(token[1]);
				bitmap |= 1 << 17;
				break;
	
				case "MIN_MEMORY": MIN_MEMORY = Integer.parseInt(token[1]);
				bitmap |= 1 << 18;
				break;
				
				default: 
					break;
			}
		}
		for (int i = 0; i < SIZE; ++i) {
			if ((bitmap & (1 << i)) == 0) {
				hasInitialized = false;
				in.close();
				return;
			}
		}
		hasInitialized = true;
		in.close();
	}
	
	/**
	 * 
	 * @return if all the constant in the class has been initialized
	 */
	public static boolean hasBeenInitialized() {
		return hasInitialized;
	}
}
