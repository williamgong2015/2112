package client.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import api.PackJson;
import api.PositionInterpreter;
import api.UnpackJson;
import client.world.ClientElement;
import client.world.ClientPosition;
import api.JsonClasses.*;
import game.exceptions.SyntaxError;

/**
 * Client to submit request and receive response
 */
public class MyClient {
	
	private final String url;
	private int session_id;
	
	public MyClient(String u) {
		url = u;
	}

	/**
	 * Login to the Critter world
	 * @param level - user level (administrator, writer, reader)
	 * @param password - password for the specify user level
	 * Effect: got 200 and initialize session_id if success
	 *         got 401 "Unauthorized" response if failed
	 * @return 200 if success
	 *         401 if failed
	 */
	public int logIn(String level, String password) {
		try {
			String tmpURL = url + "CritterWorld/" + "login";
			URL l = new URL(tmpURL);
			HttpURLConnection connection = (HttpURLConnection) l.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			String tmp = PackJson.packPassword(level, password);
			w.println(tmp);
			w.flush();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			int id = UnpackJson.unpackSessionID(r);
			session_id = id;
			return connection.getResponseCode();
		} catch (IOException e) {
			return 401;
		}
	}
	
	// debugging 
	public void setSessionID(int id) {
		session_id = id;
	}
	
	public int getSessionID() {
		return session_id;
	}

	/**
	 * 
	 * @param list
	 * @return the list contains all critters in the world
	 * @throws IOException
	 */
	public int lisAllCritters(ArrayList<ClientElement> list) throws IOException {
		String tmpURL = url + "CritterWorld/" + "critters?session_id=" + 
				session_id;
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		ArrayList<ClientElement> li =  UnpackJson.unpackListOfCritters(r);
		list.addAll(li);
		return connection.getResponseCode(); 
	}
	
	/**
	 * Create 
	 * @param x
	 * @param a
	 * @param number
	 * @return
	 * @throws Exception 
	 */
	public int createCritter(File critterFile, ArrayList<ClientPosition> a, 
			int number) throws Exception{
		String tmpURL = url + "CritterWorld/" + "critters?session_id=" + 
				session_id;
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp;
		if(a == null)
			tmp = PackJson.packCreateRandomPositionCritter(
					new ClientElement(critterFile), number);
		else
			tmp = PackJson.packCreateCritter(
					new ClientElement(critterFile), a);
		w.println(tmp);
		w.flush();
		w.close();
		return connection.getResponseCode();
	}

	/**
	 * Retrieve a critter with critter id {@code id} from Server, 
	 * @param id
	 * @return a ClientElement for Client
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public int retrieveCritter(int id, ClientElement element) throws IOException, SyntaxError{
		String tmpURL = url + "CritterWorld/critter/" + id + "?session_id=" + 
				session_id;
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		element = UnpackJson.unpackCritter(r);
		return connection.getResponseCode();
	}

	/**
	 * Create a Food or Rock in the world based on the instruction from 
	 * Client side
	 * @param pos
	 * @param amount
	 * @param type
	 * @throws IOException
	 */
	public int createFoodOrRock(ClientPosition pos, Integer amount, String type) 
			throws IOException{
		String tmpURL = url + "CritterWorld/" + "world/create_entity?session_id=" + 
				session_id;
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "Created");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packRockorFood(PositionInterpreter.getR(pos.x, pos.y), 
				PositionInterpreter.getC(pos.x, pos.y), amount, type);
		w.println(tmp);
		w.flush();
		w.close();
		return connection.getResponseCode();
	}

	/**
	 * Remove a critter from the world given the critter id {@code id}
	 * @param id
	 * @throws IOException
	 */
	public int removeCritter(int id) throws IOException {
		String tmpURL = url + "CritterWorld/critter/" + id + "?session_id=" + 
				session_id;
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("DELETE");
		connection.setRequestProperty("Content-Type", "No Content");
		return connection.getResponseCode();
	}

	/**
	 * Create a new world
	 * @throws IOException 
	 */
	public int newWorld(File file) throws IOException {
		String tmpURL = url + "CritterWorld/" + "world?session_id=" + 
				session_id;
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "Created" );
		connection.setRequestMethod("POST");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packNewWorld(file);
		w.println(tmp);
		w.flush();
		w.close();
		return connection.getResponseCode();
	}

	/**
	 * Get the update of the world since {@code update_sice} from server
	 * and store it at the Client side
	 * If {@code update_sice} is less than 0, return the entire state of the 
	 * world. 
	 * @param update_since
	 * @param from_col, from_row, to_col, to_row: specify the range of world
	 * @throws IOException
	 */  
	public int getStateOfWorld(int update_since, int from_col, 
			int from_row, int to_col, int to_row, WorldState state) 
					throws IOException{
		// other server may not be using 0 as the starting update since value
		if (update_since == 0)
			return getStateOfWorld(from_col, from_row, to_col, 
					to_row, state);
		String tmpURL = url + "CritterWorld/" + "world?update_since=" + 
			update_since + "&from_row=" + from_row + "&to_row=" + to_row 
						 + "&from_col=" + from_col + "&to_col=" + to_col
						 +"&session_id=" + session_id;
		return getStateOfWorldHelper(tmpURL, state);
	}
	

	/**
	 * Get the update of the world since {@code update_sice} from server
	 * and store it at the Client side without {@code update_since}
	 * If {@code update_sice} is less than 0, return the entire state of the 
	 * world. 
	 * @param from_col, from_row, to_col, to_row: specify the range of world
	 * @throws IOException
	 */  
	public int getStateOfWorld(int from_col, int from_row, int to_col, 
			int to_row, WorldState state) 
					throws IOException{
		String tmpURL = url + "CritterWorld/" + "world?from_row=" + from_row + 
				"&to_row=" + to_row  + "&from_col=" + from_col + "&to_col=" 
				+ to_col +"&session_id=" + session_id;
		return getStateOfWorldHelper(tmpURL, state);
	}
	
	/**
	 * Get the state of whole world
	 * @param update_since
	 * @return
	 * @throws IOException
	 */
	public int getStateOfWorld(int update_since, WorldState state) 
			throws IOException {
		// other server may not be using 0 as the starting update since value
		if (update_since == 0)
			return getStateOfWorld(state);
		String tmpURL = url + "CritterWorld/" + "world?update_since=" + 
			update_since +"&session_id=" + session_id;
		return getStateOfWorldHelper(tmpURL, state);
	}
	
	/**
	 * Get the state of whole world with out {@code update_since}
	 * @return
	 * @throws IOException
	 */
	public int getStateOfWorld(WorldState state) 
			throws IOException {
		String tmpURL = url + "CritterWorld/" + "world?session_id=" 
			+ session_id;
		return getStateOfWorldHelper(tmpURL, state);
	}
	
	/**
	 * Helper method for getStateOfWorld with and without {@code update_since}
	 * @param tmpURL
	 * @param state
	 * @return
	 * @throws IOException
	 */
	private int getStateOfWorldHelper(String tmpURL, WorldState state) 
			throws IOException {
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		WorldState s = UnpackJson.unpackWorldState(r);
		state.copy(s);
		return connection.getResponseCode();
	}
	
	/**
	 * Advance the world by {@code n} step
	 * @throws IOException 
	 */
	public int advanceWorldByStep(int n) throws IOException {
		String tmpURL = url + "CritterWorld/" + "step?&session_id=" 
				+ session_id;
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "OK" );
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packAdvWorldCount(n);
		w.println(tmp);
		w.flush();
		w.close();
		return connection.getResponseCode();
	}
	
	/**
	 * Let the world at servlet to run at rate {@code n}
	 * @param n
	 * @throws IOException 
	 */
	public int runWorldAtSpeed(int n) throws IOException {
		String tmpURL = url + "CritterWorld/" + "run?session_id=" + session_id;
		URL l = new URL(tmpURL);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "OK" );
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packAdvanceWorldRate(n);
		w.println(tmp);
		w.flush();
		w.close();
		return connection.getResponseCode();
	}
}
