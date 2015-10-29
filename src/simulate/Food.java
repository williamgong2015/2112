package simulate;

import constant.Constant;

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
