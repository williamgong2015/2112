package gui;

/**
 * Container of two coordinate
 */
public class Point {
	
	public double x;
	public double y;
	
	Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Return the middle point between two points
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Point getMiddlePoint(Point p1, Point p2) {
		return new Point((p1.x+p2.x)/2, (p1.y+p2.y)/2);
	}
	
	/**
	 * Return a point between two points, with the distance to {@code p1} D1 : 
	 * the distance to {@code p1} D2 satisfying D1 : (D1 + D2) = weight
	 * 
	 * Require weight <= 1 and >= 0
	 * @param p1
	 * @param p2
	 * @param weight
	 * @return
	 */
	public static Point getMiddlePointWithWeight(Point p1, 
			Point p2, double weight) {
		if (weight < 0 || weight > 1)
			return null;
		return new Point(weight*(p1.x-p2.x) + p2.x, 
				weight*(p1.y-p2.y) + p2.y);
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
