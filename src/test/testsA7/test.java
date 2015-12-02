package test.testsA7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import api.PositionInterpreter;
import api.JsonClasses.State;
import api.JsonClasses.WorldState;
import client.MyClient;
import client.element.ClientElement;
import client.world.ClientPosition;
import game.exceptions.SyntaxError;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;

public class test extends Application {
	
	private static final int REFRESH_SPEED = 30;  // 30 times per second

	public static void main(String[] args) {
		launch(args);
	}

	

	
	/**
	 * Get the KeyFrame for the timeline which controls the world running speed
	 * @param stepsPerSecond
	 * @return
	 */
	private static KeyFrame getGUIRefreshKeyFrame(int stepsPerSecond) {
//		KeyValue tmp = null;
//		return new KeyFrame(Duration.seconds(1 / stepsPerSecond), 
//				"GUI Refresh",
//				new EventHandler<ActionEvent>() {
//			@Override 
//			public void handle(ActionEvent e) {
//				System.out.println("handler");
////				refreshGUI();
//			}
//		}, tmp);
		
		return new KeyFrame(Duration.millis(500), event -> System.out.println("handler"));
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Timeline refreshTimeline = new Timeline();
	    refreshTimeline.setCycleCount(Integer.MAX_VALUE);
	    // recounts cycle count every time it plays again
	    refreshTimeline.setAutoReverse(false);  
		refreshTimeline.getKeyFrames().setAll(
				getGUIRefreshKeyFrame(REFRESH_SPEED)
		);
		refreshTimeline.play();
	}
}