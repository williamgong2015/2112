package client;

import api.json.JsonClasses.States;

/**
 * A world stored at client side for display and interaction with users
 *
 */
public class ClientWorld {

	public int current_timestep;
	public int current_version_number;
	public int update_since;
	public int rate;
	public String name;
	public int population;
	public int row;
	public int col;
	public int[] dead_critters;
	public States[] state;
}
