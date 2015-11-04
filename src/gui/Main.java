package gui;

import java.util.Scanner;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import simulate.World;

public class Main extends Application {
	
	private String worldFile;  // path to world file 
	private String critterFile;  // path to critter file
    private Scanner scan;
    public World world;
    
	
	
	public Rectangle []r = new Rectangle[2];
	public Pane []p = new Pane[2];
	
	private ImageView iv1;
	private Polygon poly;
	private Polyline polyLine;
	
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("a6.fxml"));
        primaryStage.setTitle("Critter World !");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        p[1] = new Pane();
        p[1].setStyle("-fx-border-style: solid inside;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" + 
                "-fx-border-radius: 5;" + 
                "-fx-border-color: blue;");
        Pane worldPane = (Pane) root.lookup("#world_pane");        
        Image critterImg = new Image(getClass().getResource("Critter.png").toExternalForm());
        iv1 = new ImageView(critterImg);
        iv1.setStyle("-fx-border-style: solid inside;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" + 
                "-fx-border-radius: 5;" + 
                "-fx-border-color: blue;");
        
        iv1.fitWidthProperty().bind(p[1].widthProperty()); 
        iv1.fitHeightProperty().bind(p[1].heightProperty()); 
        
        poly = new Polygon();
        polyLine = new Polyline();
       
        poly.getPoints().addAll(new Double[] {
        		10.0, 0.0,
        		30.0, 0.0,
        		40.0, 17.0,
        		30.0, 34.0,
        		10.0, 34.0,
        		0.0, 17.0
        });
        polyLine.getPoints().addAll(new Double[] {
        		10.0, 0.0,
        		30.0, 0.0,
        		40.0, 17.0,
        		30.0, 34.0,
        		10.0, 34.0,
        		0.0, 17.0,
        		10.0, 0.0
        });
        poly.setLayoutX(30);
        poly.setLayoutY(30);
        polyLine.setLayoutX(30);
        polyLine.setLayoutY(30);
        
        polyLine.setStyle("-fx-border-style: solid inside;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" + 
                "-fx-border-radius: 5;" + 
                "-fx-border-color: blue;");
//        polyLine.setStroke(Color.RED);
        
        poly.setFill(new ImagePattern(critterImg));
        poly.setStyle("-fx-border-style: solid inside;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" + 
                "-fx-border-radius: 5;" + 
                "-fx-border-color: blue;");
        poly.setStroke(Color.RED);
        poly.setOnMouseClicked(new ClickHexHandler());
        p[1].getChildren().addAll(poly);
        
        worldPane.getChildren().add(p[1]);
    }

    public static void main(String[] args) {
        launch(args);

    }
    
    /** Handler for arrow keys to trigger moves. */
	class ClickHexHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			// TODO Auto-generated method stub
			System.out.println("you clicked a hex");
			// get and set the id of every polygon
			System.out.println(((Polygon)event.getSource()).getId());
			double x = event.getSceneX();
			
			poly.setRotate(poly.getRotate() + 60);
			
		}
	}
	
	/** Handler for arrow keys to trigger moves. */
	class EnterHexHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			// TODO Auto-generated method stub
			System.out.println("you enter a hex");
			double x = event.getSceneX();
			if (x < 100) {
				r[0].setFill(Color.BROWN);
				r[1].setFill(Color.RED);
			}
			else {
				r[0].setFill(Color.RED);
				r[1].setFill(Color.BROWN);
			}
		}
	}
	
	/** Handler for arrow keys to trigger moves. */
	class ExitHexHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			// TODO Auto-generated method stub
			System.out.println("you enter a hex");
			double x = event.getSceneX();
			if (x < 100) 
				r[0].setFill(Color.RED);
			else
				r[1].setFill(Color.RED);
		}
	}

}