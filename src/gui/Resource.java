package gui;

import javafx.scene.image.Image;

public class Resource {
	public static final Image critterImg = 
			new Image(Resource.class.getResource("Critter.png").toExternalForm());
	public static final Image critterImgS1 = 
			new Image(Resource.class.getResource("Species1_S1.jpg").toExternalForm());
	public static final Image critterImgS2 = 
			new Image(Resource.class.getResource("Species1_S2.jpg").toExternalForm());
	public static final Image critterImgS3 = 
			new Image(Resource.class.getResource("Species1_S3.jpg").toExternalForm());
	public static final Image critterImgS4 = 
			new Image(Resource.class.getResource("Species1_S4.jpg").toExternalForm());
	public static final Image critterImgS5 = 
			new Image(Resource.class.getResource("Species1_S5.jpg").toExternalForm());
	public static final Image critterImgS6 = 
			new Image(Resource.class.getResource("Species1_S6.jpg").toExternalForm());
	public static final Image critterImgS7 = 
			new Image(Resource.class.getResource("Species1_S7.jpg").toExternalForm());
	public static final Image rockImg = 
			new Image(Resource.class.getResource("Rock.png").toExternalForm());
	public static final Image foodImg = 
			new Image(Resource.class.getResource("Food.png").toExternalForm());
}
