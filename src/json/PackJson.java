package json;

import com.google.gson.Gson;

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
	 * Used by Servlet: pack the session id before send it back to client
	 */
	public static String packSessionID(int id) {
		JsonClasses.SessionID tmp = new JsonClasses.SessionID();
		tmp.session_id  = id;
		return gson.toJson(tmp, JsonClasses.SessionID.class);
	}
	
	
	/**
	 * Unit test
	 */
	public static void main(String[] args) {
		System.out.println(packSessionID(10));
	}
}
