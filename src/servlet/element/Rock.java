package servlet.element;

import java.io.IOException;
import java.util.HashMap;

import api.HexToUpdate;
import api.HexToUpdate.HEXType;
import game.exceptions.SyntaxError;
import servlet.world.Position;
import servlet.world.World;

/**
 * A rock object in the world 
 *
 */
public class Rock extends Element {

	public Rock() {
		super("ROCK");
	}
	
	public String toString() {
		return "this is a rock";
	}
	
	/**
	 * Insert a rock to a specified location into the world
	 * @param world
	 * @param pos
	 * @param session_id - id of user who insert the critter
	 * @return true if the insertion succeed
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public static HashMap<Position, HexToUpdate> 
		insertRockIntoWorld(World world, Position pos, int session_id
				) throws IOException, SyntaxError {
		HashMap<Position, HexToUpdate> result = new HashMap<>();
		Rock newRock = new Rock();
		if(world.checkPosition(pos) &&
    			world.getElemAtPosition(pos) == null) {
			result.put(pos, new HexToUpdate(HEXType.ROCK, pos, 0, 0, 0));
			world.setElemAtPosition(newRock, pos);
		}
		return result;
	}
	

}
