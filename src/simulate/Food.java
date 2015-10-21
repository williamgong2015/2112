package simulate;

/**
 * A food object in the world
 * 
 */
public class Food extends Element {

	// the amount of food
	private int amount; 
	
	public Food() {
		super("FOOD");
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int a) {
		amount = a;
	}
}
