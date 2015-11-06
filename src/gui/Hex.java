package gui;



import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;


/**
 * A hex Polygon placed in the world that has an id, and location,
 * critters/foods/rocks are draw inside of it using ImagePattern
 *
 */
public class Hex extends Polygon {

	/** A tile node on the game board. Keeps track of its contained
	 *  background and label.
	 */
		private final HexLocation loc;
		// 6 points that define the boundary of the hex
		// each point need two double number to represent it
		private final Double[] points;
		private final String ID;
		
		private static final Color DEFAULT_STROCK_COLOR = Color.BLACK;
		private static final Color HOVER_STROCK_COLOR = Color.web("#3AD53A");
		private static final Color SELECTED_STROCK_COLOR = Color.RED;
		private static final double DEFAULT_STROCK_WIDTH = 1.0;
		private static final double HOVER_STROCK_WIDTH = 3.0;
		private static final double SELECTED_STROCK_WIDTH = 5.0;
		private static final double ANGLE_STEPSIZE = 60;
		// size of the hex, 
		// defined by the distance from the centroid to any vertex 
		public static final double HEX_SIZE = 30;
		private static final double SQRT_THREE = Math.sqrt(3);
		

		
		public Hex(int column, int row, int worldRow) {
			this.loc = new HexLocation(row, column, worldRow, HEX_SIZE);
			ID = HexLocation.getID(loc.c, loc.r);
			points = getBoundary();
			this.getPoints().addAll(points);
			this.setStroke(DEFAULT_STROCK_COLOR);
			this.setId(HexLocation.getID(loc.c, loc.r));
		}
		
		/**
		 * Compute and return boundary of the hex
		 * @param worldRow - number of row the world has, 
		 *                   to compute the offset from top
		 * @return 6 points that define the boundary of the hex
		 */
		private Double[] getBoundary() {
			return new Double[] {
				loc.xPos - HEX_SIZE/2, loc.yPos - HEX_SIZE * SQRT_THREE / 2,
				loc.xPos + HEX_SIZE/2, loc.yPos - HEX_SIZE * SQRT_THREE / 2,
				loc.xPos + HEX_SIZE, loc.yPos,
				loc.xPos + HEX_SIZE/2, loc.yPos + HEX_SIZE * SQRT_THREE / 2,
				loc.xPos - HEX_SIZE/2, loc.yPos + HEX_SIZE * SQRT_THREE / 2,
				loc.xPos - HEX_SIZE, loc.yPos
			};
		}
		
		/**
		 * Reset the border style to be the default style
		 */
		public void setDefaultStrock() {
			this.setStrokeType(StrokeType.OUTSIDE);
			this.setStrokeWidth(DEFAULT_STROCK_WIDTH);
			this.setStroke(DEFAULT_STROCK_COLOR);
		}
		
		/**
		 * Set the border style when hovering above the hex
		 */
		public void setHoverStrock() {
			this.setStrokeType(StrokeType.INSIDE);
			this.setStrokeWidth(HOVER_STROCK_WIDTH);
			this.setStroke(HOVER_STROCK_COLOR);
		}
		
		/**
		 * Set the border style when the hex is being selected
		 */
		public void setSelectedStrock() {
			this.setStrokeType(StrokeType.INSIDE);
			this.setStrokeWidth(SELECTED_STROCK_WIDTH);
			this.setStroke(SELECTED_STROCK_COLOR);
		}
		
		/**
		 * Rotate 60 degree to the left side
		 */
		public void rotateLeft() {
			this.setRotate(this.getRotate() - ANGLE_STEPSIZE);
		}
		
		/**
		 * Rotate 60 degree to the right side
		 */
		public void rotateRight() {
			this.setRotate(this.getRotate() + ANGLE_STEPSIZE);
		}
		
		/**
		 * @return the location of this hex
		 */
		public HexLocation getLoc() {
			return loc;
		}
		
		/**
		 * Set a critter image into this hex
		 * @param dir - the direction the critter is facing at
		 *              0: up, 1: up right, 2: down right
		 *              3: down, 4: down left, 5: up left
		 *              all other number are illegal and will set the critter
		 *              to facing up
		 */
		public void setCritter(int dir, int size) {
			if (size <= 0)
				this.setFill(new ImagePattern(Resource.critterImgS1));
			else if (size >7)
				this.setFill(new ImagePattern(Resource.critterImgS7));
			else {
				switch(size) {
				case 1:
					this.setFill(new ImagePattern(Resource.critterImgS1));
					break;
				case 2:
					this.setFill(new ImagePattern(Resource.critterImgS2));
					break;
				case 3:
					this.setFill(new ImagePattern(Resource.critterImgS3));
					break;
				case 4:
					this.setFill(new ImagePattern(Resource.critterImgS4));
					break;
				case 5:
					this.setFill(new ImagePattern(Resource.critterImgS5));
					break;
				case 6:
					this.setFill(new ImagePattern(Resource.critterImgS6));
					break;
				case 7:
					this.setFill(new ImagePattern(Resource.critterImgS7));
					break;
				}
			}
			if (dir <= 5 && dir >= 0)
				this.setRotate(60 * dir);
			else
				this.setRotate(0);
		}
		
		/**
		 * Set a food image into this hex
		 */
		public void setFood() {
			this.setFill(new ImagePattern(Resource.foodImg));
		}
		
		/**
		 * Set a rock image into this hex
		 */
		public void setRock() {
			this.setFill(new ImagePattern(Resource.rockImg));
		}
		
		/**
		 * Set the hex to become empty
		 */
		public void setEmpty() {
			this.setFill(Color.WHITE);
		}
	
}
