package simulate;

/**
 * A position and position calculation in hexes 
 */
public class Position {

	// row and column index in the world
	private int row;
	private int column;
	
	public Position(int r, int c) {
		row = r;
		column = c;
	}
	
	/**
	 * return the position which is in the {@code dir} 
	 * direction of this position at distance{@code val} from the position
	 */
	public Position move(int val,int dir) {
		Position pos = this;
		for(int i = 0;i < val;i++)
			pos = pos.get(dir);
		return pos;
	}
	
	/**
	 * return a position which is at direction {@code dir}
	 * of the position
	 */
	public Position get(int dir) {
		switch(dir) {
		case 0:
			return new Position(row + 1,column);
		case 1:
			return new Position(row + 1,column + 1);
		case 2:
			return new Position(row,column + 1);
		case 3:
			return new Position(row - 1,column);
		case 4:
			return new Position(row - 1,column - 1);
		case 5:
			return new Position(row,column - 1);
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
}
