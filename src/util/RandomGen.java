package util;

import java.util.Random;

/**
 * Random Number Generator
 */
public class RandomGen {

	/** @return a integer in [0..size] */
	public static int randomNumber(int size) {
		Random rand = new Random();
		return rand.nextInt(size);
	}
	
	public static int randomNumber() {
		Random rand = new Random();
		return rand.nextInt();
	}
	
	/** @return two unique integers both in [0..size] */
	public static int[] twoUniqueRandomNum(int size) {
		if (size <= 1)
			return null;
		int[] r = new int[2];
		Random rand = new Random();
		r[0] = rand.nextInt(size);
		r[1] = rand.nextInt(size);
		while (r[0] == r[1])
			r[1] = rand.nextInt(size);
		return r;
	}
	
	/** 
	 * @return a integers in [0..size] different from the given {@code one}
	 * @param one
	 *     the given integer
	 * Require: size > 1
	 */
	public static int anotherRandomNum(int size, int one) {
		if (size <= 1)
			return -1;
		Random rand = new Random();
		int another = rand.nextInt(size);
		while (one == another)
			another = rand.nextInt(size);
		return another;
	}
	
}
