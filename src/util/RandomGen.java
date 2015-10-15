package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Random Number Generator
 * 
 * Reference: 
 * Shuffle An Array
 * http://stackoverflow.com/questions/15196363/java-how-do-i-create-an-
 * int-array-with-randomly-shuffled-numbers-in-a-given-ra
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
	
	/** 
	 * @return a shuffled integer array containing [0,1,2...size] except 
	 *         {@code one}
	 * @param one
	 *     a given integer shouldn't be in the array
	 */
	public static int[] arrOfRanNumExcept(int size, int one) {
		List<Integer> dataList = new ArrayList<Integer>();
	    for (int i = 0; i < size; i++) {
	    	if (i != one)
	    		dataList.add(i);
	    }
	    Collections.shuffle(dataList);
	    int[] r = new int[dataList.size()];
	    for (int i = 0; i < dataList.size(); i++) {
	      r[i] = dataList.get(i);
	    }
	    return r;
	}
	
}
