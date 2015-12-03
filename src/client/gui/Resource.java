package client.gui;

import javafx.scene.image.Image;

public class Resource {
	public static final Image rockImg = 
			new Image(Resource.class.getResource("Rock.jpg").toExternalForm());
	public static final Image foodImg = 
			new Image(Resource.class.getResource("Food.jpg").toExternalForm());
}
