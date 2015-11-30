package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import api.PackJson;
import api.UnpackJson;
import client.element.ClientElement;
import client.world.ClientPosition;
import client.world.ClientWorld;
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
			URL l = new URL(url + "login");
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
			String json = r.readLine();
			int id = UnpackJson.unpackSessionID(json);
			dumpResponse(r);
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

	//TODO: not sure what to do about the result it returns
	public ArrayList<CritterState> lisAllCritters() throws IOException {
		URL l = new URL(url + "critters?session_id" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		return UnpackJson.unpackListOfCritters(r);
	}
	
	/**
	 * Create 
	 * @param c
	 * @param a
	 * @param number
	 * @return
	 * @throws IOException
	 */
	public int createCritter(File critterFile, ArrayList<ClientPosition> a, 
			int number) throws IOException{
		URL l = new URL(url + "critters?session_id=" + session_id);
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
		System.out.println("tmp:");
		System.out.println(tmp);
		w.println(tmp);
		w.flush();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		ResponseToCreateCritters response = 
				UnpackJson.unpackResponseToCreateCritters(r);
		dumpResponse(r);
		return connection.getResponseCode();
	}

	/**
	 * Retrieve a critter with critter id {@code id} from Server, 
	 * @param id
	 * @return a ClientElement for Client
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public ClientElement retrieveCritter(int id) throws IOException, SyntaxError{
		URL l = new URL(url + "id?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		return UnpackJson.unpackCritter(r);
	}

	/**
	 * Create a Food or Rock in the world based on the instruction from 
	 * Client side
	 * @param pos
	 * @param amount
	 * @param type
	 * @throws IOException
	 */
	public void createFoodOrRock(ClientPosition pos, int amount, String type) 
			throws IOException{
		URL l = new URL(url + "create_entity?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "Created");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packRockorFood(pos.r, pos.c, amount, type);
		w.println(tmp);
		w.flush();
		//TODO:what should we do next if the response is negative?
	}

	/**
	 * Remove a critter from the world given the critter id {@code id}
	 * @param id
	 * @throws IOException
	 */
	public void removeCritter(int id) throws IOException {
		URL l = new URL(url + "critter/" + id + "?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("DELETE");
		connection.setRequestProperty("Content-Type", "No Content");
	}

	/**
	 * Create a new world
	 * @throws IOException 
	 */
	public void newWorld(String description) throws IOException {
		URL l = new URL(url + "CritterWorld/world?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "Created" );
		connection.setRequestMethod("POST");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packNewWorld(description);
		w.println(tmp);
		w.flush();
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
	public WorldState getStateOfWorld(int update_since, int from_col, 
			int from_row, int to_col, int to_row) throws IOException{
		URL l = new URL(url + "world?update_since=" + update_since 
				+ "&from_row=" + from_row + "&to_row=" + to_row 
				+ "&from_col=" + from_col + "&to_col=" + to_col
				+"&session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		WorldState state = UnpackJson.unpackWorldState(r);
		return state;
	}
	
	/**
	 * Advance the world by {@code n} step
	 * @throws IOException 
	 */
	public void advanceWorldByStep(int n) throws IOException {
		URL l = new URL(url + "CritterWorld/step?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "OK" );
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packAdvWorldCount(n);
		w.println(tmp);
		w.flush();
	}
	
	/**
	 * Let the world at servlet to run at rate {@code n}
	 * @param n
	 * @throws IOException 
	 */
	public void runWorldAtSpeed(int n) throws IOException {
		URL l = new URL(url + "CritterWorld/run?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "OK" );
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packAdvWorldCount(n);
		w.println(tmp);
		w.flush();
	}
	
	/** Read back output from the server. Could change to parse JSON... */
	void dumpResponse(BufferedReader r) throws IOException {
		for (;;) {
			String l = r.readLine();
			if (l == null) break;
			System.out.println("Read: " + l);
		}
	}

}
