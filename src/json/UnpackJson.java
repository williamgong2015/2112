package json;

import com.google.gson.Gson;

/**
 * A class contains static methods to create Java objects from JSON object.
 * The format of JSON string is defined at {@link http://docs.cs2112fall2015.
 * apiary.io/#reference/world/world-state/get-the-state-of-the-world}
 * 
 * It is used by both {@code Client} and {@code Servlet}
 * 
 */
public class UnpackJson {
	
	private static Gson gson = new Gson();

	/**
	 * @return session id
	 */
	public static int unpackSessionID(String json) {
		JsonClasses.SessionID tmp = gson.fromJson(json, 
				JsonClasses.SessionID.class);
		return tmp.session_id;
	}
}
