package json;

import java.io.BufferedReader;
import java.util.ArrayList;

import com.google.gson.Gson;


/**
 * A class contains static methods to create Java objects from JSON object.
 * The format of JSON string is defined at {@link http://docs.cs2112fall2015.
 * apiary.io/#reference/world/world-state/get-the-state-of-the-world}
 * 
 * It is used by both {@code Client} and {@code Servlet}
 * 
 */
public class UnpackJson {
	
	private static Gson gson = new Gson();

	/**
	 * @return session id
	 */
	public static int unpackSessionID(String json) {
		JsonClasses.SessionID tmp = gson.fromJson(json, 
				JsonClasses.SessionID.class);
		return tmp.session_id;
	}
	
	/**
	 *  Used by Client: List all critters / Retrieve a critter
	 *  @return a class contains information of a critter and its position
	 *  if the critter is not created by the session_id of the client,
	 *  the program of the critter will be null
	 */
	public static JsonClasses.critterWithAllFields unpackCritter(BufferedReader json) {
		JsonClasses.critterWithAllFields tmp = gson.fromJson(json, 
				JsonClasses.critterWithAllFields.class);
		return tmp;
	}
	
	/**
	 * Used by Server: login to the server
	 */
	public static JsonClasses.Password unpackPassword(String json) {
		JsonClasses.Password tmp = gson.fromJson(json, 
				JsonClasses.Password.class);
		return tmp;
	}
	
	/**
	 * Used by server: create a food or rock object
	 */
	public static json.JsonClasses.FoodOrRock unpackRockorFood(String json) {
		JsonClasses.FoodOrRock tmp = gson.fromJson(json, 
				JsonClasses.FoodOrRock.class);
		return tmp;
	}
	
	/**
	 * Used by server: create a kind of critter at specified locations
	 */
	public static JsonClasses.CreateCritter unpackCreateCritter(String json) {
		JsonClasses.CreateCritter tmp = gson.fromJson(json, 
				JsonClasses.CreateCritter.class);
		return tmp;
	}
	
	/**
	 * Used by server: create specified numbers of{@code number} critters 
	 * of the same kind at random locations
	 */
	public static JsonClasses.CreateRandomPositionCritter
	unpackCreateRandomPositionCritter(String json) {
		JsonClasses.CreateRandomPositionCritter tmp = gson.fromJson(json, 
				JsonClasses.CreateRandomPositionCritter.class);
		return tmp;
	}
	
	/**
	 * Used by client: response to the request of client for creating
	 * new critters
	 */
	public static JsonClasses.ResponseToCreateCritters 
	unpackResponseToCreateCritters(BufferedReader json) {
		JsonClasses.ResponseToCreateCritters tmp = gson.fromJson(json, 
				JsonClasses.ResponseToCreateCritters.class);
		return tmp;
	}
	
	/**
	 * Used by client :unpack the information of list of critters sent
	 * from the server
	 */
	public static JsonClasses.listOfCritters unpackListOfCritters(BufferedReader br) {
		JsonClasses.listOfCritters tmp = gson.fromJson(br, 
				JsonClasses.listOfCritters.class);
		return tmp;
	}
	
	public static JsonClasses.worldState unpackWorldState(BufferedReader br) {
		JsonClasses.worldState tmp = gson.fromJson(br, 
				JsonClasses.worldState.class);
		return tmp;
	}
}
