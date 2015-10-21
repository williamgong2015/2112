package simulate;

/**
 * A critter object in the world, store the properties of the critter
 * 
 */
public class Critter extends Element {

	int[] mem;
	
	public Critter(int len) {
		super("CRITTER");
	}
	
	public void setMem(int index, int val) {
		mem[index] = val;
	}
	
	public int getMem(int index) {
		return mem[index];
	}

}
