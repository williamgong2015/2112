package api;

import client.gui.GUIHex;
import client.world.ClientPosition;
import servlet.world.Position;

/**
 * Interpret Position interpretation at Servlet side to Position interpretation
 * at Client side back and forward
 */
public class PositionInterpreter {

	/**
	 * Convert a position of Hex coordinate in underlying world model 
	 * into a location of Cartesian coordinate in Critter World GUI
	 * @param pos
	 * @param worldCol
	 * @param worldRow
	 * @return
	 */
	public static ClientPosition serverToClient(Position pos, 
			int worldCol, int worldRow) {
    	return new ClientPosition(Position.getV(pos.getColumn(), pos.getRow()),
    			Position.getH(pos.getColumn(), pos.getRow()), 
    			Position.getV(worldCol, worldRow), 
    			GUIHex.HEX_SIZE);
	}
	
	/**
	 * Convert an array of position of Hex coordinate in underlying world model
	 * into a location of Cartesian coordinate in Critter World GUI
	 * @param pos
	 * @param worldCol
	 * @param worldRow
	 * @return
	 */
	public static ClientPosition[] multiServerToClient(Position[] pos, 
			int worldCol, int worldRow) {
		ClientPosition[] locs = new ClientPosition[pos.length];
		for (int i = 0; i < locs.length; ++i)
			locs[i] = serverToClient(pos[i], worldCol, worldRow);
		return locs;
	}
	
	/**
	 * Convert a location of Cartesian coordinate in Critter World GUI
	 * into a position of Hex coordinate in underlying world model 
	 * @param loc
	 * @return
	 */
	public static Position clientToServer(ClientPosition loc) {
		return new Position(Position.getC(loc.c, loc.r), 
				Position.getR(loc.c, loc.r));
	}
}
