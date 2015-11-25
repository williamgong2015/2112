package client.gui;



import java.util.ArrayList;


/**
 * A hex placed in the world GUI, it keeps all 6 points making up its boundary 
 * Provide method to classify a Point the world GUI to a specific hex that it
 * belongs to. 
 */
public class NewHex {

	/** A tile node on the game board. Keeps track of its contained
	 *  background and label.
	 */
	private final HexLocation loc;
	// 6 points that define the boundary of the hex
	// each point need two double number to represent it
	public final Point[] points;
	public final Point centroid;
	public final double[] xPoints;
	public final double[] yPoints;
	// there are 6 vertexes in a polygon
	public static final int POINTSNUMBER = 6;
	// size of the hex, 
	// defined by the distance from the centroid to any vertex 
	public static final double HEX_SIZE = 10;
	public static final double SQRT_THREE = Math.sqrt(3);

	public NewHex(int column, int row, int worldRow) {
		this.loc = new HexLocation(column, row, worldRow, HEX_SIZE);
		centroid = new Point(loc.xPos, loc.yPos);
		xPoints = getXPoints();
		yPoints = getYPoints();
		points = getPoints();
	}
	
	/**
	 * Compute and return boundary of the hex
	 * @param worldRow - number of row the world has, 
	 *                   to compute the offset from top
	 * @return 6 points that define the boundary of the hex
	 */
	private Point[] getPoints() {
		return new Point[] {
			new Point(xPoints[0], yPoints[0]),
			new Point(xPoints[1], yPoints[1]),
			new Point(xPoints[2], yPoints[2]),
			new Point(xPoints[3], yPoints[3]),
			new Point(xPoints[4], yPoints[4]),
			new Point(xPoints[5], yPoints[5])
		};
	}

	/**
	 * Classify a point in the GUI to its nearest NewHex,
	 * 
	 * @param x - x coordinate value of the point 
	 * @param y - y coordinate value of the point 
	 * @param worldRow - total number of row in the world
	 * @param worldCol - total number of column in the world
	 * @return {column, row} of the NewHex in the GUI
	 *         {-1, -1} if the input is less than 0
	 */
	public static int[] classifyPoint(double x, double y, 
			int worldRow, int worldCol) {
		if (x < 0 || y < 0)
			return new int[] {-1, -1};
		// roughly compute column and row index
		int roughCol = (int) (x*2/3/HEX_SIZE);
		int roughRow =  worldRow - (int) (y*2/SQRT_THREE/HEX_SIZE);
		// the {roughCol, roughRow} NewHex and all six NewHex if exists
		// surrounding it could be the NewHex we are looking for
		ArrayList<HexLocation> toCheck = new ArrayList<>();
		toCheck.add(new HexLocation(roughCol, roughRow, 
				worldRow, HEX_SIZE));
		if (roughCol > 0 && roughRow < worldRow-1) // has top left hex
			toCheck.add(new HexLocation(roughCol-1, roughRow+1,
					worldRow, HEX_SIZE));
		if (roughCol > 0 && roughRow > 0) // has bottom left hex
			toCheck.add(new HexLocation(roughCol-1, roughRow-1,
					worldRow, HEX_SIZE));
		if (roughRow > 1) // has bottom hex
			toCheck.add(new HexLocation(roughCol, roughRow-1, 
					worldRow, HEX_SIZE));
		if (roughCol < worldCol-1 && roughRow > 0) // has bottom right
			toCheck.add(new HexLocation(roughCol+1, roughRow-1,
					worldRow, HEX_SIZE));
		if (roughCol < worldCol-1 && roughRow < worldRow-1) // top right
			toCheck.add(new HexLocation(roughCol+1, roughRow+1,
					worldRow, HEX_SIZE));
		if (roughRow < worldRow-1)
			toCheck.add(new HexLocation(roughCol, roughRow-1,
					worldRow, HEX_SIZE));
		return findClosestPoint(toCheck, x, y);
	}

	/**
	 * Find the closest point among an array of points {@code toCheck} to
	 * a given point {{@code targetXPos}, {@code targetYPos}}
	 * 
	 * @return {the column, row index} of the closest point
	 *         {-1, -1} if the {@code toCheck} is empty
	 */
	private static int[] findClosestPoint(ArrayList<HexLocation> toCheck, 
			double targetXPos, double targetYPos) {
		int[] closestPoint = new int[] {-1, -1};
		double closestDistance = Double.MAX_VALUE;
		for (HexLocation hexLoc : toCheck) {
			double tmp = distance(hexLoc.xPos, targetXPos, 
					hexLoc.yPos, targetYPos);
			if (tmp < closestDistance) {
				closestDistance = tmp;
				closestPoint[0] = hexLoc.c;
				closestPoint[1] = hexLoc.r;
			}
		}
		return closestPoint;
	}
	
	/**
	 * Get the Euler distance between two points
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @return the Euler distance
	 */
	private static double distance(double x1, double x2, 
			double y1, double y2) {
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}

	/**
	 * Compute and return x points of the hex
	 * @return 6 x points that define the boundary of the hex
	 */
	private double[] getXPoints() {
		return new double[] {
				loc.xPos - HEX_SIZE/2, 
				loc.xPos + HEX_SIZE/2,
				loc.xPos + HEX_SIZE,
				loc.xPos + HEX_SIZE/2,
				loc.xPos - HEX_SIZE/2,
				loc.xPos - HEX_SIZE,
				loc.xPos - HEX_SIZE/2
		};
	}

	/**
	 * Compute and return boundary of the hex

	 * @return 6 points that define the boundary of the hex
	 */
	private double[] getYPoints() {
		return new double[] {
				loc.yPos - HEX_SIZE * SQRT_THREE / 2,
				loc.yPos - HEX_SIZE * SQRT_THREE / 2,
				loc.yPos,
				loc.yPos + HEX_SIZE * SQRT_THREE / 2,
				loc.yPos + HEX_SIZE * SQRT_THREE / 2,
				loc.yPos,
				loc.yPos - HEX_SIZE * SQRT_THREE / 2
		};
	}

	/**
	 * @return the location of this hex
	 */
	public HexLocation getLoc() {
		return loc;
	}


}
