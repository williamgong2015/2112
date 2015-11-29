package client.world;

import java.util.Hashtable;

import api.JsonClasses.CritterState;
import api.JsonClasses.FoodState;
import api.JsonClasses.NothingState;
import api.JsonClasses.RockState;
import api.JsonClasses.State;
import api.JsonClasses.WorldState;
import client.element.ClientElement;
import client.gui.GUIHex;

/**
 * A world stored at client side for display and interaction with users
 *
 */
public class ClientWorld {

	public int current_timestep;
	public int current_version_number;
	public int update_since;
	public double rate;             // how fast the world run
	public String name;
	public int population;       // number of critter alive
	public int row;
	public int col;
	public int[] dead_critters;  // an array of dead critters' id
	
	public Hashtable<ClientPosition, ClientElement> world = new Hashtable<>();
	
	/**
	 * Create a client side world with a {@code WorldState} object
	 * @param w
	 */
	public ClientWorld(WorldState w) {
		current_timestep = w.current_timestep;
		current_version_number = w.current_version_number;
		update_since = w.update_since;
		rate = w.rate;
		name = w.name;
		population = w.population;   
		row = w.row;
		col = w.col;
		dead_critters = w.dead_critters;
		// have to use this safe down cast to visit corresponding field
		// didn't put CritterState, FoodState ... all in one class for the ease
		// of GSON packing. 
		for (State s : w.state) {
			ClientPosition pos = 
					new ClientPosition(s.col, s.row, w.row, GUIHex.HEX_SIZE);
			switch (s.getType()) {
			case "critter":
				world.put(pos, new ClientElement((CritterState) s));
				break;
			case "food":
				world.put(pos, new ClientElement((FoodState) s));
				break;
			case "rock":
				world.put(pos, new ClientElement((RockState) s));
				break;
			case "nothing":
				world.put(pos, new ClientElement((NothingState) s));
				break;
			default: 
				System.out.println("can't resolve type get from world states: "
						+ s.getType());
				break;
			}
		}
	}
	
}
