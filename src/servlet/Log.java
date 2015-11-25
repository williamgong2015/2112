package servlet;

import java.util.ArrayList;

import api.JsonClasses;

/**
 * Log record of the update in one critter's movement
 *
 */
public class Log {

	public ArrayList<JsonClasses.CritterState> critterStates = 
			new ArrayList<>();
	public ArrayList<JsonClasses.RockState> rockStates = 
			new ArrayList<>();
	public ArrayList<JsonClasses.FoodState> foodStates = 
			new ArrayList<>();
	public ArrayList<JsonClasses.NothingState> nothingStates = 
			new ArrayList<>();
	
	public Log() {
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(critterStates.size() + " critter changes: \n");
		for (int i = 0; i < critterStates.size(); ++i)
			sb.append(critterStates.get(i) + "\n");
		
		sb.append(rockStates.size() + " rock changes: \n");
		for (int i = 0; i < rockStates.size(); ++i)
			sb.append(rockStates.get(i) + "\n");
		
		sb.append(foodStates.size() + " food changes: \n");
		for (int i = 0; i < foodStates.size(); ++i)
			sb.append(foodStates.get(i) + "\n");
		
		sb.append(nothingStates.size() + " empty changes: \n");
		for (int i = 0; i < nothingStates.size(); ++i)
			sb.append(nothingStates.get(i) + "\n");
		
		return sb.toString();
	}
	
}
