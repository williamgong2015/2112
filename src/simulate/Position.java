package simulate;

/**
 * A position and position calculation in hexes 
 */
public class Position {

	// column and row index in the world
	private int column;
	private int row;
	
	public Position(int c, int r) {
		row = r;
		column = c;
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
		Position pos = this;
		for(int i = 0;i < val;i++)
			pos = pos.get(dir);
		return pos;
	}
	
	/**
	 * return a position which is at direction {@code dir}
	 * of the position one step away
	 */
	public Position get(int dir) {
		switch(dir) {
		case 0:
			return new Position(column, row + 1);
		case 1:
			return new Position(column + 1, row + 1);
		case 2:
			return new Position(column + 1, row);
		case 3:
			return new Position(column, row - 1);
		case 4:
			return new Position(column - 1, row - 1);
		case 5:
			return new Position(column - 1, row);
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
		return column;
	}
	
	public int hashCode() {
		return 37 * row + column;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Position) {
			return (this.getColumn() ==((Position)o).getColumn() 
					&& this.getRow() ==((Position)o).getRow());
		}
		return false;
	}
}
