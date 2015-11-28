package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import api.PackJson;
import api.UnpackJson;
import api.JsonClasses.*;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import servlet.element.Food;
import servlet.element.Rock;
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

	private Gson gson = new Gson();

	/**
	 * Handle DElETE request
	 */
	protected void doDelete(HttpServletRequest request, 
			HttpServletResponse response) {
		// process URI and parameters in it
		String requestURI = 
				request.getRequestURI().substring(BASE_URL.length());
		// default session_id (not admin, writer, reader)
		int session_id = -1; 
		Map<String, String[]> parameterNames = request.getParameterMap();
		for (Entry<String, String[]> entry : parameterNames.entrySet()) {
			switch (entry.getKey()) {
			case "session_id":
				session_id = Integer.parseInt(entry.getValue()[0]);
				break;
			}
		}
		
		try {
			if (requestURI.startsWith("CritterWorld/critter")) {
				String subURI = "CritterWorld/critter/";
				String idStr = requestURI.substring(subURI.length(), 2);
				int id = Integer.parseInt(idStr);
				handleRemoveCritter(request, response, session_id, id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Handle GET request
	 */
	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) {
		// process URI and parameters in it
		String requestURI = 
				request.getRequestURI().substring(BASE_URL.length());
		// default session_id (not admin, writer, reader)
		int session_id = -1; 
		Map<String, String[]> parameterNames = request.getParameterMap();
		for (Entry<String, String[]> entry : parameterNames.entrySet()) {
			switch (entry.getKey()) {
			case "session_id":
				session_id = Integer.parseInt(entry.getValue()[0]);
				break;
			}
		}
		
		try {
			if (requestURI.startsWith("CritterWorld/critter/")) {
				String subURI = "CritterWorld/critter/";
				String idStr = requestURI.substring(subURI.length(), 2);
				int id = Integer.parseInt(idStr);
				handleRetrieveCritter(request, response, session_id, id);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Handle POST request.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// process URI and parameters in it
		String requestURI = 
				request.getRequestURI().substring(BASE_URL.length());
		// default session_id (not admin, writer, reader)
		int session_id = -1; 
		Map<String, String[]> parameterNames = request.getParameterMap();
		for (Entry<String, String[]> entry : parameterNames.entrySet()) {
			switch (entry.getKey()) {
			case "session_id":
				session_id = Integer.parseInt(entry.getValue()[0]);
				break;
			}
		}

		try {
			if (requestURI.startsWith("login")) {
				handleGetSessionID(request, response);
			} 
			else if (requestURI.startsWith("critter")) {
				handleCreateCritter(request, response, session_id);
			}
			else if (requestURI.startsWith("world")) {
				handleCreateNewWorld(request, response, session_id);
			}
			else if (requestURI.startsWith("CritterWorld/"
					+ "world/create_entity")) {
				handleCreateEntity(request, response, session_id);
			}
			else if (requestURI.startsWith("CritterWorld/step")) {
				handleAdvWorldByStep(request, response, session_id);
			}
			else if (requestURI.startsWith("CritterWorld/run")) {
				handleRunWorld(request, response, session_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Retrieve critter information 
	 * @param request
	 * @param response
	 * @param session_id
	 * @param id
	 * Write:   critter information
	 *          "Not Acceptable" if the world hasn't been initialized or
	 *                           the critter doesn't exist
	 * Respond: 200 if succeed
	 *          406 if the world hasn't been initialized or the critter 
	 *              doesn't exist
	 * @throws IOException 
	 */
	private void handleRetrieveCritter(HttpServletRequest request, 
			HttpServletResponse response, int session_id, int id) 
					throws IOException {
		response.addHeader("Content-Type", "application/json");
		PrintWriter w = response.getWriter();
		if (world == null) {
			w.println("Not Acceptable");
			response.setStatus(406);
			w.flush();
			w.close();
		}
		// scan through the array list that store critters to find the 
		// critter to delete
		for (Critter c : world.order) {
			if (c.ID != id) 
				continue;
			if (sessionIdTable.get(session_id) == ADMIN_LV ||
				(sessionIdTable.get(session_id) == WRITER_LV && 
				c.session_id == session_id)) {
				// remove critter from array list, map and increase version num
				
				response.setStatus(204);
				w.flush();
				w.close();
				return;
			}
			else {
				
			}
			break;
		}
	}
	
	/**
	 * Removes a specific critter if session_id has permission to remove it.
	 * @param request
	 * @param response
	 * @param session_id
	 * @param id
	 * Write:   "Unauthorized" if the session_id doesn't have access
	 *          "Not Acceptable" if the world has not been initialized
	 * Respond: 401 if the session_id doesn't have access to it
	 * 			406 if the world has not been initialized
	 *          204 if succeed
	 * @throws IOException 
	 */
	private void handleRemoveCritter(HttpServletRequest request, 
			HttpServletResponse response, int session_id, int id) 
			throws IOException {
		response.addHeader("Content-Type", "No Content");
		PrintWriter w = response.getWriter();
		if (world == null) {
			w.println("Not Acceptable");
			response.setStatus(406);
			w.flush();
			w.close();
		}
		// scan through the array list that store critters to find the 
		// critter to delete
		for (Critter c : world.order) {
			if (c.ID != id) 
				continue;
			if (sessionIdTable.get(session_id) == ADMIN_LV ||
				(sessionIdTable.get(session_id) == WRITER_LV && 
				c.session_id == session_id)) {
				// remove critter from array list, map and increase version num
				world.removeCritter(c);  
				world.removeElemAtPosition(c.getPosition());
				response.setStatus(204);
				w.flush();
				w.close();
				return;
			}
			break;
		}
		w.println("Unauthorized");
		response.setStatus(401);
		w.flush();
		w.close();
		return;
	}

	/**
	 * Run the world continuously/change the rate of the rate
	 * @param request
	 * @param response
	 * @param session_id
	 * Write:   "Unauthorized" if the session_id doesn't have access
	 * 		    "Not Acceptable" if the world has not been initialized
	 *                           of a negative rate is specified
	 * Respond: 401 if the session_id doesn't have access
	 *          406 if the world has not been initialized
	 *              or a negative rate is specified
	 *          200 if succeed
	 * @throws IOException 
	 *        
	 */
	private void handleRunWorld(HttpServletRequest request, HttpServletResponse response, int session_id) throws IOException {
		response.addHeader("Content-Type", "OK");
		PrintWriter w = response.getWriter();
		BufferedReader r = request.getReader();
		if (sessionIdTable.get(session_id) != ADMIN_LV ||
				sessionIdTable.get(session_id) != WRITER_LV) {
			w.println("Unauthorized");
			response.setStatus(401);
			w.flush();
			w.close();
			return;
		}
		int rate = gson.fromJson(r, AdvanceWorldRate.class).rate;
		if (rate < 0 || world == null) {
			w.println("Not Acceptable");
			response.setStatus(406);
			w.flush();
			w.close();
			return;
		}
		world.rate = rate;
		response.setStatus(200);
		w.flush();
		w.close();
		return;
	}

	/**
	 * Advance world by {@code count} steps
	 * @param request
	 * @param response
	 * @param session_id
	 * Require: {@code count} is larger than 0
	 * Write:   "Not Acceptable" if the world has not been initialized 
	 *                           or entity could not be created 
	 *          "Unauthorized" if simulation is running continuously
	 * Respond: 401 if the session id doesn't have the write or admin access
	 *          406 if world has not been intialized
	 *              or simulation is running continuously
	 *          200 if succeed 
	 * @throws IOException 
	 */
	private void handleAdvWorldByStep(HttpServletRequest request, 
			HttpServletResponse response, int session_id) throws IOException {
		response.addHeader("Content-Type", "OK");
		PrintWriter w = response.getWriter();
		BufferedReader r = request.getReader();
		if (sessionIdTable.get(session_id) != ADMIN_LV ||
				sessionIdTable.get(session_id) != WRITER_LV) {
			w.println("Unauthorized");
			response.setStatus(401);
			w.flush();
			w.close();
			return;
		}
		if (world == null || world.rate != 0) {
			w.println("Not Acceptable");
			response.setStatus(406);
			w.flush();
			w.close();
		}
		int count = gson.fromJson(r, AdvanceWorldCount.class).count;
		for (int i = 0; i < count; ++i) 
			world.lapse();
		response.setStatus(200);
		w.flush();
		w.close();
	}

	/**
	 * Create a food or rock in to an existing world
	 * @param request
	 * @param response
	 * @param session_id
	 * Write:   "Not Acceptable" if the world has not been initialized 
	 *                           or entity could not be created 
	 *          "Unauthorized" if session id doesn't have access
	 *          "OK" if succeed
	 * Respond: 401 if the session id doesn't have the write or admin access
	 *          406 if the entity can not be created at the specific location
	 *          201 if succeed
	 * @throws IOException 
	 */
	private void handleCreateEntity(HttpServletRequest request, 
			HttpServletResponse response, int session_id) throws IOException {
		response.addHeader("Content-Type", "Created");
		PrintWriter w = response.getWriter();
		BufferedReader r = request.getReader();
		if (sessionIdTable.get(session_id) != ADMIN_LV ||
				sessionIdTable.get(session_id) != WRITER_LV) {
			w.println("Unauthorized");
			response.setStatus(401);
			w.flush();
			w.close();
			return;
		}
		FoodOrRock foodOrRock = UnpackJson.unpackRockorFood(r);
		Position pos = new Position(foodOrRock.col, foodOrRock.row);
		// can't create entity outside the world or of the position has been
		// taken
		if (world == null || !world.checkPosition(pos) || 
				world.getElemAtPosition(pos) != null) {
			w.println("Not Acceptable");
			response.setStatus(406);
			w.flush();
			w.close();
			return;
		}
		if (foodOrRock.type == "food")
			world.setElemAtPosition(new Food(foodOrRock.amount), pos);
		else if (foodOrRock.type == "rock")
			world.setElemAtPosition(new Rock(), pos);
		else {
			w.println("Not Acceptable type: " + foodOrRock.type);
			response.setStatus(406);
			w.flush();
			w.close();
			return;
		}
		w.println("OK");
		response.setStatus(201);
		w.flush();
		w.close();
	}

	/**
	 * Create a new world
	 * Require: session_id has administrator access
	 * @param request
	 * @param response
	 * @param session_id
	 * Write:   "OK"
	 *          "Unauthorized" if session id doesn't have access
	 * Respond: 401 if failed
	 *          201 if succeed
	 * @throws IOException 
	 */
	private void handleCreateNewWorld(HttpServletRequest request, 
			HttpServletResponse response, int session_id) throws IOException {
		response.addHeader("Content-Type", "Created");
		PrintWriter w = response.getWriter();
		BufferedReader r = request.getReader();
		if (sessionIdTable.get(session_id) != ADMIN_LV) {
			w.println("Unauthorized");
			response.setStatus(401);
			w.flush();
			w.close();
			return;
		}
		CreateNewWorld newWorld = UnpackJson.unpackCreateNewWorld(r);
		world = new World();
		world.setName(newWorld.description);
		response.setStatus(201);
		w.println("OK");
		w.flush();
		w.close();
	}

	/**
	 * Handle get session id request from client
	 * {@code session_id} is a positive integer or 0
	 * Write:   0 if failed, new generated positive session_id if succeed
	 * Respond: 401 if the password doesn't match
	 *          406 if current users have reach the max capacity
	 *          200 if succeed
	 * @throws IOException 
	 */
	private void handleGetSessionID(HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		response.addHeader("Content-Type", "application/json");
		PrintWriter w = response.getWriter();
		BufferedReader r = request.getReader();
		Password input = UnpackJson.unpackPassword(r);
		int level = input.level;
		String password = input.password;
		if (sessionIdTable.size() > MAX_CAPACITY) {
			response.setStatus(406);
			w.println("users number reach max capacity");
			w.flush();
			w.close();
			return;
		}
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
			w.println(PackJson.packSessionID(tmp));
			response.setStatus(200);
		}
		else {
			w.println(PackJson.packSessionID(0));
			response.setStatus(401);
		}
		w.flush();
		w.close();
	}

	/**
	 * Handle create critter request
	 * @param request
	 * @param response
	 * @param session_id
	 * @throws IOException
	 * Write:   {@code species_id} and {@code ids} of newly created critters
	 * Respond: 401 if the {@code session_id} is not authorized
	 *          406 if the world hasn't been created
	 *          201 if succeed
	 * @throws SyntaxError 
	 */
	private void handleCreateCritter(HttpServletRequest request, 
			HttpServletResponse response, int session_id) throws IOException, 
			SyntaxError {
		response.addHeader("Content-Type", "application/json");
		PrintWriter w = response.getWriter();
		BufferedReader r = request.getReader();
		if (sessionIdTable.get(session_id) != ADMIN_LV ||
				sessionIdTable.get(session_id) != WRITER_LV) {
			w.println("wrong session_id");
			response.setStatus(401);
			w.flush();
			w.close();
			return;
		}
		if (world == null) {
			w.println("world hasn't been created");
			response.setStatus(406);
			w.flush();
			w.close();
			return;
		}
		world.version_number++;
		r.mark(10);
		char tmp = (char)r.read();
		// skip space, bracket and quota
		while (tmp >= 'z' || tmp <= 'a')
			tmp = (char) r.read();
		// has specified specific position
		ArrayList<Integer> idTmp = new ArrayList<>();
		String species_id;
		if (tmp  == 's') {
			w.println("create critter command start with: " + tmp + "\n");
			r.reset();
			CreateCritter c = 
					UnpackJson.unpackCreateCritter(r);
			species_id = c.species_id;
			Position[] pos = c.positions;
			for(Position p : pos) {
				Critter critter = new Critter(c, world.critterIDCount++,
						session_id);
				critter.setPosition(p);
				world.addCritter(critter, p);
				idTmp.add(critter.ID);
			}
		} 
		// hasn't specified specific position
		else {
			w.println("the create critter command start with: " + tmp + "\n");
			r.reset();
			CreateRandomPositionCritter c = 
					UnpackJson.unpackCreateRandomPositionCritter(r);
			species_id = "critters " + world.critterIDCount + " - " + 
					(world.critterIDCount + c.num);
			w.println("created critter at server: " + gson.toJson(c));
			idTmp = world.setCritterAtRandomPosition(c, species_id,
					session_id);
		}
		// write back result
		int[] ids = new int[idTmp.size()];
	    for (int i=0; i < ids.length; i++)
	    {
	        ids[i] = idTmp.get(i).intValue();
	    }
		w.println(PackJson.packResponseToCreateCritters(species_id, ids));
		response.setStatus(201);
		w.flush();
		w.close();
	}

}
