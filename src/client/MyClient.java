package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import api.json.PackJson;
import api.json.UnpackJson;
import api.json.JsonClasses.*;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import servlet.element.Food;
import servlet.element.Rock;
import servlet.world.Position;
import servlet.world.World;

/**
 * Client to submit request and receive response
 */
public class MyClient {
	
	private final String url;
	private int session_id;
	private World world;

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
	public int createCritter(Critter c, ArrayList<Position> a, int number) throws IOException{
		URL l = new URL(url + "critters?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.connect();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp;
		if(a == null)
			tmp = PackJson.packCreateRandomPositionCritter(c, number);
		else
			tmp = PackJson.packCreateCritter(c, a);
		w.println(tmp);
		w.flush();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
//		ResponseToCreateCritters response = 
//				UnpackJson.unpackResponseToCreateCritters(r);
		dumpResponse(r);
		return connection.getResponseCode();
	}

	public Critter retrieveCritter(int id) throws IOException, SyntaxError{
		URL l = new URL(url + "id?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.connect();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		return new Critter(UnpackJson.unpackCritter(r));
	}

	public void createFoodOrRock(Position pos, int amount, String type) throws IOException{
		URL l = new URL(url + "create_entity?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		String tmp = PackJson.packRockorFood(pos.getRow(), pos.getColumn(), amount, type);
		w.println(tmp);
		w.flush();
		//TODO:what should we do next if the response is negative?
	}

	public void removeCritter(int id) throws IOException {
		URL l = new URL(url + "critter/" + id + "?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty(//TODO
				"Content-Type", "application/x-www-form-urlencoded" );
		connection.setRequestMethod("DELETE");
		connection.connect();
	}

	public void newWorld() {

	}

	public void getStateOfWorld(int update_since) throws IOException{
		if(update_since < 0) {
			URL l = new URL(url + "world?session_id=session_id");
			HttpURLConnection connection = (HttpURLConnection) l.openConnection();
			connection.connect();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			worldState state = UnpackJson.unpackWorldState(r);
			String name = state.name;
			int col = state.col;
			int row = state.row;
			world = new World(col, row, name);
			world.version_number = state.current_version_number;
			world.rate = state.rate;
			world.turns = state.current_timestep;
			for(States s : state.state) {
				switch(s.getType()) {
				case "rock":
					RockState rock = (RockState)s;
					world.setElemAtPosition(new Rock(), new Position(rock.col, rock.row));
					break;
				case "food":
					FoodState food = (FoodState)s;
					world.setElemAtPosition(new Food(food.value),
							new Position(food.col, food.row));
					break;
				case "critter":
					CritterState critter = (CritterState)s;
					try {
						Critter tmp = new Critter(critter);
						world.setElemAtPosition(tmp,
								new Position(critter.col, critter.row));
					} catch (SyntaxError e) {
						e.printStackTrace();
					}
					break;
				}
			}
		} else{
			//TODO
			URL l = new URL(url + "world?update_since=" + update_since 
					+"&session_id=" + session_id);
			HttpURLConnection connection = (HttpURLConnection) l.openConnection();
			connection.connect();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

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
