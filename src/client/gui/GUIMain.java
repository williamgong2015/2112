package client.gui;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import api.HexToUpdate;
import api.PositionInterpreter;
import client.world.ClientPoint;
import client.world.ClientPosition;
import game.exceptions.SyntaxError;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.util.Pair;
import servlet.element.Critter;
import servlet.element.Element;
import servlet.element.Food;
import servlet.element.Rock;
import servlet.world.Position;
import servlet.world.World;

/**
 * Graphical User Interface at the client side. It draws all the hexes in the 
 * GUI and control its interaction with the underlying critter program model.
 * - Listen to the action on View and control the update in underlying data
 * - Execute the update of View based HexToUpdate passed from underlying 
 *   critter world
 *
 * Reference: 
 * - The zooming functionality is modified from: 
 *   - {@link http://hg.openjdk.java.net/openjfx/8u-dev/rt/rev/36a59c629605}
 *   - {@link https://www.youtube.com/watch?v=ij0HwRAlCmo}
 * - The login dialog form is modified from:
 *   - {@link http://code.makery.ch/blog/javafx-dialogs-official/}
 */
public class GUIMain extends Application {

	// menu bar items index
	private final static int NEW_MENU_IDX = 0;
	private final static int DEFAULT_WORLD_IDX = 0;
	private final static int CUSTOM_WORLD_IDX = 1;

	private final static int MODIFY_MENU_IDX = 1;
	private final static int INSERT_CRITTER_IDX = 0;
	private final static int INSERT_FOOD_IDX = 1;
	private final static int INSERT_ROCK_IDX = 2;
	private final static int RANDOM_CRITTER_IDX = 3;
	private final static int DELETE_CRITTER_IDX = 4;

	private final static int SIMULATE_MENU_IDX = 2;
	private final static int SIMULATE_STEP_IDX = 0;
	private final static int SIMULATE_RUN_IDX = 1;
	private final static int SIMULATE_PAUSE_IDX = 2;

	private final static int INSPECT_MENU_IDX = 3;
	private final static int DEAD_CRITTERS_IDX = 0;
	private final static int ALL_CRITTERS_IDX = 1;

	private final static int HELP_MENU_IDX = 4;
	private final static int HOW_USE_IDX = 0;
	private final static int ABOUT_IDX = 1;
	
	private final static String HOW_TO_USE_INFO = 
			"Click 'New World' Button to generate or load a new world.\n"
			+ "Click 'Load Critter' Button to load a critter file.\n"
			+ "Specify an amount. Then click 'Add' to add some critters.\n"
			+ "Click a hex. Then click 'Insert' to insert critter at "
			+ "that specific hex.\n"
			+ "Click 'Run' to start simulation, you may adjust the "
			+ "simulation speed using the slider.\n"
			+ "Click 'Stop' to stop the simulation.\n"
			+ "Click 'Step' to proceeed one step of the simulation.\n";


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
	private int intialStepsPerSecond = 1;
	private GraphicsContext gc;
	private HashMap<Integer, Color> speciesColor;

	// - if the speed <= 30, each cycle lapse the world, draw the world, 
	//   so counterWorldLapse = counterWorldDraw
	// - if the speed > 30, each cycle lapse the world but 
	//   draw the world only when 30*counterWorldLapse/speed > counterWorldDraw
	//   and have counterWorldDraw++ after drawing the world
	private volatile int speed;
	private volatile int counterWorldLapse;
	private volatile int counterWorldDraw;
	Timeline timeline;

	private static final Color DEFAULT_STROCK_COLOR = Color.BLACK;
	private static final Color SELECTED_STROCK_COLOR = Color.RED;
	public static final double SQRT_THREE = Math.sqrt(3);

	public int session_id = 0;

	@FXML 
	private MenuItem newworld_custom_manuitem;


	@Override
	public void start(Stage primaryStage) {
		try {
			root = FXMLLoader.load(getClass().getResource("/client/gui/a7.fxml"));
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


		showLoginDialog();

		// initialize the default world and load default critter file
		world = new World();
		drawWorldLayout();
		HashMap<Position, HexToUpdate> tmp = 
				world.getHexToUpdate();
		executeHexUpdate(tmp.values());
		critterFile = new File(Main.class.
				getResource("colorful_critter.txt").getPath());


		// initialize button

		initializeMenuBar(primaryStage);


	}

	/**
	 * Set onActionListener on items on menu bar
	 */
	private void initializeMenuBar(Stage primaryStage) {
		MenuBar menubar = (MenuBar) root.lookup("#menubar");
		if (menubar != null) 
			System.out.println("found");

		ObservableList<Menu> menus = menubar.getMenus();

		ObservableList<MenuItem> new_menuitems = 
				menus.get(NEW_MENU_IDX).getItems();

		new_menuitems.get(DEFAULT_WORLD_IDX).setOnAction(e -> {
			stopSimulating();
			world = new World();
			drawWorldLayout();
			HashMap<Position, HexToUpdate> hexToUpdate = 
					world.getHexToUpdate();
			executeHexUpdate(hexToUpdate.values());
		});

		new_menuitems.get(CUSTOM_WORLD_IDX).setOnAction(e -> {
			stopSimulating();
			worldFile = loadFile(primaryStage);
			if (worldFile == null)
				return;
			world = World.loadWorld(worldFile, session_id);
			drawWorldLayout(); 
			HashMap<Position, HexToUpdate> hexToUpdate = 
					world.getHexToUpdate();
			executeHexUpdate(hexToUpdate.values());
		});

		ObservableList<MenuItem> modify_menuitems = 
				menus.get(MODIFY_MENU_IDX).getItems();

		modify_menuitems.get(INSERT_CRITTER_IDX).setOnAction(e -> { 
			stopSimulating();
			critterFile = loadFile(primaryStage);
			insertCritter();
		});

		modify_menuitems.get(INSERT_FOOD_IDX).setOnAction(e -> { 
			stopSimulating();
			int amount = getAmountInput("Insert Food Dialog", 
					"Please specify the amount of food you want to insert.");
			insertFood(amount);
		});
		
		modify_menuitems.get(INSERT_ROCK_IDX).setOnAction(e -> { 
			stopSimulating();
			insertRock();
		});

		modify_menuitems.get(RANDOM_CRITTER_IDX).setOnAction(e -> { 
			stopSimulating();
			int amount = getAmountInput("Add Critter Dialog", 
					"Please specify the number of critters you want to add.");
			addCritter(amount);
		});
		
		modify_menuitems.get(DELETE_CRITTER_IDX).setOnAction(e -> { 
			stopSimulating();
			deleteCritter();
		});
		
		ObservableList<MenuItem> simulate_menuitems = 
				menus.get(SIMULATE_MENU_IDX).getItems();
		
		simulate_menuitems.get(SIMULATE_STEP_IDX).setOnAction(e -> { 
			worldStepAhead();
		});
		
		simulate_menuitems.get(SIMULATE_RUN_IDX).setOnAction(e -> { 
			int speed = getAmountInput("Simutate Dialog", 
					"Please specify how many steps per second you want the "
					+ "world to run.");
			if (speed < 0)
				speed = 0;
			changeSimulationSpeed(speed);
			timeline.play();
		});
		
		simulate_menuitems.get(SIMULATE_PAUSE_IDX).setOnAction(e -> { 
			stopSimulating();
		});
		
		ObservableList<MenuItem> inspect_menuitems = 
				menus.get(INSPECT_MENU_IDX).getItems();
		
		ObservableList<MenuItem> help_menuitems = 
				menus.get(HELP_MENU_IDX).getItems();
		
		help_menuitems.get(HOW_USE_IDX).setOnAction(e -> { 
			Alerts.alertDisplayHelpInfo();
		});
		
		help_menuitems.get(ABOUT_IDX).setOnAction(e -> { 
			Alerts.alertDisplayAbout(); 
		});
		
//		private final static int INSPECT_MENU_IDX = 3;
//		private final static int DEAD_CRITTERS_IDX = 0;
//		private final static int ALL_CRITTERS_IDX = 1;


	}

	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Stop the world from running
	 */
	private void stopSimulating() {
		timeline.stop();
		HashMap<Position, HexToUpdate> hexToUpdate = 
				world.getHexToUpdate();
		executeHexUpdate(hexToUpdate.values());
	}

	/**
	 * Change the simulation speed 
	 * @param sliderVal - the current value of slider (1-30)
	 *        number of frame will be executed in one second 
	 *        = {@code sliderVal}
	 */
	private void changeSimulationSpeed(int sliderVal) {
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

	/**
	 * Get the KeyFrame for the timeline which controls the world running speed
	 * @param stepsPerSecond
	 * @return
	 */
	private KeyFrame getWorldSimulationKeyFrame(int stepsPerSecond) {
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

	/**
	 * Draw all the hex into the canvas
	 */
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

		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, worldCol*GUIHex.HEX_SIZE*3/2 + 0.5*GUIHex.HEX_SIZE,
				(worldRow+1)*GUIHex.HEX_SIZE*GUIHex.SQRT_THREE/2);

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
		printToSimulationPanel("World running at the speed of " + speed + 
				" steps per second. \n" + world.getWorldInfo());
		printToInfomationPanel("");
	}
	
	/**
	 * Print information to the simulation panel
	 */
	private void printToSimulationPanel(String info) {
		worldInfoLabel.setText(info);
	}
	
	/**
	 * Print information to the information panel
	 */
	private void printToInfomationPanel(String info) {
		critterInfoLabel.setText(info);
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
		System.out.println("draw food");
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

	/**
	 * Insert a food of {@code amount} amount at the selected position
	 * @param amount
	 */
	private void insertFood(int amount) {
		if (amount == 0) {
			System.out.println("amount = 0");
			return;
		}
		if (current == null) {
			Alerts.alertSelectHexToInsert();
			return;
		}
		try {
			ClientPosition loc = current.getLoc();
			HashMap<Position, HexToUpdate> hexToUpdate = 
					Food.insertFoodIntoWorld(world, 
							PositionInterpreter.clientToServer(loc), 
							session_id, amount);
			executeHexUpdate(hexToUpdate.values());
		} catch (Exception err) {
			err.printStackTrace();
		} 
	}
	
	/**
	 * Insert a rock at the selected position
	 * @param amount
	 */
	private void insertRock() {
		if (current == null) {
			Alerts.alertSelectHexToInsert();
			return;
		}
		try {
			ClientPosition loc = current.getLoc();
			HashMap<Position, HexToUpdate> hexToUpdate = 
					Rock.insertRockIntoWorld(world, 
							PositionInterpreter.clientToServer(loc), 
							session_id);
			executeHexUpdate(hexToUpdate.values());
		} catch (Exception err) {
			err.printStackTrace();
		} 
	}

	
	private void deleteCritter() {
		if (current == null) {
			Alerts.alertSelectCritterToDelete();
			return;
		}
		Position pos = PositionInterpreter.clientToServer(current.loc);
		if (world.getElemAtPosition(pos) == null ||
				world.getElemAtPosition(pos).getType() != "CRITTER") {
			Alerts.alertSelectCritterToDelete();
			return;
		}
		try {
			HashMap<Position, HexToUpdate> hexToUpdate = 
					Critter.deleteCritterFromWorld(world, pos, session_id);
			executeHexUpdate(hexToUpdate.values());
		} catch (Exception err) {
			err.printStackTrace();
		} 
	}
	
	/**
	 * Add {@code number} number of critter at selected position
	 * @param critterNumStr
	 */
	private void addCritter(int number) {
		if (critterFile == null) {
			Alerts.alertChooseCritterFile();
			return;
		}
		try {
			HashMap<Position, HexToUpdate> hexToUpdate = 
					Critter.loadCrittersIntoWorld(world, critterFile, number,
							session_id);
			executeHexUpdate(hexToUpdate.values());
		} catch (SyntaxError err) {
			Alerts.alertCritterFileIllegal();
		} catch (Exception expt) {
			Alerts.alertSpecifyNumOfCritter();
		}
	}

	private void insertCritter() {
		if (current == null) {
			Alerts.alertSelectHexToInsert();
			return;
		}
		if (critterFile == null) {
			Alerts.alertChooseCritterFile();
			return;
		}
		try {
			ClientPosition loc = current.getLoc();
			HashMap<Position, HexToUpdate> hexToUpdate = 
					Critter.insertCritterIntoWorld(world, critterFile, 
							Position.getC(loc.c, loc.r),
							Position.getR(loc.c, loc.r), session_id);
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

	private int getAmountInput(String title, String headerText) {
		// Create the custom dialog.
		Dialog<Integer> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);

		// Set the button types.
		ButtonType submitButtonType = 
				new ButtonType("Insert", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(
				submitButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField amount = new TextField();
		amount.setPromptText("1 - 500");

		grid.add(new Label("Input:"), 0, 0);
		grid.add(amount, 1, 0);

		// Enable/Disable login button depending on whether a amount was entered.
		Node submitButton = 
				dialog.getDialogPane().lookupButton(submitButtonType);
		submitButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		amount.textProperty().addListener((observable, oldValue, newValue) -> {
			submitButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> amount.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == submitButtonType) {
				return Integer.parseInt(amount.getText());
			}
			return null;
		});

		Optional<Integer> result = dialog.showAndWait();

		result.ifPresent(amountNumber -> {
			System.out.println("amount=" + amountNumber);
		});

		return result.get();
		
	}


	private Pair<String, String> showLoginDialog() {
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Login Dialog");
		dialog.setHeaderText("Please specify user level and input password.");

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("admin/writer/reader");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("User Level:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		//		// Request focus on the username field by default.
		//		Platform.runLater(() -> username.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(username.getText(), password.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
			System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
		});

		return null;

	}

}
