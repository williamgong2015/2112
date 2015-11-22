package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import json.JsonClasses;
import json.PackJson;
import json.UnpackJson;

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
	
	public void lisAllCritters() throws IOException {
		URL l = new URL(url + "critters?session_id" + session_id);
		HttpURLConnection connection = (HttpURLConnection) l.openConnection();
		connection.connect();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		JsonClasses.listOfCritters al = UnpackJson.unpackListOfCritters(r);
	}
}
