package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import json.JsonClasses;
import json.PackJson;
import json.UnpackJson;
import simulate.Critter;
import simulate.Position;

public class myClient {
	
	private final String url;
	private int session_id;
	public myClient(String u) {
		url = u;
	}
	
	public void logIn(String password, int level) throws IOException {
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
		session_id = UnpackJson.unpackSessionID(json);
	}
	//TODO
	public JsonClasses.listOfCritters lisAllCritters() throws IOException {
		URL l = new URL(url + "critters?session_id" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.connect();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		return UnpackJson.unpackListOfCritters(r);
	}
	//TODO
	public void createCritter(Critter c, ArrayList<Position> a, int number) throws IOException{
		URL l = new URL(url + "critters?session_id" + session_id);
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
		JsonClasses.ResponseToCreateCritters response = 
				UnpackJson.unpackResponseToCreateCritters(r);
	}
	
	public JsonClasses.GetCritter retrieveCritter(int id) throws IOException{
		URL l = new URL(url + "id?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.connect();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		return UnpackJson.unpackCritter(r);
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
}
