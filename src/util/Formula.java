package util;

/**
 * For all the formula and their calculation 
 *
 */
public class Formula {

	/**
	 * logistic function P(x) = 1 / (1 + exp(-x))
	 * @param x
	 * @return P(x)
	 */
	public static double logistic(double x) {
		return 1 / (1 + Math.exp(x));
	}
}
