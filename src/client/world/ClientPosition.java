package client.world;

import api.PositionInterpreter;

/**
 * Position of a hex (column,row) defined by its centroid in the ClientWorld, 
 * all the following x, y position are using Cartesian coordinate
 * 
 * e.g. first number is x, second number is y
 *      (1,3)     (3,3)
 * (0,2)     (2,2)     (4,2)
 *      (1,1)     (3,1)
 * (0,0)     (2,0)     (4,0)
 * 
 * Class Invariant:
 * 1. even x can only have even y, odd x can only have odd y
 *    vice versa
 * 2. x position and y position should be larger than 0 and within the world
 */
public class ClientPosition {

	public int x;
	public int y;
	public final double xPos;  // position of the centroid along x axis
	public final double yPos;  // position of the centroid along y axis
	private final static double SQRT_THREE = Math.sqrt(3);
	
	/**
	 * Constructor: initialize the a location of hex in the world
	 * @param column - column index in the world, in Hex Coordinate
	 * @param row - row index in the world, in Hex Coordinate
	 * @param worldRow - total row in the world, to compute offset 
	 *                   from top, in Hex Coordinate
	 * @param OFFSET - size of each hex 
	 *                 (defined by the distance from centroid to vertex) 
	 */
	public ClientPosition(int column, int row, int worldRow, int worldCol, double OFFSET) {
		this.x = PositionInterpreter.getX(column, row);
		this.y = PositionInterpreter.getY(column, row);
		xPos = getXPos(x, y, OFFSET);
		yPos = getYPos(y, PositionInterpreter.getY(worldCol, worldRow), OFFSET);
	}
	
	/**
	 * Create a Client Position with x, y and worldY all in Cartesian Coordinate
	 * @param x
	 * @param y
	 * @param Y
	 * @param OFFSET
	 */
	public ClientPosition(int x, int y, int worldY, double OFFSET) {
		this.x = x;
		this.y = y;
		xPos = getXPos(x, y, OFFSET);
		yPos = getYPos(y, worldY, OFFSET);
	}

	/**
	 * Get the x position give column and row index of a location 
	 * @param x - x, starting from the left, in Cartesian Coordinate
	 * @param y - y, starting from the bottom, in Cartesian Coordinate
	 * @param OFFSET - size of the hex
	 *                 (defined by the distance from centroid to vertex) 
	 * @return x position of the location (should be larger than or equal to 0)
	 *         -1 if the given index is invalid 
	 */
	private static double getXPos(int x, int y, double OFFSET) {
		// class invariant
		if (y % 2 != x % 2 || x < 0 || y < 0) 
			return -1;
		if (y % 2 == 0) 
			return x/2 * 3 * OFFSET + OFFSET;
		else
			return x/2 * 3 * OFFSET + 2.5 * OFFSET;
	}
	
	/**
	 * Get the y position given the row index and the total row in the world
	 * @param y in Cartesian Coordinate
	 * @param worldY in Cartesian Coordinate
	 * @param OFFSET - size of the hex
	 *                 (defined by the distance from centroid to vertex) 
	 * @return y position of the location (should be larger than or equal to 0)
	 *         -1 if the given index is invalid
	 */
	private static double getYPos(int y, int worldY, double OFFSET) {
		return (worldY - y) * OFFSET * SQRT_THREE / 2 ;
	}
	
	/**
	 * @param x - column of the location in Cartesian Coordinate
	 * @param y - row of the location in Cartesian Coordinate
	 * @return the id of hex defined by the row and column of location
	 */
	public static String getID(int x, int y) {
		return "" + x + "," + y;
	}
	
	/**
	 * Get the column/row of the location given the String representation of ID
	 * @param ID
	 * @param parseRow - {@code true} if need to parse row
	 *                   {@code false} if need to parse column
	 * @return x/y of the location (should be larger than or equal to 0)
	 *         -1 if the given {@code ID} is illegal
	 */
	private static int parseXYFromID(String ID, boolean parseRow) {
		String[] token = ID.split(",");
		if (token.length != 2) {
			System.out.println("illegal id: " + ID);
			return -1;
		}
		try {
			int x = Integer.parseInt(token[0]);
			int y = Integer.parseInt(token[1]);
			if (y < 0)
				return -1;
			if (parseRow)
				return y;
			return x;
		} catch (Exception e) {
			System.out.println("illegal id: " + ID);
			return -1;
		}
	}
	
	/**
	 * Get the y of the location given the String representation of ID
	 * @param ID
	 * @return y number of the location (should be larger than or equal to 0)
	 * 		   in Cartesian Coordinate
	 *         -1 if the given {@code ID} is illegal 
	 */
	public static int parseYFromID(String ID) {
		return parseXYFromID(ID, true);
	}
	
	/**
	 * Get the x of the location given the String representation of ID
	 * @param ID
	 * @return x of the location (should be larger than or equal to 0)
	 *         in Cartesian Coordinate
	 *         -1 if the given {@code ID} is illegal
	 */
	public static int parseXFromID(String ID) {
		return parseXYFromID(ID, false);
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClientPosition))
			return false;
		ClientPosition tmp = (ClientPosition) obj;
		return tmp.x == this.x && tmp.y == this.y;
	}
	
	@Override
	public int hashCode() {
		return 2187 * this.x + this.y;
	}
}
