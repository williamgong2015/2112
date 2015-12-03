package client.gui;



import java.util.ArrayList;

import api.PositionInterpreter;
import client.world.ClientPosition;
import client.world.ClientPoint;


/**
 * A hex placed in the world GUI, it keeps all 6 points making up its boundary 
 * Provide method to classify a Point the world GUI to a specific hex that it
 * belongs to. 
 */
public class GUIHex {

	/** A tile node on the game board. Keeps track of its contained
	 *  background and label.
	 */
	public final ClientPosition loc;
	// 6 points that define the boundary of the hex
	// each point need two double number to represent it
	public final ClientPoint[] points;
	public final ClientPoint centroid;
	public final double[] xPoints;
	public final double[] yPoints;
	// there are 6 vertexes in a polygon
	public static final int POINTSNUMBER = 6;
	// size of the hex, 
	// defined by the distance from the centroid to any vertex 
	public static final double HEX_SIZE = 10;
	public static final double SQRT_THREE = Math.sqrt(3);

	public GUIHex(int x, int y, int worldY) {
		this.loc = new ClientPosition(x, y, worldY, HEX_SIZE);
		centroid = new ClientPoint(loc.xPos, loc.yPos);
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
	private ClientPoint[] getPoints() {
		return new ClientPoint[] {
			new ClientPoint(xPoints[0], yPoints[0]),
			new ClientPoint(xPoints[1], yPoints[1]),
			new ClientPoint(xPoints[2], yPoints[2]),
			new ClientPoint(xPoints[3], yPoints[3]),
			new ClientPoint(xPoints[4], yPoints[4]),
			new ClientPoint(xPoints[5], yPoints[5])
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
		int worldX = PositionInterpreter.getX(worldCol, worldRow);
		int worldY = PositionInterpreter.getY(worldCol, worldRow);
		int roughX = (int) (x*2/3/HEX_SIZE);
		int roughY =  worldY - (int) (y*2/SQRT_THREE/HEX_SIZE);
		// the {roughCol, roughRow} NewHex and all six NewHex if exists
		// surrounding it could be the NewHex we are looking for
		ArrayList<ClientPosition> toCheck = new ArrayList<>();
		toCheck.add(new ClientPosition(roughX, roughY, 
				worldY, HEX_SIZE));
		if (roughX > 0 && roughY < worldY-1) // has top left hex
			toCheck.add(new ClientPosition(roughX-1, roughY+1,
					worldY, HEX_SIZE));
		if (roughX > 0 && roughY > 0) // has bottom left hex
			toCheck.add(new ClientPosition(roughX-1, roughY-1,
					worldY, HEX_SIZE));
		if (roughY > 1) // has bottom hex
			toCheck.add(new ClientPosition(roughX, roughY-1, 
					worldY, HEX_SIZE));
		if (roughX < worldX-1 && roughY > 0) // has bottom right
			toCheck.add(new ClientPosition(roughX+1, roughY-1,
					worldY, HEX_SIZE));
		if (roughX < worldX-1 && roughY < worldY-1) // top right
			toCheck.add(new ClientPosition(roughX+1, roughY+1,
					worldY, HEX_SIZE));
		if (roughY < worldY-1)
			toCheck.add(new ClientPosition(roughX, roughY-1,
					worldY, HEX_SIZE));
		return findClosestPoint(toCheck, x, y);
	}

	/**
	 * Find the closest point among an array of points {@code toCheck} to
	 * a given point {{@code targetXPos}, {@code targetYPos}}
	 * 
	 * @return {the column, row index} of the closest point
	 *         {-1, -1} if the {@code toCheck} is empty
	 */
	private static int[] findClosestPoint(ArrayList<ClientPosition> toCheck, 
			double targetXPos, double targetYPos) {
		int[] closestPoint = new int[] {-1, -1};
		double closestDistance = Double.MAX_VALUE;
		for (ClientPosition hexLoc : toCheck) {
			double tmp = distance(hexLoc.xPos, targetXPos, 
					hexLoc.yPos, targetYPos);
			if (tmp < closestDistance) {
				closestDistance = tmp;
				closestPoint[0] = hexLoc.x;
				closestPoint[1] = hexLoc.y;
			}
		}
		// print closest point in Cartesian Coordinate
		System.out.println("closest point index in Hex Coordinate: (" + 
			PositionInterpreter.getC(closestPoint[0], closestPoint[1]) + ","
			+ PositionInterpreter.getR(closestPoint[0], closestPoint[1]) + 
			")");
		System.out.println("closest distance: " + closestDistance);
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
	public ClientPosition getLoc() {
		return loc;
	}

	public String toString() {
		return "centroid at: " + centroid;
	}
	
	/**
	 * If two hex has the same location, they are consider to be equaled
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GUIHex))
			return false;
		GUIHex tmp = (GUIHex) obj;
		return this.loc.equals(tmp.loc);
	}

}
