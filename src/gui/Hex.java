//package gui;
//
//import javafx.scene.layout.StackPane;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Text;
//
///**
// * A hex pane placed in the world 
// *
// */
//public class Hex extends StackPane {
//
//	/** A tile node on the game board. Keeps track of its contained
//	 *  background and label.
//	 */
//		public final Text label;
//		public final Rectangle background;
//		int row, column;
//		public Tile(int row, int column) {
//			Rectangle r = background = new Rectangle();
//			this.row = row;
//			this.column = column;
//			label = new Text();
//			getChildren().add(r);
//			getChildren().add(label);
//			r.setWidth(tileSize);
//			r.setHeight(tileSize);
//			r.setFill(tileColor);
//			r.setStrokeWidth(0);
//			r.setStroke(tileColor);
//			r.setArcWidth(tileSize * 0.30);
//			r.setArcHeight(tileSize * 0.30);
//			label.setFont(numFonts[0]);
//			label.setFill(numColor);
//		}
//		public void setPosn() {
//			setLayoutX(gutter + column * (tileSize + gutter));
//			setLayoutY(gutter + row * (tileSize + gutter));
//		}
//}
