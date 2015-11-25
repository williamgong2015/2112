package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.PackJson;
import api.UnpackJson;
import api.JsonClasses.*;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import servlet.world.Position;
import servlet.world.World;

/**
 * Servlet implementation class
 * 
 * The max client number is MAX_CAPACITY
 */
@WebServlet("/") /* relative URL path to servlet (under package name 'demoServlet'). */
public class Servlet extends HttpServlet {
	/**
	 * Default serialVersion UID
	 */
	private static final long serialVersionUID = 1L;
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
			int tmp = Math.abs(game.utils.RandomGen.randomNumber());
			while (sessionIdTable.containsKey(tmp)) {
				tmp = Math.abs(game.utils.RandomGen.randomNumber());
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
//		Gson gson = new Gson();
		response.addHeader("Content-Type", "application/json");
		PrintWriter w = response.getWriter();
		// it is the URI right after 'localhost:8080:'
//		String requestURI = request.getRequestURI();


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
		int session_id = -1;
		//check the url parameters (the ?a=b&c=d at the end)
		Map<String, String[]> parameterNames = request.getParameterMap();
		for (Entry<String, String[]> entry : parameterNames.entrySet()) {
			switch (entry.getKey()) {
			case "session_id":
				session_id = Integer.parseInt(entry.getValue()[0]);
				w.println("Changed got post request session_id: " + session_id);
				break;
			}
		}
		
//		w.append("POST URI: " + requestURI + "\r\n"); // for debugging

		
		/**
		 * Handle get session id request 
		 * Effect: respond with 200 and write back {@code session_id} 
		 *         if succeed
		 *         respond with 401 "Unauthorized" if failed
		 */
		if (requestURI.startsWith("login")) {
			response.addHeader("Content-Type", "application/json");
			Password input = UnpackJson.unpackPassword(r);
			int session_id_new = handleGetSessionID(input.level, input.password);
			w.println(PackJson.packSessionID(session_id_new));
			if (session_id_new == -1)
				response.setStatus(401);
			else
				response.setStatus(200);
		} 
		/**
		 * Handle create a critter request 
		 * Effect: write back {@code species_id} and {@code ids} of newly 
		 *         created critters and respond 201 if succeed
		 *         respond 401 if the {@code session_id} is not authorized
		 */
		if (requestURI.startsWith("critter")) {
			response.addHeader("Content-Type", "application/json");
			if(sessionIdTable.get(session_id) != ADMIN_LV ||
					sessionIdTable.get(session_id) != WRITER_LV)
				response.setStatus(401);
			else {
				world.version_number++;
				r.mark(2);
				char tmp = (char)r.read();
				if(tmp  == 's') {
					w.append("create critter command start with: " + tmp);
					r.reset();
					CreateCritter c = 
							UnpackJson.unpackCreateCritter(r);
					try {
						Position[] pos = c.positions;
						for(Position p : pos) {
							Critter critter = new Critter(c);
							critter.setPosition(p);
							world.addCritter(critter, p);
							//TODO  we need send back some info
						}
					} catch (SyntaxError e) {
						System.out.println("Wrong syntax");
					}
				} else {
					w.append("the create critter command start with: " + tmp);
					r.reset();
					CreateRandomPositionCritter c = 
							UnpackJson.unpackCreateRandomPositionCritter(r);
					world.setCritterAtRandomPosition(c);
					//TODO send info back
				}
				response.setStatus(201);
			}
		}
		//create a new world
		if(requestURI.startsWith("world")) {
			//TODO
			
		}
		


		w.flush();
		w.close();
	}

}
