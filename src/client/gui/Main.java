package client.gui;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import api.HexToUpdate;
import api.PositionInterpreter;
import client.world.ClientPosition;
import client.world.ClientPoint;
import game.exceptions.SyntaxError;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import servlet.element.Critter;
import servlet.element.Element;
import servlet.world.Position;
import servlet.world.World;

/**
 * Main controller class that draw all the hexes in the GUI and control 
 * its interaction with the underlying critter program model.
 * - Listen to the action on View and control the update in underlying data
 * - Execute the update of View based HexToUpdate passed from underlying 
 *   critter world
 *
 * Reference: 
 * - The zooming functionality is modified from: 
 *   - http://hg.openjdk.java.net/openjfx/8u-dev/rt/rev/36a59c629605
 *   - https://www.youtube.com/watch?v=ij0HwRAlCmo
 */
public class Main extends Application {
    private final static int DEFAULT_WORLD_IDX = 0;
	private final static int CUSTOM_WORLD_IDX = 1;
	private File worldFile = null;  // path to world file 
	private File critterFile = null;  // path to critter file
    private GUIHex current = null; // current selected hex
	private Parent root;
	private Pane worldPane;
	private Label worldInfoLabel;
	private Label critterInfoLabel;
	public World world;
    private int worldCol;
    private int worldRow;
    private double intialStepsPerSecond = 1;
    private GraphicsContext gc;
    private HashMap<Integer, Color> speciesColor;
    
    // - if the speed <= 30, each cycle lapse the world, draw the world, 
    //   so counterWorldLapse = counterWorldDraw
    // - if the speed > 30, each cycle lapse the world but 
    //   draw the world only when 30*counterWorldLapse/speed > counterWorldDraw
    //   and have counterWorldDraw++ after drawing the world
    private volatile double speed;
    private volatile int counterWorldLapse;
    private volatile int counterWorldDraw;
    Timeline timeline;
    
	private static final Color DEFAULT_STROCK_COLOR = Color.BLACK;
	private static final Color HOVER_STROCK_COLOR = Color.web("#3AD53A");
	private static final Color SELECTED_STROCK_COLOR = Color.RED;
	public static final double SQRT_THREE = Math.sqrt(3);
	
	
    @Override
    public void start(Stage primaryStage) {
        try {
			root = FXMLLoader.load(getClass().getResource("/client/gui/a6.fxml"));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("can't find the fxml file");
		}
        primaryStage.setTitle("Critter World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
        speciesColor = new HashMap<>();
        
        worldPane = (Pane) root.lookup("#world_pane"); 
        worldInfoLabel = (Label) root.lookup("#worldinfodetails_label");
        critterInfoLabel = (Label) root.lookup("#critterinfodetails_label");
        // set the iteration count to be as large as it can
        timeline = new Timeline();
        timeline.setCycleCount(Integer.MAX_VALUE);
        // recounts cycle count every time it plays again
        timeline.setAutoReverse(false);  
        timeline.getKeyFrames().add(
        		getWorldSimulationKeyFrame(intialStepsPerSecond));
        
        
        // initialize the default world and load default critter file
        world = new World();
        drawWorldLayout();
        HashMap<Position, HexToUpdate> tmp = 
				world.getHexToUpdate();
		executeHexUpdate(tmp.values());
		critterFile = new File(Main.class.
				getResource("colorful_critter.txt").getPath());
        
		
		// initialize button
        MenuButton worldFileBtn = 
        		(MenuButton) root.lookup("#newworld_manubutton");
        worldFileBtn.getItems().get(DEFAULT_WORLD_IDX)
				.setOnAction(e -> {
				        world = new World();
				        drawWorldLayout();
				        HashMap<Position, HexToUpdate> hexToUpdate = 
        	    				world.getHexToUpdate();
        	    		executeHexUpdate(hexToUpdate.values());
				    });
        
        
        worldFileBtn.getItems().get(CUSTOM_WORLD_IDX)
        		.setOnAction(e -> {
        		        worldFile = loadFile(primaryStage);
        		        if (worldFile == null)
        		        	return;
        		        world = World.loadWorld(worldFile);
        		        drawWorldLayout(); 
        		        HashMap<Position, HexToUpdate> hexToUpdate = 
        	    				world.getHexToUpdate();
        	    		executeHexUpdate(hexToUpdate.values());
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
        	HashMap<Position, HexToUpdate> hexToUpdate = 
    				world.getHexToUpdate();
    		executeHexUpdate(hexToUpdate.values());
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
        		    	worldRunAhead();
        		    }
        		}, tmp);
    }
    
    
    private void drawWorldLayout() {
        worldRow = Position.getH(world.getColumn(), 
	                 world.getRow());
	    worldCol = Position.getV(world.getColumn(), 
	       			 world.getRow());
	    world.printCoordinatesASCIIMap();
    	worldPane.getChildren().clear();
    	final Canvas canvas = 
    			new Canvas(worldCol*GUIHex.HEX_SIZE*3/2 + 0.5*GUIHex.HEX_SIZE,
    					(worldRow+1)*GUIHex.HEX_SIZE*GUIHex.SQRT_THREE/2);
    	gc = canvas.getGraphicsContext2D();

    	gc.setStroke(DEFAULT_STROCK_COLOR);
        GUIHex poly;
        for (int i = 0; i < worldRow; ++i) {
        	for (int j = 0; j < worldCol; ++j) {
        		if (i % 2 != j % 2)
        			continue;
        		poly = new GUIHex(j, i, worldRow);
        		gc.setFill(Color.WHITE);
        		gc.strokePolyline(poly.xPoints, poly.yPoints, 
        				GUIHex.POINTSNUMBER+1);
        	}
        }
        worldPane.getChildren().add(canvas);
        canvas.setOnMouseClicked(new ClickHexHandler());
    }
    
    /**
     * Have the underlying world proceed for one turn and update the GUI
     * with a maximum speed limitation of 30
     */
    private void worldRunAhead() {
		// no need to bother with the counter if speed <= 30
		// because always lapse and draw the world at the same time
    	if (speed <= 30) {
    		new Thread() { // Create a new background process
    		    public void run() {
    		        // world simulation in background
    		    	world.lapse();
    		        Platform.runLater(new Runnable() { // Go back to UI/application thread
    		            public void run() {
    		                // Update UI to reflect changes to the model
    		            	executeHexUpdate(world.getHexToUpdate().values());
    		            }
    		        });
    		    }
    		}.start(); // Starts the background thread!
	    	return;
    	}
    	// detect overflow, lose a little precision of interval here
    	if (counterWorldLapse == Integer.MAX_VALUE) {
    		counterWorldLapse = 0;
    		counterWorldDraw = 0;
    	}
		new Thread() { // Create a new background process
		    public void run() {
		        // world simulation in background
		    	world.lapse();
		    	counterWorldLapse++;
		        Platform.runLater(new Runnable() { // Go back to UI/application thread
		            public void run() {
		                // Update UI to reflect changes to the model
		            	if ((int) 30*counterWorldLapse/speed > counterWorldDraw) {
		            		executeHexUpdate(world.getHexToUpdate().values());
		            		counterWorldDraw++;
		            	}
		            }
		        });
		    }
		}.start(); // Starts the background thread!
    }
    
    /**
     * Have the underlying world proceed for one turn and update the GUI
     */
    private void worldStepAhead() {
		new Thread() { // Create a new background process
		    public void run() {
		        // world simulation in background
		    	world.lapse();
		        Platform.runLater(new Runnable() { // Go back to UI/application thread
		            public void run() {
		                // Update UI to reflect changes to the model
		            	executeHexUpdate(world.getHexToUpdate().values());
		            }
		        });
		    }
		}.start(); // Starts the background thread!
    }
    
    /**
     * Effect: execute a list of Hex update and refresh world info and 
     *         clear the critter info (because it may has changed)
     */
    synchronized private void executeHexUpdate(Collection<HexToUpdate> list) {
    	for (HexToUpdate update : list) {
    		ClientPosition loc = PositionInterpreter.serverToClient(
    				update.pos, worldCol, worldRow);
    		
    		switch (update.type) {
	    		case CRITTER:
	    			drawCritterAt(gc, loc, 
	    					update.direction, update.size, update.species);
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
    	worldInfoLabel.setText("World running at the speed of " + speed + 
    			" steps per second. \n" + world.getWorldInfo());
    	critterInfoLabel.setText("");
    }
    
    /**
     * Draw a blank slot into the given HexLocation {@code loc} at the given
     * GraphicsContext {@code gc}
     * 
     * @param gc
     * @param loc
     */
    private void drawEmptyAt(GraphicsContext gc, ClientPosition loc) {
		GUIHex tmp = new GUIHex(loc.c, loc.r, worldRow);
		gc.setFill(Color.WHITE);
		gc.fillPolygon(tmp.xPoints, tmp.yPoints, 
					GUIHex.POINTSNUMBER+1);
		gc.setStroke(DEFAULT_STROCK_COLOR);
		gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
				GUIHex.POINTSNUMBER+1);
    }
   
    /**
     * Draw a rock into the given HexLocation {@code loc} at the given
     * GraphicsContext {@code gc}
     * 
     * @param gc
     * @param loc
     */
    private void drawRockAt(GraphicsContext gc, ClientPosition loc) {
		GUIHex tmp = new GUIHex(loc.c, loc.r, worldRow);
		gc.setFill(new ImagePattern(Resource.rockImg));
		gc.fillPolygon(tmp.xPoints, tmp.yPoints, 
					GUIHex.POINTSNUMBER+1);
		gc.setStroke(DEFAULT_STROCK_COLOR);
		gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
				GUIHex.POINTSNUMBER+1);
    }
    
    /**
     * Draw a food into the given HexLocation {@code loc} at the given
     * GraphicsContext {@code gc}
     * 
     * @param gc
     * @param loc
     */
    private void drawFoodAt(GraphicsContext gc, ClientPosition loc) {
		GUIHex tmp = new GUIHex(loc.c, loc.r, worldRow);
		gc.setFill(new ImagePattern(Resource.foodImg));
		gc.fillPolygon(tmp.xPoints, tmp.yPoints, 
					GUIHex.POINTSNUMBER+1);
		gc.setStroke(DEFAULT_STROCK_COLOR);
		gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
				GUIHex.POINTSNUMBER+1);
    }
    
    /**
     * Draw a critter into the given HexLocation {@code loc} at the given
     * GraphicsContext {@code gc}
     * @param dir - direction of the critter facing at 
     * @param size - size of the critter
     * @param gc
     * @param loc
     */
    private void drawCritterAt(GraphicsContext gc, ClientPosition loc, 
    		int dir, int size, int species) {
    	GUIHex tmp = new GUIHex(loc.c, loc.r, worldRow);
		double radio = getRadio(size);
		
		// body
		gc.setFill(getColorOfSpecies(species));
		gc.fillOval(tmp.getLoc().xPos - GUIHex.HEX_SIZE*3/4*radio, 
				tmp.getLoc().yPos - GUIHex.HEX_SIZE*3/4*radio, 
				GUIHex.HEX_SIZE*3/2*radio, GUIHex.HEX_SIZE*3/2*radio);
		
		// eyes (with 0.5 width border)
		ClientPoint[] eyes = getEyesPos(dir, tmp, radio);
		gc.setFill(Color.BLACK);
		if (eyes == null || eyes[0] == null || eyes[1] == null) {
			System.out.println("eyes is null");
			System.out.println("dir: " + dir + ", radio: " + radio + 
					"Hex cen: " + tmp.centroid + ", Hex c,r: " + 
					tmp.getLoc().c + "," + tmp.getLoc().r);
			return;
		}
		gc.fillOval(eyes[0].x - 1.5*radio - 0.5, eyes[0].y - 1.5*radio - 0.5, 
				3*radio + 1, 3*radio + 1);
		gc.fillOval(eyes[1].x - 1.5*radio - 0.5, eyes[1].y - 1.5*radio - 0.5, 
				3*radio + 1, 3*radio + 1);
		
		gc.setFill(Color.WHITE);
		gc.fillOval(eyes[0].x - 1.5*radio, eyes[0].y - 1.5*radio, 
				3*radio, 3*radio);
		gc.fillOval(eyes[1].x - 1.5*radio, eyes[1].y - 1.5*radio, 
				3*radio, 3*radio);
    }
    
    private Color getColorOfSpecies(int species) {
    	// the color of given species has been defined
    	if (speciesColor.containsKey(species))
    		return speciesColor.get(species);
    	// specify a random color for the species
    	int r = game.utils.RandomGen.randomNumber(256);
    	int g = game.utils.RandomGen.randomNumber(256);
    	int b = game.utils.RandomGen.randomNumber(236);
    	Color tmp = Color.rgb(r, g, b);
    	speciesColor.put(species, tmp);
    	return tmp;
    }
    
    /**
     * Get the radio to draw a critter given the real {@code size} 
     * of the critter
     * 
     * Equation:
     * {@code size} = 1  -->  0.50
     * {@code size} = 2  -->  0.55
     * {@code size} = 3  -->  0.60
     * ...
     * {@code size} > 10 -->  1.00
     * @param size
     * @return
     */
    private double getRadio(int size) {
    	if (size > 10)
    		return 1.0;
    	return 0.5 + (size-1.0) / 20;
    }
    
    /**
     * Get the two eyes location of the critter facing at {@code dir} direction
     * and located at the given {@code centroid}
     * @param dir - 0-5
     * @param hex - the hex want to fill in two eyes
     * @return {centroidOfLeftEye, centroidOfRightEye}
     */
    private ClientPoint[] getEyesPos(int dir, GUIHex hex, double radio) {
    	ClientPoint[] result = new ClientPoint[2];
    	result[0] = ClientPoint.getMiddlePoint(hex.centroid, hex.points[dir%6]);
    	result[0] = ClientPoint.getMiddlePointWithWeight(result[0], hex.centroid, 
    			radio);
    	result[1] = ClientPoint.getMiddlePoint(hex.centroid, hex.points[(dir+1)%6]);
    	result[1] = ClientPoint.getMiddlePointWithWeight(result[1], hex.centroid, 
    			radio);
    	return result;
    }

    
    private void addCritter(String critterNumStr) {
    	if(critterFile == null) {
    		Alerts.alertChooseCritterFile();
    		return;
    	}
    	try {
    		int n = Integer.parseInt(critterNumStr);
    		HashMap<Position, HexToUpdate> hexToUpdate = 
    				Critter.loadCrittersIntoWorld(world, critterFile, n);
    		executeHexUpdate(hexToUpdate.values());
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
    		ClientPosition loc = current.getLoc();
    		HashMap<Position, HexToUpdate> hexToUpdate = 
	    		Critter.insertCritterIntoWorld(world, critterFile, 
	    				Position.getC(loc.c, loc.r),
	    				Position.getR(loc.c, loc.r));
    		executeHexUpdate(hexToUpdate.values());
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
        return selectedFile;
    }
    
    /** Handler for arrow keys to trigger moves. */
	class ClickHexHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			double x = event.getX();
			double y = event.getY();
			int[] nearestHexIndex = 
					GUIHex.classifyPoint(x, y, worldRow, worldCol);
			if (nearestHexIndex[0] == -1 ||
					nearestHexIndex[1] == -1)
				return;
			GUIHex tmp = new GUIHex(nearestHexIndex[0],
					nearestHexIndex[1], worldRow);
			// un-select click
			if (tmp == current) {
				current = null;
				gc.setStroke(DEFAULT_STROCK_COLOR);
				gc.strokePolyline(tmp.xPoints, tmp.yPoints, 
						GUIHex.POINTSNUMBER+1);
			}
			// select click
			else {
				if (current != null) {
					gc.setStroke(DEFAULT_STROCK_COLOR);
					gc.strokePolyline(current.xPoints, current.yPoints, 
							GUIHex.POINTSNUMBER+1);
				}
				current = tmp;
				gc.setStroke(SELECTED_STROCK_COLOR);
				gc.strokePolyline(current.xPoints, current.yPoints, 
						GUIHex.POINTSNUMBER+1);
			}
			// check if there is a critter in the selected hex,
			// if so, need to display the critter info
			Position pos = PositionInterpreter.clientToServer(tmp.getLoc());
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
		int[] nearestHexIndex = 
				GUIHex.classifyPoint(x, y, worldRow, worldCol);
		if (nearestHexIndex[0] == -1 ||
				nearestHexIndex[1] == -1)
			return;
		gc.setStroke(COLOR);
		GUIHex tmpHex = new GUIHex(nearestHexIndex[0],
				nearestHexIndex[1], worldRow);
   		gc.strokePolyline(tmpHex.xPoints, tmpHex.yPoints, 
				GUIHex.POINTSNUMBER+1);
	}

}