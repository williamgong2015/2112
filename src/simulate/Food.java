package simulate;

import util.RandomGen;

/**
 * A food object in the world
 * 
 */
public class Food extends Element {

	// the amount of food
	private int amount; 
	
	public Food(int a) {
		super("FOOD");
		amount = a;
	}
	
	/**
	 * Load {@code n} number of food into the world, the amount of the food
	 * is limited by {@code amount}
	 * 
	 * Check the world has enough empty slot for {@code n} of food
	 * if not, only insert half the number of food equals to half of the 
	 * empty slots
	 * 
	 * @param world
	 * @param n
	 * @param amount
	 */
	public static void loadFoodIntoWorld(World world, int n, int amount) {
		if (world.availableSlot() < n)
			n = world.availableSlot() / 2;
		Food f;
		int column = world.getColumn();
		int row = world.getRow();
		while (n > 0) {
			f = new Food(util.RandomGen.randomNumber(amount));
			int a = Math.abs(RandomGen.randomNumber(row));
			int b = Math.abs(RandomGen.randomNumber(column));
			Position pos = new Position(b,a);
			if(world.checkPosition(pos) && 
					world.getElemAtPosition(pos) == null) {
				world.setElemAtPosition(f, pos);
			}
			n--;
		}
	}
	
	/**
	 * @return the amount of this food
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * set the amount of the food to {@code a}
	 */
	public void setAmount(int a) {
		amount = a;
	}
	
	/**
	 * Create a string representation of the food amount
	 */
	public String toString() {
		return "Amount: " + amount;
	}
	
	@Override
	public boolean equals(Object that) {
		if (!(that instanceof Food))
			return false;
		return ((Food) that).getAmount() == this.amount;
	}
}
