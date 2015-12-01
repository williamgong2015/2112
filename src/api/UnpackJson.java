package api;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import api.JsonClasses.*;
import client.element.ClientElement;
import game.exceptions.SyntaxError;


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
	public static int unpackSessionID(BufferedReader r) {
		SessionID tmp = gson.fromJson(r, SessionID.class);
		return tmp.session_id;
	}
	
	public static Password unpackPassword(BufferedReader r) {
		Password tmp = gson.fromJson(r, Password.class);
		return tmp;
	}
	
	/**
	 *  Used by Client: List all critters / Retrieve a critter
	 *  @return a class contains information of a critter and its position
	 *  if the critter is not created by the session_id of the client,
	 *  the program of the critter will be null
	 */
	public static ClientElement unpackCritter(BufferedReader json) {
		State tmp = gson.fromJson(json, State.class);
		tmp.type = JsonClasses.CRITTER;
		return new ClientElement(tmp);
	}
	
	/**
	 * Used by Server: login to the server
	 */
	public static Password unpackPassword(String json) {
		Password tmp = gson.fromJson(json, Password.class);
		return tmp;
	}
	
	/**
	 * Used by server: create a food or rock object
	 */
	public static FoodOrRock unpackRockorFood(BufferedReader r) {
		FoodOrRock tmp = gson.fromJson(r, FoodOrRock.class);
		return tmp;
	}
	
	/**
	 * Used by server: create a kind of critter at specified locations
	 */
	public static CreateCritter unpackCreateCritter(BufferedReader json) {
		CreateCritter tmp = gson.fromJson(json, CreateCritter.class);
		return tmp;
	}
	
	/**
	 * Used by server: create specified numbers of{@code number} critters 
	 * of the same kind at random locations
	 */
	public static CreateRandomPositionCritter
	unpackCreateRandomPositionCritter(BufferedReader json) {
		CreateRandomPositionCritter tmp = gson.fromJson(json, 
				CreateRandomPositionCritter.class);
		return tmp;
	}
	
	/**
	 * Used by client: response to the request of client for creating
	 * new critters
	 */
	public static ResponseToCreateCritters 
	unpackResponseToCreateCritters(BufferedReader json) {
		ResponseToCreateCritters tmp = gson.fromJson(json, 
				ResponseToCreateCritters.class);
		return tmp;
	}
	
	/**
	 * Used by client :unpack the information of list of critters sent
	 * from the server
	 */
	public static ArrayList<ClientElement> unpackListOfCritters(BufferedReader br) {
		Type t = new TypeToken<ArrayList<State>>(){}.getType();
		ArrayList<State> tmp = gson.fromJson(br, t);//TODO
		ArrayList<ClientElement> array = new ArrayList<>();
		for(State s : tmp) {
			s.type = JsonClasses.CRITTER;
			array.add(new ClientElement(s));
		}
		return array;
	}
	
	public static WorldState unpackWorldState(BufferedReader br) {
		WorldState tmp = gson.fromJson(br, WorldState.class);
		return tmp;
	}
	
	public static void main(String[] args) throws FileNotFoundException, SyntaxError {
		BufferedReader br = new BufferedReader(new FileReader("a7.txt"));
		//ArrayList<State> tmp = unpackListOfCritters(br);
		//Critter c = new Critter(tmp.get(0));
		//System.out.println(c);
	}

	public static CreateNewWorld unpackCreateNewWorld(BufferedReader r) {
		return gson.fromJson(r, CreateNewWorld.class);
	}
}
