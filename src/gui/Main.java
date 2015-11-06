package gui;

import java.io.File;
import java.util.ArrayList;

import exceptions.SyntaxError;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import simulate.Critter;
import simulate.Element;
import simulate.Position;
import simulate.World;

/**
 * Main controller class that draw all the hexes in the GUI and control 
 * its interaction with the underlying critter program model.
 * - Listen to the action on View and control the update in underlying data
 * - Execute the update of View based HexToUpdate passed from underlying 
 *   critter world
 *
 */
public class Main extends Application {
    private final static int DEFAULT_WORLD_IDX = 0;
	private final static int CUSTOM_WORLD_IDX = 1;
	private File worldFile = null;  // path to world file 
	private File critterFile = null;  // path to critter file
    private Hex current = null; // current selected hex
	private Parent root;
	private Pane worldPane;
	private Label worldInfoLabel;
	private Label critterInfoLabel;
	public World world;
    private int worldCol;
    private int worldRow;
    private double intialStepsPerSecond = 1;
    Timeline timeline;
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(getClass().getResource("a6.fxml"));
        primaryStage.setTitle("Critter World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
        worldPane = (Pane) root.lookup("#world_pane"); 
        worldInfoLabel = (Label) root.lookup("#worldinfodetails_label");
        critterInfoLabel = (Label) root.lookup("#critterinfodetails_label");
        Alerts.alertWellcome();
        // set the iteration count to be as large as it can
        timeline = new Timeline();
        timeline.setCycleCount(Integer.MAX_VALUE);
        // recounts cycle count every time it plays again
        timeline.setAutoReverse(false);  
        timeline.getKeyFrames().add(
        		getWorldSimulationKeyFrame(intialStepsPerSecond));
        
        // initialize button
        
        MenuButton worldFileBtn = 
        		(MenuButton) root.lookup("#newworld_manubutton");
        worldFileBtn.getItems().get(DEFAULT_WORLD_IDX)
				.setOnAction(e -> {
				        world = new World();
				        drawWorldLayout();
				        ArrayList<HexToUpdate> hexToUpdate = 
        	    				world.getHexToUpdate();
        	    		executeHexUpdate(hexToUpdate);
				    });
        
        worldFileBtn.getItems().get(CUSTOM_WORLD_IDX)
        		.setOnAction(e -> {
        		        worldFile = loadFile(primaryStage);
        		        world = World.loadWorld(worldFile);
        		        drawWorldLayout(); 
        		        ArrayList<HexToUpdate> hexToUpdate = 
        	    				world.getHexToUpdate();
        	    		executeHexUpdate(hexToUpdate);
        		    });
        
        Button critterFileBtn = (Button) root.lookup("#loadcritter_button");
        critterFileBtn.setOnAction(e -> critterFile = loadFile(primaryStage));
        
        Button addCritterBtn = (Button) root.lookup("#addcritter_button");
        TextField critterNumText = 
        		(TextField) root.lookup("#critternum_textfield");
        addCritterBtn.setOnAction(e -> {
		    	addCritter(critterNumText.getText());
		    	critterNumText.clear();
		    });
        
        Button insertCritterBtn = 
        		(Button) root.lookup("#insertcritter_button");
        insertCritterBtn.setOnAction(e -> insertCritter());
        
        Button worldStepButton = 
        		(Button) root.lookup("#step_button");
        worldStepButton.setOnAction(e -> worldStepAhead());
        
        Button worldRunButton = 
        		(Button) root.lookup("#run_button");
        worldRunButton.setOnAction(e -> timeline.play());
        
        Button worldStopButton =
        		(Button) root.lookup("#stop_button");
        worldStopButton.setOnAction(e -> timeline.stop());
        
        Slider simulationSpeedSlider = 
        		(Slider) root.lookup("#simulationspeed_slider");
        simulationSpeedSlider.valueProperty().addListener((o, oldVal, newVal) -> 
        changeSimulationSpeed(simulationSpeedSlider.getValue()));
        
        Button helpButton = 
        		(Button) root.lookup("#help_button");
        helpButton.setOnAction(e -> Alerts.alertDisplayHelpInfo());
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Change the simulation speed 
     * @param sliderVal - the current value of slider (1-30)
     *        number of frame will be executed in one second 
     *        = {@code sliderVal}
     */
    private void changeSimulationSpeed(double sliderVal) {
    	boolean wasRunning = false;
    	if (timeline.statusProperty().get() == Status.RUNNING)
    		wasRunning = true;
    	timeline.stop();
    	timeline.getKeyFrames().setAll(
    			getWorldSimulationKeyFrame(sliderVal)
        );
    	if (wasRunning)
    		timeline.play();
    }
    
    
    private KeyFrame getWorldSimulationKeyFrame(double stepsPerSecond) {
    	KeyValue tmp = null;
    	return new KeyFrame(Duration.seconds(1 / stepsPerSecond), 
    			"world lapse",
        		new EventHandler<ActionEvent>() {
        		    @Override 
        		    public void handle(ActionEvent e) {
        		    	worldStepAhead();
        		    }
        		}, tmp);
    }
    
    
    private void drawWorldLayout() {
        worldRow = Position.getH(world.getColumn(), 
	                 world.getRow());
	    worldCol = Position.getV(world.getColumn(), 
	       			 world.getRow());
	    System.out.println("worldRow: " + worldRow);
	    world.printCoordinatesASCIIMap();
    	worldPane.getChildren().clear();
        Hex poly;
        for (int i = 0; i < worldRow; ++i) {
        	for (int j = 0; j < worldCol; ++j) {
        		if (i % 2 != j % 2)
        			continue;
        		poly = new Hex(i, j, worldRow);
        		poly.setFill(Color.WHITE);
                poly.setOnMouseClicked(new ClickHexHandler());
                poly.setOnMouseEntered(new EnterHexHandler());
                poly.setOnMouseExited(new ExitHexHandler());
                worldPane.getChildren().add(poly);
        	}
        }
    }
    
    /**
     * Have the underlying world proceed for one turn and update the GUI
     */
    private void worldStepAhead() {
    	world.lapse();
    	executeHexUpdate(world.getHexToUpdate());
    }
    
    /**
     * Effect: execute a list of Hex update and refresh world info and 
     *         clear the critter info (because it may has changed)
     */
    private void executeHexUpdate(ArrayList<HexToUpdate> list) {
    	for (HexToUpdate update : list) {
    		HexLocation loc = HexLocation.positionToLocation(
    				update.pos, worldCol, worldRow);
    		Hex tmp = (Hex) root.lookup("#" + 
					HexLocation.getID(loc.c, loc.r));
    		
    		switch (update.type) {
	    		case CRITTER:
	    			tmp.setCritter(update.direction, update.size);
	    			break;
	    			
	    		case ROCK:
	    			tmp.setRock();
	    			break;
	    			
	    		case FOOD:
	    			tmp.setFood();
	    			break;
	    			
	    		case EMPTY:
	    			tmp.setEmpty();
	    			break;
    		}
    	}
    	System.out.println(world.toString());
    	worldInfoLabel.setText(world.getWorldInfo());
    	critterInfoLabel.setText("");
    }
    
    
    
    private void addCritter(String critterNumStr) {
    	if(critterFile == null) {
    		Alerts.alertChooseCritterFile();
    		return;
    	}
    	try {
    		int n = Integer.parseInt(critterNumStr);
    		ArrayList<HexToUpdate> hexToUpdate = 
    				Critter.loadCrittersIntoWorld(world, critterFile, n);
    		executeHexUpdate(hexToUpdate);
    	} catch (SyntaxError err) {
    		Alerts.alertCritterFileIllegal();
    	} catch (Exception expt) {
    		Alerts.alertSpecifyNumOfCritter();
    	}
    }
    
    private void insertCritter() {
    	if(critterFile == null) {
    		Alerts.alertChooseCritterFile();
    		return;
    	}
    	if (current == null) {
    		Alerts.alertSelectHexToInsertCritter();
    		return;
    	}
    	try {
    		HexLocation loc = current.getLoc();
    		ArrayList<HexToUpdate> hexToUpdate = 
	    		Critter.insertCritterIntoWorld(world, critterFile, 
	    				Position.getC(loc.c, loc.r),
	    				Position.getR(loc.c, loc.r));
    		executeHexUpdate(hexToUpdate);
    	} catch (Exception err) {
    		err.printStackTrace();
    		Alerts.alertCritterFileIllegal();
    	} 
    }
    
    
    private File loadFile(Stage primaryStage) {
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open A File");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
        	System.out.println("Select file: " + selectedFile);
        }
        return selectedFile;
    }
    
    /** Handler for arrow keys to trigger moves. */
	class ClickHexHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			Hex tmp = (Hex) event.getSource();
			// un-select click
			if (tmp == current) {
				current = null;
				tmp.setHoverStrock();
			}
			// select click
			else {
				if (current != null)
					current.setDefaultStrock();
				current = tmp;
				tmp.setSelectedStrock();
			}
			// check if there is a critter in the selected hex,
			// if so, need to display the critter info
			Position pos = HexLocation.locationToPosition(tmp.getLoc());
			Element elem = world.getElemAtPosition(pos);
			if (elem != null)
				critterInfoLabel.setText(elem.toString());
			else
				critterInfoLabel.setText("");
		}
	}
	
	/** Handler for arrow keys to trigger moves. */
	class EnterHexHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			// TODO Auto-generated method stub
			System.out.println("you enter a hex");
			Hex tmp = (Hex) event.getSource();
			if (tmp != current)
				tmp.setHoverStrock();
		}
	}
	
	/** Handler for arrow keys to trigger moves. */
	class ExitHexHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			// TODO Auto-generated method stub
			System.out.println("you exit a hex");
			Hex tmp = (Hex) event.getSource();
			if (tmp != current)
				tmp.setDefaultStrock();
		}
	}

}