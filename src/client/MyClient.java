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
	private ClientWorld world;

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
	public int logIn(int level, String password) {
		try {
			URL l = new URL(url + "login");
			HttpURLConnection connection = (HttpURLConnection) l.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
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

	//TODO
	public ArrayList<CritterState> lisAllCritters() throws IOException {
		URL l = new URL(url + "critters?session_id" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.connect();
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
//		ResponseToCreateCritters response = 
//				UnpackJson.unpackResponseToCreateCritters(r);
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
		connection.connect();
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
		connection.setRequestProperty(//TODO
				"Content-Type", "No Content" );
		connection.setRequestMethod("DELETE");
		connection.connect();
	}

	/**
	 * Create a new world
	 */
	public void newWorld() {

	}

	/**
	 * Get the update of the world since {@code update_sice} from server
	 * and store it at the Client side
	 * If {@code update_sice} is less than 0, return the entire state of the 
	 * world. 
	 * @param update_since
	 * @throws IOException
	 */
	public void getStateOfWorld(int update_since) throws IOException{
		if(update_since < 0) {
			URL l = new URL(url + "world?session_id=session_id");
			HttpURLConnection connection = (HttpURLConnection) l.openConnection();
			connection.connect();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			WorldState state = UnpackJson.unpackWorldState(r);
			world = new ClientWorld(state);
		} else {
			//TODO
			URL l = new URL(url + "world?update_since=" + update_since 
					+"&session_id=" + session_id);
			HttpURLConnection connection = (HttpURLConnection) l.openConnection();
			connection.connect();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			WorldState state = UnpackJson.unpackWorldState(r);
			world = new ClientWorld(state);
		}
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
