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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.StrokeType;
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
    private NewHex current = null; // current selected hex
	private Parent root;
	private Pane worldPane;
	private Label worldInfoLabel;
	private Label critterInfoLabel;
	public World world;
    private int worldCol;
    private int worldRow;
    private double intialStepsPerSecond = 1;
    private GraphicsContext gc;
    
    // - if the speed <= 30, each cycle lapse the world, draw the world, 
    //   so counterWorldLapse = counterWorldDraw
    // - if the speed > 30, each cycle lapse the world but 
    //   draw the world only when 30*counterWorldLapse/speed > counterWorldDraw
    //   and have counterWorldDraw++ after drawing the world
    private double speed;
    private int counterWorldLapse;
    private int counterWorldDraw;
    Timeline timeline;
    
	private static final Color DEFAULT_STROCK_COLOR = Color.BLACK;
	private static final Color HOVER_STROCK_COLOR = Color.web("#3AD53A");
	private static final Color SELECTED_STROCK_COLOR = Color.RED;
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(getClass().getResource("a6.fxml"));
        primaryStage.setTitle("Critter World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
        worldPane = (Pane) root.lookup("#world_pane"); 
        worldInfoLabel = (Label) root.lookup("#worldinfodetails_label");
        critterInfoLabel = (Label) root.lookup("#critterinfodetails_label");
//        Alerts.alertWellcome();
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
        worldStopButton.setOnAction(e -> {
        	// execute update to keep the View - Model synchronized
        	timeline.stop();
        	ArrayList<HexToUpdate> hexToUpdate = 
    				world.getHexToUpdate();
    		executeHexUpdate(hexToUpdate);
        });
        
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
    	speed = stepsPerSecond;
    	counterWorldLapse = 0;
    	counterWorldDraw = 0;
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
    	final Canvas canvas = 
    			new Canvas(worldCol*NewHex.HEX_SIZE*3/2 + 0.5*NewHex.HEX_SIZE,
    					(worldRow+1)*NewHex.HEX_SIZE*NewHex.SQRT_THREE/2);
    	gc = canvas.getGraphicsContext2D();

    	gc.setStroke(DEFAULT_STROCK_COLOR);
        NewHex poly;
        for (int i = 0; i < worldRow; ++i) {
        	for (int j = 0; j < worldCol; ++j) {
        		if (i % 2 != j % 2)
        			continue;
        		poly = new NewHex(j, i, worldRow);
        		gc.setFill(Color.WHITE);
        		gc.strokePolyline(poly.xPoints, poly.yPoints, 
        				NewHex.POINTSNUMBER+1);
        	}
        }
        worldPane.getChildren().add(canvas);
        canvas.setOnMouseClicked(new ClickHexHandler());
    }
    
    /**
     * Have the underlying world proceed for one turn and update the GUI
     */
    private void worldStepAhead() {
//    	System.out.println("Speed: " + speed);
//    	System.out.println("World Lapse: " + counterWorldLapse);
//    	System.out.println("World Draw: " + counterWorldDraw);
		// no need to bother with the counter if speed <= 30
		// because always lapse and draw the world at the same time
    	if (speed <= 30) {
	    	world.lapse();
	    	executeHexUpdate(world.getHexToUpdate());
	    	return;
    	} 
    	// detect overflow, lose a little precision of interval here
    	if (counterWorldLapse == Integer.MAX_VALUE) {
    		world.lapse();
    		counterWorldLapse = 0;
    		counterWorldDraw = 0;
    		return;
    	}
    	world.lapse();
    	counterWorldLapse++;
    	if ((int) 30*counterWorldLapse/speed > counterWorldDraw) {
    		executeHexUpdate(world.getHexToUpdate());
    		counterWorldDraw++;
    	}
    }
    
    /**
     * Effect: execute a list of Hex update and refresh world info and 
     *         clear the critter info (because it may has changed)
     */
    private void executeHexUpdate(ArrayList<HexToUpdate> list) {
    	for (HexToUpdate update : list) {
    		HexLocation loc = HexLocation.positionToLocation(
    				update.pos, worldCol, worldRow);
    		
    		switch (update.type) {
	    		case CRITTER:
	    			drawCritterAt(gc, loc, 
	    					update.direction, update.size);
	    			break;
	    			
	    		case ROCK:
	    			drawRockAt(gc, loc);
	    			break;
	    			
	    		case FOOD:
	    			drawFoodAt(gc, loc);
	    			break;
	    			
	    		case EMPTY:
	    			drawEmptyAt(gc, loc);
	    			break;
    		}
    	}
    	System.out.println(world.toString());
    	worldInfoLabel.setText(world.getWorldInfo());
    	critterInfoLabel.setText("");
    }
    
    /**
     * Draw a blank slot into the given HexLocation {@code loc} at the given
     * GraphicsContext {@code gc}
     * 
     * @param gc
     * @param loc
     */
    private void drawEmptyAt(GraphicsContext gc, HexLocation loc) {
		NewHex tmp = new NewHex(loc.c, loc.r, worldRow);
		gc.setFill(Color.WHITE);
		gc.fillPolygon(tmp.xPoints, tmp.yPoints, 
					NewHex.POINTSNUMBER+1);
		gc.setStroke(DEFAULT_STROCK_COLOR);
		gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
				NewHex.POINTSNUMBER+1);
    }
   
    /**
     * Draw a rock into the given HexLocation {@code loc} at the given
     * GraphicsContext {@code gc}
     * 
     * @param gc
     * @param loc
     */
    private void drawRockAt(GraphicsContext gc, HexLocation loc) {
		NewHex tmp = new NewHex(loc.c, loc.r, worldRow);
		gc.setFill(new ImagePattern(Resource.rockImg));
		gc.fillPolygon(tmp.xPoints, tmp.yPoints, 
					NewHex.POINTSNUMBER+1);
		gc.setStroke(DEFAULT_STROCK_COLOR);
		gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
				NewHex.POINTSNUMBER+1);
    }
    
    /**
     * Draw a food into the given HexLocation {@code loc} at the given
     * GraphicsContext {@code gc}
     * 
     * @param gc
     * @param loc
     */
    private void drawFoodAt(GraphicsContext gc, HexLocation loc) {
		NewHex tmp = new NewHex(loc.c, loc.r, worldRow);
		gc.setFill(new ImagePattern(Resource.foodImg));
		gc.fillPolygon(tmp.xPoints, tmp.yPoints, 
					NewHex.POINTSNUMBER+1);
		gc.setStroke(DEFAULT_STROCK_COLOR);
		gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
				NewHex.POINTSNUMBER+1);
    }
    
    /**
     * Draw a critter into the given HexLocation {@code loc} at the given
     * GraphicsContext {@code gc}
     * @param dir - direction of the critter facing at 
     * @param size - size of the critter
     * @param gc
     * @param loc
     */
    public void drawCritterAt(GraphicsContext gc, HexLocation loc, 
    		int dir, int size) {
    	ImagePattern toSet;
		if (size <= 0)
			toSet = new ImagePattern(Resource.critterImgS1);
		else {
			switch(size) {
			case 1:
				toSet = new ImagePattern(Resource.critterImgS1);
				break;
			case 2:
				toSet = new ImagePattern(Resource.critterImgS2);
				break;
			case 3:
				toSet = new ImagePattern(Resource.critterImgS3);
				break;
			case 4:
				toSet = new ImagePattern(Resource.critterImgS4);
				break;
			case 5:
				toSet = new ImagePattern(Resource.critterImgS5);
				break;
			case 6:
				toSet = new ImagePattern(Resource.critterImgS6);
				break;
			case 7:
				toSet = new ImagePattern(Resource.critterImgS7);
				break;
			default:
				toSet = new ImagePattern(Resource.critterImgS7);
				break;
			}
		}
		gc.setFill(toSet);
		NewHex tmp = new NewHex(loc.c, loc.r, worldRow);
		gc.fillPolygon(tmp.xPoints, tmp.yPoints, 
				NewHex.POINTSNUMBER+1);
		gc.setStroke(DEFAULT_STROCK_COLOR);
		gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
				NewHex.POINTSNUMBER+1);
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
			double x = event.getX();
			double y = event.getY();
//			System.out.println("You click x: " + x + ", y: " + y);
			int[] nearestHexIndex = 
					NewHex.classifyPoint(x, y, worldRow, worldCol);
//			System.out.println("the point is col: " + nearestHexIndex[0]
//					+ ", row: " + nearestHexIndex[1]);
			if (nearestHexIndex[0] == -1 ||
					nearestHexIndex[1] == -1)
				return;
			NewHex tmp = new NewHex(nearestHexIndex[0],
					nearestHexIndex[1], worldRow);
			// un-select click
			if (tmp == current) {
				current = null;
				gc.setStroke(DEFAULT_STROCK_COLOR);
				gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
						NewHex.POINTSNUMBER+1);
			}
			// select click
			else {
				if (current != null) {
					gc.setStroke(DEFAULT_STROCK_COLOR);
					gc.strokePolyline(current.xPoints, current.yPoints, 
							NewHex.POINTSNUMBER+1);
				}
				current = tmp;
				gc.setStroke(SELECTED_STROCK_COLOR);
				gc.strokePolyline(current.xPoints, current.yPoints, 
						NewHex.POINTSNUMBER+1);
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
	
	/** 
	 * Change the color of strock to {@code HOVER_STROCK_COLOR} when
	 * the mouse enter that hex
	 */
	class EnterHexHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			drawPolyLineAt(event.getX(), event.getY(), HOVER_STROCK_COLOR);
		}
	}
	
	/** 
	 * Change the color of strock to {@code DEFAULT_STROCK_COLOR} when
	 * the mouse leave that hex
	 */
	class ExitHexHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			drawPolyLineAt(event.getX(), event.getY(), DEFAULT_STROCK_COLOR);
		}
	}
	
	/**
	 * Draw a polyline with specified color that is the border of the 
	 * hex located at the {@code x}, {@code y}
	 * @param COLOR - the color used to draw the polyline
	 */
	private void drawPolyLineAt(double x, double y, Color COLOR) {
		System.out.println("Event triggered by x: " + x + ", y: " + y);
		int[] nearestHexIndex = 
				NewHex.classifyPoint(x, y, worldRow, worldCol);
		if (nearestHexIndex[0] == -1 ||
				nearestHexIndex[1] == -1)
			return;
		System.out.println("draw: " + COLOR);
		gc.setStroke(COLOR);
		System.out.println("Drawing col: " + nearestHexIndex[0]
				+ ", row: " + nearestHexIndex[1]);
		NewHex tmpHex = new NewHex(nearestHexIndex[0],
				nearestHexIndex[1], worldRow);
   		gc.strokePolyline(tmpHex.xPoints, tmpHex.yPoints, 
				NewHex.POINTSNUMBER+1);
	}

}