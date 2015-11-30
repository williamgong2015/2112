package client.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.google.gson.Gson;

import api.JsonClasses;
import api.JsonClasses.CritterState;
import api.JsonClasses.FoodState;
import api.JsonClasses.NothingState;
import api.JsonClasses.RockState;
import api.JsonClasses.State;
import api.JsonClasses.WorldState;
import client.element.ClientElement;
import client.gui.GUIHex;
import client.world.HexToUpdate.HEXType;
import game.constant.IDX;
import game.utils.RandomGen;

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
	// an array of dead critters' id
	public ArrayList<Integer> dead_critters = new ArrayList<>();  
	
	public Hashtable<ClientPosition, ClientElement> board = new Hashtable<>();
	
	private HashMap<ClientPosition, HexToUpdate> hexToUpdate = new HashMap<>();
	
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
	}
	
	/**
	 * Copy Constructor
	 */
	public ClientWorld(ClientWorld w) {
		current_timestep = w.current_timestep;
		current_version_number = w.current_version_number;
		update_since = w.update_since;
		rate = w.rate;
		name = w.name;
		population = w.population;   
		row = w.row;
		col = w.col;
		dead_critters = w.dead_critters;
		board = w.board;
		hexToUpdate = w.hexToUpdate;
	}
	
	/**
	 * Update this {@code ClientWorld} object with the information in w
	 * store the elements need to be drawn on the GUI in 
	 * @param w
	 */
	public void updateWithWorldState(WorldState w) {
		current_timestep = w.current_timestep;
		current_version_number = w.current_version_number;
		update_since = w.update_since;
		rate = w.rate;
		name = w.name;
		population = w.population;   
		row = w.row;
		col = w.col;
		dead_critters.addAll(w.dead_critters);
		
		for (State s : w.state) {
			ClientPosition pos = 
					new ClientPosition(s.col, s.row, w.row, GUIHex.HEX_SIZE);
			switch (s.getType()) {
			case "critter":
				CritterState state = (CritterState) s;
				board.put(pos, new ClientElement(state));
				hexToUpdate.put(pos,  new HexToUpdate(HEXType.CRITTER, pos, 
						state.direction, state.mem[IDX.SIZE], 
						state.mem[IDX.POSTURE]));
				break;
			case "food":
				board.put(pos, new ClientElement((FoodState) s));
				hexToUpdate.put(pos,  new HexToUpdate(HEXType.FOOD, pos, 
						0, 0, 0));
				break;
			case "rock":
				board.put(pos, new ClientElement((RockState) s));
				hexToUpdate.put(pos,  new HexToUpdate(HEXType.ROCK, pos, 
						0, 0, 0));
				break;
			case "nothing":
				board.put(pos, new ClientElement((NothingState) s));
				hexToUpdate.put(pos,  new HexToUpdate(HEXType.EMPTY, pos, 
						0, 0, 0));
				break;
			default: 
				System.out.println("can't resolve type get from world states: "
						+ s.getType());
				break;
			}
		}
	}
	
	/**
	 * @return all the update to hex should be enforced after this turn
	 */
	public HashMap<ClientPosition, HexToUpdate> getHexToUpdate() {
		HashMap<ClientPosition, HexToUpdate> tmp = hexToUpdate;
		hexToUpdate = new HashMap<>();
		return tmp;
	}
	
	/**
	 * @return a string of information to display at simulation panel
	 */
	public String getWorldInfo() {
		ClientWorld tmp = new ClientWorld(this);
		Gson gson = new Gson();
		return gson.toJson(tmp, this.getClass());
	}
	
	/**
	 * Randomly get {@code number} of empty position in the world 
	 * @param number - the number of empty position need to get
	 * @return
	 */
	public ArrayList<ClientPosition> getListOfEmptyPosition(int number) {
		ArrayList<ClientPosition> result = new ArrayList<>();
		ClientWorld tmp = new ClientWorld(this);
		for(int i = 0;i < number;) {
			int row = RandomGen.randomNumber(tmp.row);
			int col = RandomGen.randomNumber(tmp.col);
			ClientPosition pos = new ClientPosition(col, row, tmp.row, 
					GUIHex.HEX_SIZE);
			if(tmp.board.get(pos) == null ||
					tmp.board.get(pos).type == JsonClasses.NOTHING) {
				result.add(pos);
				tmp.board.put(pos, new ClientElement());
				i++;
			}
		}
		return result;
	}
}
