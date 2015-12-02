package client.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import api.JsonClasses.WorldState;
import api.PositionInterpreter;
import api.JsonClasses;
import client.MyClient;
import client.element.ClientElement;
import client.world.ClientPoint;
import client.world.ClientPosition;
import client.world.ClientWorld;
import client.world.HexToUpdate;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
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
	
	private final static int VIEW_MENU_IDX = 1;
	private final static int WHOLE_WORLD_IDX = 0;
	private final static int SUBSECTION_WORLD_IDX = 1;
	private final static int REFRESH_WORLD_IDX = 2;
	private final static int KEEPUPDATE_WORLD_IDX = 3;

	private final static int MODIFY_MENU_IDX = 2;
	private final static int INSERT_CRITTER_IDX = 0;
	private final static int INSERT_FOOD_IDX = 1;
	private final static int INSERT_ROCK_IDX = 2;
	private final static int RANDOM_CRITTER_IDX = 3;
	private final static int DELETE_CRITTER_IDX = 4;

	private final static int SIMULATE_MENU_IDX = 3;
	private final static int SIMULATE_STEP_IDX = 0;
	private final static int SIMULATE_RUN_IDX = 1;
	private final static int SIMULATE_PAUSE_IDX = 2;

	private final static int MORE_MENU_IDX = 4;
	private final static int DEAD_CRITTERS_IDX = 0;
	private final static int ALL_CRITTERS_IDX = 1;

	private final static int HELP_MENU_IDX = 5;
	private final static int HOW_USE_IDX = 0;
	private final static int ABOUT_IDX = 1;
	
	private final static int VERSION_ZERO = 0;

	private File critterFile = null;  // path to critter file
	private GUIHex current = null; // current selected hex
	private Parent root;
	private Pane worldPane; 
	private Label worldInfoLabel;
	private Label otherInfoLabel; 
	private Label consoleInfoLabel;
	public ClientWorld clientWorld;
	private GraphicsContext gc;
	private HashMap<Integer, Color> speciesColor = new HashMap<>();

	private static final Color DEFAULT_STROCK_COLOR = Color.BLACK;
	private static final Color SELECTED_STROCK_COLOR = Color.RED;
	private static final int REFRESH_SPEED = 30;  // 30 times per second
	public static final double SQRT_THREE = Math.sqrt(3);

	public int session_id = 0;
	
	private MyClient myClient;
	
	private int from_col;
	private int from_row;
	private int to_col;
	private int to_row;

	private Timeline refreshTimeline;
	

	@Override
	public void start(Stage primaryStage) {
		try {
			root = FXMLLoader.load
					(getClass().getResource("/client/gui/a7.fxml"));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("can't find the fxml file");
		}
		primaryStage.setTitle("Critter World");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		
		otherInfoLabel = 
				(Label) root.lookup("#otherinfodetails_label");
		worldInfoLabel = 
				(Label) root.lookup("#worldinfodetails_label");
		consoleInfoLabel = 
				(Label) root.lookup("#consolenfodetails_label");
		worldPane = (Pane) root.lookup("#world_pane"); 
		
		// create a client connection to the server
		String url = "http://localhost:8080/2112/servlet/servlet.Servlet/";
		myClient = new MyClient(url);
		
		refreshTimeline = new Timeline();
        refreshTimeline.setCycleCount(Integer.MAX_VALUE);
        // recounts cycle count every time it plays again
        refreshTimeline.setAutoReverse(false);  
		refreshTimeline.getKeyFrames().setAll(
				new KeyFrame(Duration.millis(1000/REFRESH_SPEED), 
						event -> refreshGUI())
		);
		
		// initialize menu bar
		initializeMenuBar(primaryStage);
		
		// ask the user to login
		Pair<String, String> result = showLoginDialog(primaryStage);
		int respondCode = myClient.logIn(result.getKey(), result.getValue());
		while (respondCode == 401) {
			Alerts.alert401Error("Your password or level is incorrect. \n"
					+ "Level should be one of the 'admin', 'write', 'read'\n"
					+ "If you forgot password, call 6073799054." );
			result = showLoginDialog(primaryStage);
			respondCode = myClient.logIn(result.getKey(), result.getValue());
		}
		Alerts.alert200Success("Congratulation, you has successfully"
				+ "login to Critter World, you session id is " +
				myClient.getSessionID() + "\nClick help for tutorial");

		initializeWorld();
	}
	
	/**
	 * Request the server to create a new world and update the ClientWorld 
	 * stored at the client side
	 * @param myClient
	 */
	void initializeWorld() {
		try {
			// get the whole world since version 0
			WorldState state = myClient.getStateOfWorld(VERSION_ZERO);
			clientWorld = new ClientWorld(state);
			// for now, will use to_col and to_row to get part of the world
			to_col = clientWorld.col;
			to_row = clientWorld.row;
			drawWorldLayout();
			clientWorld.updateWithWorldState(state);
			HashMap<ClientPosition, HexToUpdate> hexToUpdate = 
					clientWorld.getHexToUpdate();
			executeHexUpdate(hexToUpdate.values());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the Information of the world stored in {@code clientWorld}
	 * 
	 * Update the GUI with {@code hexToUpdate}
	 */
	void refreshGUI() {
		try {
			System.out.println("refresh GUI");
			WorldState state = myClient.getStateOfWorld(
					clientWorld.current_version_number, from_col, from_row,
					to_col, to_row);
			clientWorld.updateWithWorldState(state);
			HashMap<ClientPosition, HexToUpdate> hexToUpdate = 
					clientWorld.getHexToUpdate();
			executeHexUpdate(hexToUpdate.values());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			initializeWorld();
		});

		new_menuitems.get(CUSTOM_WORLD_IDX).setOnAction(e -> {
			stopSimulating();
			initializeWorld();
		});
		

		ObservableList<MenuItem> view_menuitems = 
				menus.get(VIEW_MENU_IDX).getItems();
		
		view_menuitems.get(WHOLE_WORLD_IDX).setOnAction(e -> { 
			to_col = clientWorld.col;
			to_row = clientWorld.row;
		});
		
		view_menuitems.get(SUBSECTION_WORLD_IDX).setOnAction(e -> { 
			to_col = getAmountInput("Subsection World Dialog", 
					"Please specify the number of column you want to view.");
			to_row = getAmountInput("Subsection World Dialog", 
					"Please specify the number of row you want to view.");
		});
		
		view_menuitems.get(REFRESH_WORLD_IDX).setOnAction(e -> { 
			refreshGUI();
		});
		
		view_menuitems.get(KEEPUPDATE_WORLD_IDX).setOnAction(e -> { 
			CheckMenuItem tmp = (CheckMenuItem) 
					view_menuitems.get(KEEPUPDATE_WORLD_IDX);
			if (tmp.isSelected()) {
				System.out.println("timeline should play");
				refreshTimeline.play();
			}
			else
				refreshTimeline.stop();
		});
		

		ObservableList<MenuItem> modify_menuitems = 
				menus.get(MODIFY_MENU_IDX).getItems();

		modify_menuitems.get(INSERT_CRITTER_IDX).setOnAction(e -> { 
			critterFile = loadFile(primaryStage);
			insertCritter();
			refreshGUI();
		});

		modify_menuitems.get(INSERT_FOOD_IDX).setOnAction(e -> { 
			int amount = getAmountInput("Insert Food Dialog", 
					"Please specify the amount of food you want to insert.");
			insertFood(amount);
			refreshGUI();
		});
		
		modify_menuitems.get(INSERT_ROCK_IDX).setOnAction(e -> { 
			insertRock();
			refreshGUI();
		});

		modify_menuitems.get(RANDOM_CRITTER_IDX).setOnAction(e -> { 
			critterFile = loadFile(primaryStage);
			int amount = getAmountInput("Add Critter Dialog", 
					"Please specify the number of critters you want to add.");
			addCritter(amount);
			refreshGUI();
		});
		
		modify_menuitems.get(DELETE_CRITTER_IDX).setOnAction(e -> { 
			deleteCritter();
			refreshGUI();
		});
		
		ObservableList<MenuItem> simulate_menuitems = 
				menus.get(SIMULATE_MENU_IDX).getItems();
		
		simulate_menuitems.get(SIMULATE_STEP_IDX).setOnAction(e -> { 
			int amount = getAmountInput("World Step Dialog", 
					"Please specify the number of step you want to process.");
			worldStepAhead(amount);
			refreshGUI();
		});
		
		simulate_menuitems.get(SIMULATE_RUN_IDX).setOnAction(e -> { 
			int speed = getAmountInput("Simutate Dialog", 
					"Please specify how many steps per second you want the "
					+ "world to run.");
			if (speed < 0)
				speed = 0;
			changeSimulationSpeed(speed);
		});
		
		simulate_menuitems.get(SIMULATE_PAUSE_IDX).setOnAction(e -> { 
			stopSimulating();
		});
		
		ObservableList<MenuItem> more_menuitems = 
				menus.get(MORE_MENU_IDX).getItems();
		
		more_menuitems.get(DEAD_CRITTERS_IDX).setOnAction(e -> { 
			displayDeadCritterInfo();
		});
		
		more_menuitems.get(ALL_CRITTERS_IDX).setOnAction(e -> { 
			displayAllCritterInfo();
		});
		
		ObservableList<MenuItem> help_menuitems = 
				menus.get(HELP_MENU_IDX).getItems();
		
		help_menuitems.get(HOW_USE_IDX).setOnAction(e -> { 
			Alerts.alertDisplayHelpInfo();
		});
		
		help_menuitems.get(ABOUT_IDX).setOnAction(e -> { 
			Alerts.alertDisplayAbout(); 
		});

	}

	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Get dead critter info from the server and display it to the 
	 * information panel
	 */
	private void displayDeadCritterInfo() {
		ArrayList<Integer> critters = clientWorld.dead_critters;
		StringBuilder s = new StringBuilder();
		
		s.append(critters.size() + "Critters Has Died: \n");
		for (int i = 0; i < critters.size()-1; ++i)
			s.append(critters.get(i) + ",");
		s.append(critters.get(critters.size()-1));
		printToInfomationPanel(s.toString());
	}
	
	/**
	 * Get all critter info from the server and display it to the 
	 * information panel
	 */
	private void displayAllCritterInfo() {
		try {
			ArrayList<ClientElement> critters = myClient.lisAllCritters();
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < critters.size(); ++i) 
				s.append("Critter " + critters.get(i).id + 
						" Information: \n" + critters.get(i));
			printToInfomationPanel(s.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stop the world from running
	 */
	private void stopSimulating() {
		try {
			myClient.runWorldAtSpeed(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap<ClientPosition, HexToUpdate> hexToUpdate = 
				clientWorld.getHexToUpdate();
		executeHexUpdate(hexToUpdate.values());
	}

	/**
	 * Change the simulation speed 
	 * @param speed - the number of frame will be executed in one second 
	 *        = {@code speed}
	 */
	private void changeSimulationSpeed(int speed) {
		try {
			myClient.runWorldAtSpeed(speed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Draw the world and all the hex into the canvas
	 * the size of the world is bounded by {@code from_col}, {@code from_row}. 
	 * {@code to_col}, {@code to_row} and {@code clientWorld.col} 
	 * {@code clientWorld.row}
	 * 
	 * No element will be drawn with this method. 
	 */
	private void drawWorldLayout() {
		int worldY = PositionInterpreter.getY(clientWorld.col, clientWorld.row);
		int worldX = PositionInterpreter.getX(clientWorld.col, clientWorld.row);
		int from_x = PositionInterpreter.getX(from_col, from_row);
		int from_y = PositionInterpreter.getY(from_col, from_row);
		int to_x = PositionInterpreter.getX(to_col, to_row);
		int to_y = PositionInterpreter.getY(to_col, to_row);
		worldPane.getChildren().clear();
		final Canvas canvas = 
				new Canvas(worldX*GUIHex.HEX_SIZE*3/2 + 0.5*GUIHex.HEX_SIZE,
						(worldY+1)*GUIHex.HEX_SIZE*GUIHex.SQRT_THREE/2);
		gc = canvas.getGraphicsContext2D();

		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, worldX*GUIHex.HEX_SIZE*3/2 + 0.5*GUIHex.HEX_SIZE,
				(worldY+1)*GUIHex.HEX_SIZE*GUIHex.SQRT_THREE/2);

		gc.setStroke(DEFAULT_STROCK_COLOR);
		GUIHex poly;
		for (int i = Math.max(from_y, 0); i < Math.min(to_y, worldY); ++i) {
			for (int j = Math.max(from_x, 0); j < Math.min(to_x, worldX); ++j) {
				if (i % 2 != j % 2)
					continue;
				poly = new GUIHex(j, i, worldY);
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
	 */
	private void worldStepAhead(int n) {
		try {
			myClient.advanceWorldByStep(n);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Effect: execute a list of Hex update and refresh world info and 
	 *         clear the critter info (because it may has changed)
	 */
	synchronized private void executeHexUpdate(Collection<HexToUpdate> list) {
		
		for (HexToUpdate update : list) {

			switch (update.type) {
			case CRITTER:
				drawCritterAt(gc, update.pos, 
						update.direction, update.size, update.species);
				break;

			case ROCK:
				drawRockAt(gc, update.pos);
				break;

			case FOOD:
				drawFoodAt(gc, update.pos);
				break;

			case EMPTY:
				drawEmptyAt(gc, update.pos);
				break;
			}
		}
		printToSimulationPanel(clientWorld.getWorldInfo());
		appendToConsolePanel("The world has been updated.");
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
		otherInfoLabel.setText(info);
	}
	
	/**
	 * Print response of request to the console panel
	 */
	private void appendToConsolePanel(String info) {
		consoleInfoLabel.setText(consoleInfoLabel.getText() + "\n" + info);
	}

	/**
	 * Draw a blank slot into the given HexLocation {@code loc} at the given
	 * GraphicsContext {@code gc}
	 * 
	 * @param gc
	 * @param loc
	 */
	private void drawEmptyAt(GraphicsContext gc, ClientPosition loc) {
		GUIHex tmp = new GUIHex(loc.x, loc.y, 
				PositionInterpreter.getY(clientWorld.col, clientWorld.row));
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
		GUIHex tmp = new GUIHex(loc.x, loc.y, 
				PositionInterpreter.getY(clientWorld.col, clientWorld.row));
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
		GUIHex tmp = new GUIHex(loc.x, loc.y,
				PositionInterpreter.getY(clientWorld.col, clientWorld.row));
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
		GUIHex tmp = new GUIHex(loc.x, loc.y,
				PositionInterpreter.getY(clientWorld.col, clientWorld.row));
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
					tmp.getLoc().x + "," + tmp.getLoc().y);
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
			myClient.createFoodOrRock(loc, amount, JsonClasses.FOOD);
		} catch (Exception err) {
			err.printStackTrace();
		} 
	}
	
	/**
	 * Insert a rock at the selected position and 
	 * refresh GUI right after the insertion
	 * @param amount
	 */
	private void insertRock() {
		if (current == null) {
			Alerts.alertSelectHexToInsert();
			return;
		}
		try {
			ClientPosition loc = current.getLoc();
			myClient.createFoodOrRock(loc, 0, JsonClasses.ROCK);
		} catch (Exception err) {
			err.printStackTrace();
		} 
	}

	/**
	 * Delete a selected critter and refresh GUI right after the deletion
	 */
	private void deleteCritter() {
		if (current == null) {
			Alerts.alertSelectCritterToDelete();
			return;
		}
		if (clientWorld.board.get(current.loc) == null ||
				!clientWorld.board.get(current.loc).type.
						equals(JsonClasses.CRITTER)) {
			Alerts.alertSelectCritterToDelete();
			return;
		}
		try {
			myClient.removeCritter(clientWorld.board.get(current.loc).id);
		} catch (IOException e) {
			e.printStackTrace();
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
			myClient.createCritter(critterFile, 
					clientWorld.getListOfEmptyPosition(number), number);
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
			ArrayList<ClientPosition> tmp = new ArrayList<>();
			tmp.add(loc);
			myClient.createCritter(critterFile, tmp, 1);
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
			System.out.println("you clicked (" + x + "," + y + ")");
			int[] nearestHexIndex = 
					GUIHex.classifyPoint(x, y, clientWorld.row, clientWorld.col);
			if (nearestHexIndex[0] == -1 ||
					nearestHexIndex[1] == -1)
				return;
			GUIHex tmp = new GUIHex(nearestHexIndex[0], nearestHexIndex[1], 
					PositionInterpreter.getY(clientWorld.col, clientWorld.row));
			System.out.println("closest point: (" + tmp.loc.xPos + 
					"," + tmp.loc.yPos + ")");
			// un-select click
			if (current != null && tmp != null && tmp.loc.equals(current.loc)) {
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
			ClientElement elem = clientWorld.board.get(tmp.getLoc());
			if (elem != null) {
				System.out.println("should print");
				otherInfoLabel.setText(elem.toString());
			}
			else
				otherInfoLabel.setText("");
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


	private Pair<String, String> showLoginDialog(Stage primaryStage) {
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
		username.setPromptText("admin/write/read");
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
			else {
				primaryStage.close();
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
			System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
		});

		return result.get();

	}

}
