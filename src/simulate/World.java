package simulate;

import java.util.ArrayList;
import java.util.Hashtable;
import intial.Constant;
import util.RandomGen;

/**
 * The critter world
 *
 */
public class World {

	// how many turns has passed in the world
	private int turns;
	
	// the scale of the world
	private int row;
	private int column;
	private String name;
	
	public World(int r,int c,String n) {
		row = r;
		column = c;
		name = n;
		turns = 0;
		hexes = new Hashtable<Position, Element>();
	}
	
	/**
	 * initialize the world using constants
	 * and put rocks in random position
	 */
	public World() {
		row = Constant.ROWS;
		column = Constant.COLUMNS; 
		hexes = new Hashtable<Position, Element>();
		turns = 0;
		for(int i = 0;i < Math.abs(RandomGen.randomNumber(row * column / 10));) {
			int a = Math.abs(RandomGen.randomNumber(row));
			int b = Math.abs(RandomGen.randomNumber(column));
			Position pos = new Position(a,b);
			if(checkPosition(pos) && hexes.get(pos) == null) {
				hexes.put(pos, new Rock());
				i++;
				//TODO:要考虑死循环的情况，就是这个世界里所有格子都被占满的时候要结束循环
			}
		}
	}
	
	// maps position to element in the world
	private Hashtable<Position, Element> hexes;
	
	// order of critters in the world to take actions
	private ArrayList<Critter> order;
	
	/**
	 * add a critter to the arraylist
	 */
	public void addCritter(Critter c) {
		order.add(c);
	}
	
	/**
	 * A new turn of the world
	 */
	public void lapse() {
		turns++;
		for(Critter c : order) {
			Mediator m = new Mediator(c,this);
			//TODO
		}
	}
	
	/**
	 * Check the position is within the world boundary
	 * @param position the position to check
	 * @return true if the position is within the boundary,
	 *         false otherwise
	 */
	public boolean checkPosition(Position position) {
		int temp = position.getRow() * 2 - position.getColumn();
		if(position.getRow() < 0 || position.getRow() >= row ||
		   position.getColumn() < 0 || position.getColumn() >= column ||
		   temp < 0 || temp > 2 * row * column)
		return false;
		return true;
	}
	
	/**
	 * Get the element at {@code pos} in the world if 
	 * the position is within the boundary of the world, otherwise
	 * return a rock
	 */
	public Element getElemAtPosition(Position pos) {
		if (checkPosition(pos))
			return hexes.get(pos);
		return new Rock();
	}
	
	/**
	 * Set element at {@code pos} 
	 * @param elem element to set
	 * @param pos
	 * @return {@code false} if the {@code pos} is out of the world boundary
	 *         {@code true} otherwise
	 */
	public boolean setElemAtPosition(Element elem, Position pos) {
		if (!checkPosition(pos))
			return false;
		if(hexes.get(pos) != null)
			removeElemAtPosition(pos);
		hexes.put(pos, elem);
		return true;
	}
	
	/**
	 * Remove the element at the {@code pos} in the world
	 * @param pos the position to check
	 * @return {@code false} if the position is out of the boundary, 
	 *         {@code false} if there is no element at the {@code pos}
	 *         {@code true} otherwise
	 */
	public boolean removeElemAtPosition(Position pos) {
		if (!checkPosition(pos))
			return false;
		if (!hexes.containsKey(pos))
			return false;
		hexes.remove(pos);
		return true;
	}
	
	/**
	 * Effect: Print an ASCII-art map of the world
	 */
	public void printASCIIMap() {
		int r = row - 1;int c = column - 1;
		if(c % 2 == 1) {
			System.out.print("  ");
			for(int j = 1,k = r - c / 2;j <= Math.min(c,2 * r);j += 2) {
				System.out.print(enquery(k,j) + "  ");
				k++;
			}
			System.out.println();
		}
		for(int i = (r - c / 2);i >= 0;i--) {
			int j = 0;
			int k = i;
			for(;j <= Math.min(c,2 * r);j += 2) {
				System.out.print(enquery(k,j) + "  ");
				k++;
			}
			System.out.println();
			if(i == 0)
				return;
			j = 1;
			k = i;
			System.out.print(" ");
			for(;j <= Math.min(c,2 * r);j += 2) {
				System.out.print(enquery(k,j) + "  ");
				k++;
			}
			System.out.println();
		}
	}
	
	private String enquery(int r,int c) {
		Position pos = new Position(r,c);
		Element e = hexes.get(pos);
		if(e == null)
			return "-";
		if(e.getType().equals("FOOD"))
			return "F";
		if(e.getType().equals("CRITTER"))
			return "" + ((Critter)e).getDir();
		if(e.getType().equals("ROCK"))
			return "#";
		return null;
	}
	
	/**
	 * @return how many positions are not occupied
	 */
	public int aviablePos() {
		return 0;//TODO
	}
	
	/**
	 * @return how many rows in the world
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * @return how many columns in the world
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * Print a description of the contents of 
	 * the hex at coordinate (column, row{@code c, r}). 
	 */
	public void hex(int r,int c) {
		Position pos = new Position(r,c);
		Element e = hexes.get(pos);
		if(e == null || e.getType().equals("ROCK"))
			return;
		if(e.getType().equals("FOOD"))
			System.out.println(((Food)e).getAmount());
		if(e.getType().equals("CRITTER")) {
			Critter temp = (Critter)e;
			System.out.println(temp.toString());
			//TODO:这里需要打印这个生物执行的最后一条动作
		}
	}
}
