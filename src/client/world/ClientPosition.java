package client.world;


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

	public final int c;  // column
	public final int r;  // row
	public final double xPos;  // position of the centroid along x axis
	public final double yPos;  // position of the centroid along y axis
	private final static double SQRT_THREE = Math.sqrt(3);
	
	/**
	 * Constructor: initialize the a location of hex in the world
	 * @param column - column index in the world, starting from left
	 * @param row - row index in the world, starting from bottom
	 * @param worldRow - total row in the world, to compute offset from top
	 * @param OFFSET - size of each hex 
	 *                 (defined by the distance from centroid to vertex) 
	 */
	public ClientPosition(int column, int row, int worldRow, double OFFSET) {
		this.c = column;
		this.r = row;
		xPos = getXPos(column, row, OFFSET);
		yPos = getYPos(row, worldRow, OFFSET);
	}
	
	/**
	 * Get the x position give column and row index of a location 
	 * @param c - column, starting from the left
	 * @param r - row, starting from the bottom
	 * @param OFFSET - size of the hex
	 *                 (defined by the distance from centroid to vertex) 
	 * @return x position of the location (should be larger than or equal to 0)
	 *         -1 if the given index is invalid 
	 */
	public static double getXPos(int c, int r, double OFFSET) {
		// class invariant
		if (r % 2 != c % 2 || c < 0 || r < 0) 
			return -1;
		if (r % 2 == 0) 
			return c/2 * 3 * OFFSET + OFFSET;
		else
			return c/2 * 3 * OFFSET + 2.5 * OFFSET;
	}
	
	/**
	 * Get the y position given the row index and the total row in the world
	 * @param r
	 * @param worldRow
	 * @param OFFSET - size of the hex
	 *                 (defined by the distance from centroid to vertex) 
	 * @return y position of the location (should be larger than or equal to 0)
	 *         -1 if the given index is invalid
	 */
	public static double getYPos(int r, int worldRow, double OFFSET) {
		return (worldRow - r) * OFFSET * SQRT_THREE / 2 ;
	}
	
	/**
	 * @param c - column of the location
	 * @param r - row of the location
	 * @return the id of hex defined by the row and column of location
	 */
	public static String getID(int c, int r) {
		return "" + c + "," + r;
	}
	
	/**
	 * Get the column/row of the location given the String representation of ID
	 * @param ID
	 * @param parseRow - {@code true} if need to parse row
	 *                   {@code false} if need to parse column
	 * @return column/row of the location (should be larger than or equal to 0)
	 *         -1 if the given {@code ID} is illegal
	 */
	private static int parseColRowFromID(String ID, boolean parseRow) {
		String[] token = ID.split(",");
		if (token.length != 2) {
			System.out.println("illegal id: " + ID);
			return -1;
		}
		try {
			int col = Integer.parseInt(token[0]);
			int row = Integer.parseInt(token[1]);
			if (row < 0)
				return -1;
			if (parseRow)
				return row;
			return col;
		} catch (Exception e) {
			System.out.println("illegal id: " + ID);
			return -1;
		}
	}
	
	/**
	 * Get the row of the location given the String representation of ID
	 * @param ID
	 * @return row number of the location (should be larger than or equal to 0)
	 *         -1 if the given {@code ID} is illegal
	 */
	public static int parseRowFromID(String ID) {
		return parseColRowFromID(ID, true);
	}
	
	/**
	 * Get the column of the location given the String representation of ID
	 * @param ID
	 * @return column of the location (should be larger than or equal to 0)
	 *         -1 if the given {@code ID} is illegal
	 */
	public static int parseColFromID(String ID) {
		return parseColRowFromID(ID, false);
	}
}