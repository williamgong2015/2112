package servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import exceptions.SyntaxError;
import json.JsonClasses;
import json.JsonClasses.*;
import json.PackJson;
import json.UnpackJson;
import simulate.Critter;
import simulate.Position;
import simulate.World;

/**
 * Servlet implementation class
 * 
 * The max client number is MAX_CAPACITY
 */
@WebServlet("/") /* relative URL path to servlet (under package name 'demoServlet'). */
public class Servlet extends HttpServlet {
	// define password for different level of user
	private static final String ADMIN_PW = "admin";
	private static final String WRITER_PW = "writer";
	private static final String READER_PW = "reader";
	private static final int ADMIN_LV = 1;
	private static final int WRITER_LV = 2;
	private static final int READER_LV = 3;
	private static final int MAX_CAPACITY = 2 ^ 30;

	// the base url of the servlet are goint received
	private static final String BASE_URL = 
			"/2112/servlet/servlet.Servlet/";

	// use synchronized java collection (mapping session id to level)
	private Hashtable<Integer, Integer> sessionIdTable = new Hashtable<>();

	private World world;
	
	/**
	 * Handle get session id request from client
	 * {@code session_id} is a positive integer or 0
	 * 
	 * @return -1 if the password doesn't match
	 *         -2 if current users have reach the max capacity
	 *         positive number or 0 if succeed
	 */
	private int handleGetSessionID(int level, String password) {
		if (sessionIdTable.size() > MAX_CAPACITY)
			return -2;
		boolean succeed = false;
		switch (level) {
		case ADMIN_LV:
			if (password.equals(ADMIN_PW)) 
				succeed = true;
			break;
		case WRITER_LV:
			if (password.equals(WRITER_PW)) 
				succeed = true;
			break;
		case READER_LV:
			if (password.equals(READER_PW)) 
				succeed = true;
			break;
		}
		if (succeed) {
			int tmp = Math.abs(util.RandomGen.randomNumber());
			while (sessionIdTable.containsKey(tmp)) {
				tmp = Math.abs(util.RandomGen.randomNumber());
			}
			sessionIdTable.put(tmp, level);
			return tmp;
		}
		return -1;
	}

	/**
	 * Handle GET request
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Gson gson = new Gson();
		response.addHeader("Content-Type", "application/json");
		PrintWriter w = response.getWriter();
		// it is the URI right after 'localhost:8080:'
		String requestURI = request.getRequestURI();


		//flush the stream to make sure it actually gets written
		w.flush();
		//close the output stream
		w.close();
		//send a 200 status indicating success
		response.setStatus(200);
	}

	/**
	 * Handle POST request.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.addHeader("Content-Type", "text/plain");
		PrintWriter w = response.getWriter();
		BufferedReader r = request.getReader();
		String requestURI = 
				request.getRequestURI().substring(BASE_URL.length());
//		w.append("POST URI: " + requestURI + "\r\n"); // for debugging

		//login
		if (requestURI.startsWith("login")) {
			response.addHeader("Content-Type", "application/json");
			Password input = UnpackJson.unpackPassword(r);
			int session_id = handleGetSessionID(input.level, input.password);
			w.println(PackJson.packSessionID(session_id));
			response.setStatus(200);
		} 
		//create a critter
		if (requestURI.startsWith("critter")) {
			response.addHeader("Content-Type", "application/json");
			String s = requestURI.substring("critters?session_id=".length());
			int id = Integer.parseInt(s);
			if(sessionIdTable.get(id) == null ||
					sessionIdTable.get(id) == READER_LV)
				response.setStatus(401);
			else {
				r.mark(2);
				if((char)r.read() == 's') {
					r.reset();
					CreateCritter c = 
							UnpackJson.unpackCreateCritter(r);
					try {
						
						Position[] pos = c.positions;
						for(Position p : pos) {
							Critter critter = new Critter(c);
							critter.setPosition(p);
							critter.ID = ++world.CritterID;
							//TODO
						}
					} catch (SyntaxError e) {
						System.out.println("Wrong syntax");
					}
				} else {
					r.reset();
					
				}
				response.setStatus(201);
			}
		}


		//check the url parameters (the ?a=b&c=d at the end)
//		Map<String, String[]> parameterNames = request.getParameterMap();
//		for (Entry<String, String[]> entry : parameterNames.entrySet()) {
//			switch (entry.getKey()) {
//			case "other_param":
//				otherParameter = Integer.parseInt(entry.getValue()[0]);
//				w.println("Changed other_param to " + otherParameter);
//				break;
//			}
//		}
		w.flush();
		w.close();
	}

}
