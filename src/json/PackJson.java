package json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import exceptions.SyntaxError;
import simulate.Critter;
import simulate.Position;
import simulate.World;

/**
 * A class contains static methods to create JSON object from Java objects.
 * The format of JSON string is defined at {@link http://docs.cs2112fall2015.
 * apiary.io/#reference/world/world-state/get-the-state-of-the-world}
 * 
 * It is used by both {@code Client} and {@code Servlet}
 *
 */
public class PackJson {
	
	// only create one instance to save space
	private static Gson gson = new Gson();


	/**
	 * Created by Servlet: pack the session id before send it back to client
	 */
	public static String packSessionID(int id) {
		JsonClasses.SessionID tmp = new JsonClasses.SessionID(id);
		return gson.toJson(tmp, JsonClasses.SessionID.class);
	}
	
	/**
	 *  Used by Server: List all critters / Retrieve a critter
	 */
	public static String packCritter(Critter c, Position p) {
		JsonClasses.GetCritter tmp = new JsonClasses.GetCritter(c, p);
		return gson.toJson(tmp, JsonClasses.GetCritter.class);
	}
	
	/**
	 *  Used by Server: List all critters / Retrieve a critter
	 */
	public static String packCritterWithAllFields(Critter c, Position p) {
		JsonClasses.critterWithAllFields tmp = 
				new JsonClasses.critterWithAllFields(c, p);
		return gson.toJson(tmp, JsonClasses.critterWithAllFields.class);
	}
	
	/**
	 * Created by Client: login to the server
	 */
	public static String packPassword(int level, String password) {
		JsonClasses.Password tmp = new JsonClasses.Password(level, password);
		return gson.toJson(tmp, JsonClasses.Password.class);
	}
	
	/**
	 * Created by client: create a food or rock object
	 */
	public static String packRockorFood(int row, int col, int amount, String type) {
		if(!type.equals("food") && !type.equals("rock"))
			return null;
		JsonClasses.FoodOrRock tmp = 
				new JsonClasses.FoodOrRock(row, col, type, amount);
		return gson.toJson(tmp, JsonClasses.FoodOrRock.class);
	}
	
	/**
	 * Created by client: create a kind of critter at specified locations
	 */
	public static String packCreateCritter(Critter c, ArrayList<Position> a) {
		JsonClasses.CreateCritter tmp = new JsonClasses.CreateCritter(c, a);
		return gson.toJson(tmp, JsonClasses.CreateCritter.class);
	}
	
	/**
	 * Created by client: create specified numbers of{@code number} critters 
	 * of the same kind at random locations
	 */
	public static String packCreateRandomPositionCritter(Critter c, int number) {
		JsonClasses.CreateRandomPositionCritter tmp = 
				new JsonClasses.CreateRandomPositionCritter(c, number);
		return gson.toJson(tmp, JsonClasses.CreateRandomPositionCritter.class);
	}
	
	/**
	 * Created by server: response to the request of client for creating
	 * new critters
	 */
	public static String packResponseToCreateCritters(String s, int[] ids) {
		JsonClasses.ResponseToCreateCritters tmp = 
				new JsonClasses.ResponseToCreateCritters(s, ids);
		return gson.toJson(tmp, JsonClasses.ResponseToCreateCritters.class);
	}
	
	public static String packListOfCritters(ArrayList<Critter> al, int session_id, World w) {
		ArrayList<JsonClasses.GetCritter> tmp = new ArrayList<>();
		for(Critter c : al) {
			if(c.session_id == session_id) {
				Position p = w.getPositionFromCritter(c);
				tmp.add(new JsonClasses.critterWithAllFields(c, p));
			} else {
				Position p = w.getPositionFromCritter(c);
				tmp.add(new JsonClasses.GetCritter(c, p));
			}
		}
		return gson.toJson(tmp, JsonClasses.listOfCritters.class);
	}
	
	/**
	 * Unit test
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, SyntaxError {
		//System.out.println(packSessionID(10));
		Critter c = new Critter(new File("critter1.txt"));
		Position p = new Position(3,4);
		//System.out.println(packCritter(c,p));
		//System.out.println(packCritterWithAllFields(c,p));
		System.out.println(gson.toJson(p, Position.class));
	}
}
