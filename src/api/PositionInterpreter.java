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
    	return new ClientPosition(getY(pos.getColumn(), pos.getRow()),
    			getX(pos.getColumn(), pos.getRow()), 
    			getY(worldCol, worldRow), 
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
		return new Position(getC(loc.x, loc.y), 
				getR(loc.x, loc.y));
	}
	
	/**
	 * Coordinate transform between Cartesian coordinate and Hex coordinate
	 * Formula:  y = 2r-c, x = c, 
	 *           r = (x+y+1)/2, c = x
	 */
	public static int getR(int x, int y) {
		return (x+y+1)/2;
	}
	public static int getC(int x, int y) {
		return x;
	}
	public static int getX(int c, int r) {
		return 2*r-c;
	}
	public static int getY(int c, int r) {
		return c;
	}
}
