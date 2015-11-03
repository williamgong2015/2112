package gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Starter extends Application {
	
	public Rectangle []r = new Rectangle[2];
	public Pane []p = new Pane[2];
	
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("a6.fxml"));
        primaryStage.setTitle("Critter World !");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
        Button bt = (Button) root.lookup("#addcritter_button");
        if (bt != null)
        	bt.setOnAction(e -> System.out.println("pressed addcritter_button"));
        Pane worldPane = (Pane) root.lookup("#world_pane");
        StackPane pane = new StackPane();
        r[0] = new Rectangle(50,50);
        r[0].setFill(Color.RED);
        p[0] = new Pane();
        p[0].setStyle("-fx-background-image: url(\"http://www.bogotobogo.com/images/java/tutorial/java_images/Duke256.png\");"
        		+ "-fx-background-repeat: no-repeat;"
        		+ "-fx-background-size: contain;");
        pane.getChildren().addAll(r[0], p[0]);
        r[0].widthProperty().bind(pane.prefWidthProperty());
        r[0].heightProperty().bind(pane.prefHeightProperty());
        pane.setPrefSize(100, 100);
//        worldPane.getChildren().add(pane);
        
        StackPane pane2 = new StackPane();
        r[1] = new Rectangle(50, 50);
        r[1].setFill(Color.RED);
        p[1] = new Pane();
        p[1].setStyle("-fx-background-image: url(\"http://www.bogotobogo.com/images/java/tutorial/java_images/Duke256.png\");"
        		+ "-fx-background-repeat: no-repeat;"
        		+ "-fx-background-size: contain;");
        pane2.getChildren().addAll(r[1], p[1]);
        r[1].widthProperty().bind(pane2.prefWidthProperty());
        r[1].heightProperty().bind(pane2.prefHeightProperty());
        pane2.setPrefSize(100, 100);
        pane2.setLayoutX(100);
        worldPane.getChildren().addAll(pane,pane2);
        pane.setOnMouseEntered(new EnterHexHandler());
        pane.setOnMouseClicked(new ClickHexHandler());
        pane.setOnMouseExited(new ExitHexHandler());
        pane2.setOnMouseEntered(new EnterHexHandler());
        pane2.setOnMouseClicked(new ClickHexHandler());
        pane2.setOnMouseExited(new ExitHexHandler());
        
        
        
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
			double x = event.getSceneX();
			if (x < 100)  {
				r[0].setFill(Color.BLACK);
				r[1].setFill(Color.RED);
				p[0].getTransforms().add(new Rotate(90));
			}
			else {
				r[0].setFill(Color.RED);
				r[1].setFill(Color.BLACK);
			}
				
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