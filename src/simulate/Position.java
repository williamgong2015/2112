package simulate;

/**
 * A position and position calculation in hexes 
 */
public class Position {

	// col and row index in the world
	private int col;
	private int row;
	
	public Position(int c, int r) {
		row = r;
		col = c;
	}
	
	/**
	 * Randomly create a position with the boundary of world,
	 * the world has a col boundary {@code cBound} and a 
	 * row boundary {@code rBound}
	 * 
	 * @param cBound 
	 * @param rBound
	 */
	public static Position getRandomPosition(int cBound, int rBound) {
		if (cBound <= 0 || rBound <= 0) {
			System.out.println("Can't get random position");
			return null;
		}
		Position pos = new Position(util.RandomGen.randomNumber(cBound), 
				util.RandomGen.randomNumber(rBound));
		while (!checkPosition(pos, cBound, rBound)) {
			pos = new Position(util.RandomGen.randomNumber(cBound), 
					util.RandomGen.randomNumber(rBound));
		}
		return pos;
	}
	
	/**
	 * Check if the given position is within the given boundary
	 * @param position the position to check
	 * @param cBound the boundary of col
	 * @param rBound the boundary of row
	 * @return
	 */
	public static boolean checkPosition(Position position, int cBound, 
			int rBound) {
		if (position == null)
			return false;
		int temp = position.getRow() * 2 - position.getColumn();
		if(position.getRow() < 0 || position.getRow() >= rBound ||
		   position.getColumn() < 0 || position.getColumn() >= cBound ||
		   temp < 0 || temp >= 2 * rBound - cBound)
		return false;
		return true;
	}
	
	/**
	 * Coordinate transform between Cartesian coordinate and Hex coordinate
	 * Formula:  h = 2r-c, v = c, 
	 *           r = (v+h+1)/2, c = v
	 */
	public static int getR(int v, int h) {
		return (v+h+1)/2;
	}
	public static int getC(int v, int h) {
		return v;
	}
	public static int getH(int c, int r) {
		return 2*r-c;
	}
	public static int getV(int c, int r) {
		return c;
	}
	
	/**
	 * return the position which is in the {@code dir} 
	 * direction of this position at distance{@code val} from the position
	 */
	public Position getRelativePos(int val,int dir) {
		if (val < 0) {
			val = -val;
			dir = Math.abs(dir - 3);
		}
		Position pos = this;
		for(int i = 0;i < val;i++)
			pos = pos.getNextStep(dir);
		return pos;
	}
	
	/**
	 * return a position which is at direction {@code dir}
	 * of the position one step away
	 */
	public Position getNextStep(int dir) {
		switch(dir) {
		case 0:
			return new Position(col, row + 1);
		case 1:
			return new Position(col + 1, row + 1);
		case 2:
			return new Position(col + 1, row);
		case 3:
			return new Position(col, row - 1);
		case 4:
			return new Position(col - 1, row - 1);
		case 5:
			return new Position(col - 1, row);
		}
		return null;
	}
	
	/**
	 * return the row number of this position
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * return the row number of this position
	 */
	public int getColumn() {
		return col;
	}
	
	public int hashCode() {
		return 37 * row + col;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Position) {
			return (this.getColumn() ==((Position)o).getColumn() 
					&& this.getRow() ==((Position)o).getRow());
		}
		return false;
	}
	
	public String toString() {
		return "" + col + ".." + row;
	}
}
