package api.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import api.json.JsonClasses.*;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import servlet.element.Food;
import servlet.element.Rock;
import servlet.world.Position;
import servlet.world.World;

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
		SessionID tmp = new JsonClasses.SessionID(id);
		return gson.toJson(tmp, SessionID.class);
	}
	
	/**
	 *  Used by Server: List all critters / Retrieve a critter
	 */
	public static String packCritterWithAllFields(Critter c) {
		CritterState tmp = 
				new CritterState(c);
		tmp.setType(null);
		return gson.toJson(tmp, CritterState.class);
	}
	
	/**
	 * Created by Client: login to the server
	 */
	public static String packPassword(int level, String password) {
		if (password == null)
			password = "";
		Password tmp = new Password(level, password);
		return gson.toJson(tmp, Password.class);
	}
	
	/**
	 * Created by client: create a food or rock object
	 */
	public static String packRockorFood(int row, int col, int amount, String type) {
		if(!type.equals("food") && !type.equals("rock"))
			return null;
		FoodOrRock tmp = 
				new FoodOrRock(row, col, type, amount);
		return gson.toJson(tmp, FoodOrRock.class);
	}
	
	/**
	 * Created by client: create a kind of critter at specified locations
	 */
	public static String packCreateCritter(Critter c, ArrayList<Position> a) {
		CreateCritter tmp = new CreateCritter(c, a);
		return gson.toJson(tmp, CreateCritter.class);
	}
	
	/**
	 * Created by client: create specified numbers of{@code number} critters 
	 * of the same kind at random locations
	 */
	public static String packCreateRandomPositionCritter(Critter c, int number) {
		CreateRandomPositionCritter tmp = 
				new JsonClasses.CreateRandomPositionCritter(c, number);
		return gson.toJson(tmp, CreateRandomPositionCritter.class);
	}
	
	/**
	 * Created by server: response to the request of client for creating
	 * new critters
	 */
	public static String packResponseToCreateCritters(String s, int[] ids) {
		ResponseToCreateCritters tmp = 
				new ResponseToCreateCritters(s, ids);
		return gson.toJson(tmp, ResponseToCreateCritters.class);
	}
	
	/**
	 * Created by server: the information of all the critters that is alive in the world
	 */
	public static String packListOfCritters(ArrayList<Critter> al, int session_id) {
		ArrayList<CritterState> tmp = new ArrayList<>();
		for(Critter c : al) {
			CritterState critter = new CritterState(c);
			critter.setType(null);
			if(c.session_id == session_id) {
				critter.program = c.getProgram().toString();
				critter.recently_executed_rule = c.getLastRuleIndex();
				tmp.add(critter);
			} else {
				tmp.add(new CritterState(c));
			}
		}
		return gson.toJson(tmp, ArrayList.class);
	}
	
	/**
	 * Created by server: the information of current world
	 */
	public static String packStateOfWorld(World w, int session_id) {
		worldState tmp = w.getWorldState(session_id);
		return gson.toJson(tmp, worldState.class);
	}
	
	/**
	 * Unit test
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, SyntaxError {
//		System.out.println(packSessionID(10));
//		Critter c = new Critter(new File("critter1.txt"));
//		c.setPosition(new Position(3,5));
//		System.out.println(packCritterWithAllFields(c));
//		System.out.println(packPassword(3, "abcd"));
		//System.out.println(packCritter(c,p));
		//System.out.println(packCritterWithAllFields(c,p));
//		ArrayList<Critter> al = new ArrayList<>();
//		for(int i = 0; i < 3; i++) {
//			Critter c = new Critter(new File("critter1.txt"));
//			c.session_id = i;
//			al.add(c);
//		}
//		System.out.println(packListOfCritters(al, 2));
//		World w = new World(10,10,"test");
//		Critter c = new Critter(new File("critter1.txt"));
//		c.session_id =1;
//		w.setElemAtPosition(c, new Position(4,5));
//		Critter d = new Critter(new File("critter1.txt"));
//		d.session_id =1;
//		w.setElemAtPosition(d, new Position(4,4));
//		w.setElemAtPosition(new Rock(), new Position(5,5));
//		w.setElemAtPosition(new Food(12), new Position(5,5));
//		Critter e = new Critter(new File("critter1.txt"));
//		e.session_id = 2;
//		w.setElemAtPosition(e, new Position(6,5));
//		System.out.println(packStateOfWorld(w, 1));
//		System.out.println(packStateOfWorld(w, 2));
	}
}
