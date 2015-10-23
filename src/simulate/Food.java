package simulate;

import intial.Constant;

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
	 * @return the energy that this food contains
	 */
	public int getEnergy() {
		return amount * Constant.FOOD_PER_SIZE;
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
}
