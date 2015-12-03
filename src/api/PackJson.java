package api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.google.gson.Gson;

import api.JsonClasses.*;
import client.element.ClientElement;
import client.world.ClientPosition;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import servlet.element.Element;
import servlet.element.Nothing;
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
	 * Used by Server: Retrieve a critter
	 * @param c
	 * @param session_id   - who is trying to retrieve the critter
	 * @param isAdmin      - if the critter program and last rule should be
	 *                       retrieved because user is an admin
	 * @return
	 */
	public static String packCritterInfo(Critter c,
			int session_id, boolean isAdmin) {
		State tmp = new State(c.getPosition());
		tmp.setCriiter(c, session_id == c.session_id || isAdmin == true);
		tmp.setType(null);
		return gson.toJson(tmp, State.class);
	}
	
	/**
	 * Created by Client: login to the server
	 */
	public static String packPassword(String level, String password) {
		if (password == null)
			password = "";
		Password tmp = new Password(level, password);
		return gson.toJson(tmp, Password.class);
	}
	
	/**
	 * Created by client: create a food or rock object
	 */
	public static String packRockorFood(int row, int col, Integer amount, 
			String type) {
		if(!type.equals(JsonClasses.FOOD) && 
				!type.equals(JsonClasses.ROCK))
			return null;
		FoodOrRock tmp = 
				new FoodOrRock(row, col, type, amount);
		return gson.toJson(tmp, FoodOrRock.class);
	}
	
	/**
	 * Created by client: create a kind of critter at specified locations
	 */
	public static String packCreateCritter(ClientElement c, ArrayList<ClientPosition> a) {
		CreateCritter tmp = new CreateCritter(c, a);
		tmp.program = c.program;
		return gson.toJson(tmp, CreateCritter.class);
	}
	
	/**
	 * Created by client: create specified numbers of{@code number} critters 
	 * of the same kind at random locations
	 */
	public static String packCreateRandomPositionCritter(ClientElement c, int number) {
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
	 * Created by server: the information of all the critters 
	 * that is alive in the world
	 * @param al - an array list contains all the critter in the world
	 * @param session_id
	 * @param isAdmin - if the user is admin
	 * @return
	 */
	public static String packListOfCritters(ArrayList<Critter> al, 
			int session_id, boolean isAdmin) {
		ArrayList<State> tmp = new ArrayList<>();
		for(Critter c : al) {
			State critter = new State(c.getPosition());
			critter.setCriiter(c, c.session_id == session_id || isAdmin == true);
			critter.setType(null);
			tmp.add(critter);
		}
		return gson.toJson(tmp, ArrayList.class);
	}
	
	/**
	 * Created by server: the information of the world
	 * @param getWholeWorld - if all the hex should be returned
	 */
	public static String packStateOfWorld(World w, int session_id, 
			boolean isAdmin, int update_since, int from_col, 
			int from_row, int to_col, int to_row, boolean getWholeWorld) {
		Hashtable<Position, Element> table; 
		if (getWholeWorld) 
			table = getWholeWorldInfo(w, from_col, from_row, to_col, to_row);
		else
			table = w.getUpdatesSinceMap(update_since, from_col, 
						from_row, to_col, to_row);
		ArrayList<Integer> tmp = w.getDeadCritterIDSince(update_since);
		WorldState state = w.getWorldState(session_id, isAdmin, table, 
				tmp);
		return gson.toJson(state, WorldState.class);
	}
	
	/**
	 * Get all the world information including null to be interpreted as empty
	 * @param w
	 * @return
	 */
	private static Hashtable<Position, Element> getWholeWorldInfo(World w, 
			int from_col, int from_row, int to_col, int to_row) {
		Hashtable<Position, Element> result = new Hashtable<>();
		int worldY = PositionInterpreter.getY(w.getColumn(), w.getRow());
		int worldX = PositionInterpreter.getX(w.getColumn(), w.getRow());
		int from_x = PositionInterpreter.getX(from_col, from_row);
		int from_y = PositionInterpreter.getY(from_col, from_row);
		int to_x = PositionInterpreter.getX(to_col, to_row);
		int to_y = PositionInterpreter.getY(to_col, to_row);
		for (int i = Math.max(from_y, 0); i <= Math.min(to_y, worldY); ++i) {
			for (int j = Math.max(from_x, 0); j <= Math.min(to_x, worldX); ++j) {
				if (i % 2 != j % 2)
					continue;
				Position pos = new Position(PositionInterpreter.getC(j, i), 
						PositionInterpreter.getR(j, i));
				if (w.hexes.get(pos) == null)
					result.put(pos, new Nothing());
				else 
					result.put(pos, w.hexes.get(pos));
			}
		}
		return result;
	}
	
	/**
	 * Create by client: ask the servlet to create a new world
	 * @param description
	 * @return
	 */
	public static String packNewWorld(String description) {
		return gson.toJson(new CreateNewWorld(description));
	}
	
	/**
	 * Create by client: ask the servlet to run the world at rate {@code n}
	 * @param n
	 * @return
	 */
	public static String packAdvanceWorldRate(int n) {
		return gson.toJson(new AdvanceWorldRate(n));
	}
	
	/**
	 * Create by client: ask the servlet world to advance by {@code n} steps
	 * @param n
	 * @return
	 */
	public static String packAdvWorldCount(int n) {
		return gson.toJson(new AdvanceWorldCount(n));
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
