package client.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Optional;

import api.JsonClasses.WorldState;
import api.PositionInterpreter;
import api.JsonClasses;
import client.connection.MyClient;
import client.world.ClientElement;
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
	private final static int ZERO = 0;

	private final static int MIN_INSERT_FOOD_AMOUNT = 1;
	private final static int MAX_INSERT_FOOD_AMOUNT = 500;
	private final static int MIN_INSERT_CRITTER_AMOUNT = 1;
	private final static int MAX_INSERT_CRITTER_AMOUNT = 10000;
	private final static int MIN_WORLD_STEP_AMOUNT = 1;
	private final static int MAX_WORLD_STEP_AMOUNT = 10000;
	private final static int MIN_WORLD_RUN_SPEED = 1;
	private final static int MAX_WORLD_RUN_SPEED = 1000;


	private final static int RED_SEED = 100;
	private final static int GREEN_SEED = 200;
	private final static int BLUE_SEED = 300;
	private final static int RGB_OFFSET = 90;
	private final static int RGB_RANGE = 256;



	private volatile File critterFile = null;  // path to critter file
	private ArrayList<GUIHex> selectedHex = new ArrayList<>(); // current selected hex
	private Parent root;
	private Pane worldPane; 
	private Label worldInfoLabel;
	private Label otherInfoLabel; 
	public ClientWorld clientWorld;
	private GraphicsContext gc;
	private Hashtable<Integer, Color> speciesColor = new Hashtable<>();

	private static final Color DEFAULT_STROCK_COLOR = Color.BLACK;
	private static final Color SELECTED_STROCK_COLOR = Color.RED;
	private static final int REFRESH_SPEED = 20;  // 30 times per second
	public static final double SQRT_THREE = Math.sqrt(3);

	public int session_id = 0;

	private MyClient myClient;

	private int from_col;
	private int from_row;
	private int to_col;
	private int to_row;

	private Timeline refreshTimeline;

	private ArrayDeque<WorldState> statesToUpdate = new ArrayDeque<>();

	private static String LOCAL_HOST_URL = 
			"http://localhost:8080/2112/servlet/servlet.Servlet/";

	private static String PUBLIC_TEST_HOST_URL = 
			"http://inara.cs.cornell.edu:54345/";


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
		worldPane = (Pane) root.lookup("#world_pane"); 

		// create a client connection to the server
		String url;
		String userInputUrl = getStringInput("Specify Server", 
				"Input the Server URL you want to connect to. \n"
						+ "For example: " + LOCAL_HOST_URL);
		if (userInputUrl != null)
			url = userInputUrl;
		else
			url = LOCAL_HOST_URL;
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
		if (result == null) {
			primaryStage.close();
			return;
		}
		int statusCode = myClient.logIn(result.getKey(), result.getValue());
		while (statusCode == 401) {
			Alerts.alert401Error("Your password or level is incorrect. \n"
					+ "Level should be one of the 'admin', 'write', 'read'\n"
					+ "If you forgot password, call 6073799054." );
			result = showLoginDialog(primaryStage);
			if (result == null) {
				primaryStage.close();
				return;
			}
			statusCode = myClient.logIn(result.getKey(), result.getValue());
		}
		Alerts.alert200Success("Congratulation, you has successfully"
				+ "login to Critter World, you session id is " +
				myClient.getSessionID() + "\nClick help for tutorial");

		initializeWorld();
	}

	/**
	 * Redraw the whole world when the user change subsection of the world
	 */
	synchronized void reInitializeWorld() {
		// get the whole world since version 0
		WorldState state = new WorldState();
		int statusCode;
		try {
			statusCode = myClient.getStateOfWorld(VERSION_ZERO, from_col, 
					from_row, to_col, to_row, state);
			if (statusCode == 406) {
				Alerts.alert406Error("Can't get the world");
				return;
			}
			clientWorld = new ClientWorld(state);
			// for now, will use to_col and to_row to get part of the world
			drawWorldLayout();
			clientWorld.updateWithWorldState(state);
			Hashtable<ClientPosition, HexToUpdate> hexToUpdate = 
					clientWorld.getHexToUpdate();
			executeHexUpdate(hexToUpdate.values());
		} catch (IOException e) {
			//			Alerts.alert401Error("You are not an administrator");
			e.printStackTrace();
		}
	}

	/**
	 * Request the server to create a new world and update the ClientWorld 
	 * stored at the client side
	 * @param myClient
	 */
	synchronized void initializeWorld() {
		try {
			// get the whole world since version 0
			WorldState state = new WorldState();
			int statusCode = myClient.getStateOfWorld(state);
			if (statusCode == 406) {
				Alerts.alert406Error("Can't get the world");
				return;
			}
			clientWorld = new ClientWorld(state);
			// for now, will use to_col and to_row to get part of the world
			to_col = clientWorld.col;
			to_row = clientWorld.row;
			drawWorldLayout();
			clientWorld.updateWithWorldState(state);
			Hashtable<ClientPosition, HexToUpdate> hexToUpdate = 
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
	synchronized private void refreshGUI() {
		try {
			WorldState state = new WorldState();
			int statusCode = myClient.getStateOfWorld(
					clientWorld.current_version_number, from_col, from_row,
					to_col, to_row, state);
			if (statusCode == 406) {
				Alerts.alert406Error("Can't get the world");
				return;
			}
			statesToUpdate.add(state);
			clientWorld.updateWithWorldState(state);
			Hashtable<ClientPosition, HexToUpdate> hexToUpdate = 
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
			try {
				int statusCode = myClient.newWorld("New Default World");
				if (statusCode == 401)
					Alerts.alert401Error("You have to be an administrator");
			} catch (Exception e1) {
				Alerts.alert401Error("You are not an administrator");
			}
			initializeWorld();
		});


		ObservableList<MenuItem> view_menuitems = 
				menus.get(VIEW_MENU_IDX).getItems();

		view_menuitems.get(WHOLE_WORLD_IDX).setOnAction(e -> { 
			from_col = 0;
			from_row = 0;
			to_col = clientWorld.col;
			to_row = clientWorld.row;
			reInitializeWorld();
		});

		view_menuitems.get(SUBSECTION_WORLD_IDX).setOnAction(e -> { 
			changeSubsectionOfGUIWorld();
			reInitializeWorld();
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
					"Please specify the amount of food you want to insert.", 
					MIN_INSERT_FOOD_AMOUNT, MAX_INSERT_FOOD_AMOUNT);
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
					"Please specify the number of critters you want to add.", 
					MIN_INSERT_CRITTER_AMOUNT, MAX_INSERT_CRITTER_AMOUNT);
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
					"Please specify the number of step you want to process.", 
					MIN_WORLD_STEP_AMOUNT, MAX_WORLD_STEP_AMOUNT);
			worldStepAhead(amount);
			refreshGUI();
		});

		simulate_menuitems.get(SIMULATE_RUN_IDX).setOnAction(e -> { 
			int speed = getAmountInput("Simutate Dialog", 
					"Please specify how many steps per second you want the "
							+ "world to run.", 
							MIN_WORLD_RUN_SPEED, MAX_WORLD_RUN_SPEED);
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

	/**
	 * Change the subsection of GUI world to inspect
	 */
	private void changeSubsectionOfGUIWorld() {
		int from_col_tmp = getAmountInput("Subsection World Dialog", 
				"Please specify starting column index.", ZERO, 
				clientWorld.col);
		int to_col_tmp = getAmountInput("Subsection World Dialog", 
				"Please specify ending column index. ", from_col_tmp,
				clientWorld.col);
		int from_row_tmp = getAmountInput("Subsection World Dialog", 
				"Please specify starting row you want to view.", ZERO,
				clientWorld.row);
		int to_row_tmp = getAmountInput("Subsection World Dialog", 
				"Please specify ending row you want to view.", from_row_tmp,
				clientWorld.row);

		// in case user cancel the input
		if (from_col_tmp == 0 || from_row_tmp == 0 
				|| to_col_tmp == 0 || to_row_tmp == 0)
			return;
		from_col = from_col_tmp;
		from_row = from_row_tmp;
		to_col = to_col_tmp;
		to_row = to_row_tmp;		
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
			ArrayList<ClientElement> critters = new ArrayList<>();
			int statusCode = myClient.lisAllCritters(critters);
			if (statusCode == 406) {
				Alerts.alert406Error("Can't get the world");
				return;
			}
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
			int statusCode = myClient.runWorldAtSpeed(0);
			if (statusCode == 401)
				Alerts.alert401Error("You have to be a writer or "
						+ "an administrator");
			else if (statusCode == 406)
				Alerts.alert406Error("The world hasn't been created");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//		WorldState state = new WorldState();
		//		int statusCode = myClient.getStateOfWorld(
		//				clientWorld.current_version_number, from_col, from_row,
		//				to_col, to_row, state);
		//		if (statusCode == 406) {
		//			Alerts.alert406Error("Can't get the world");
		//			return;
		//		}
		//		statesToUpdate.add(state);
		//		clientWorld.updateWithWorldState(state);
		//		Hashtable<ClientPosition, HexToUpdate> hexToUpdate = 
		//				clientWorld.getHexToUpdate();
		//		executeHexUpdate(hexToUpdate.values());
	}

	/**
	 * Change the simulation speed 
	 * @param speed - the number of frame will be executed in one second 
	 *        = {@code speed}
	 */
	private void changeSimulationSpeed(int speed) {
		try {
			int statusCode = myClient.runWorldAtSpeed(speed);
			if (statusCode == 401)
				Alerts.alert401Error("You have to be a writer or "
						+ "an administrator");
			else if (statusCode == 406)
				Alerts.alert406Error("The world hasn't been created");
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
		for (int i = Math.max(from_y, 0); i <= Math.min(to_y, worldY); ++i) {
			for (int j = Math.max(from_x, 0); j <= Math.min(to_x, worldX); ++j) {
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
			int statusCode = myClient.advanceWorldByStep(n);
			if (statusCode == 401)
				Alerts.alert401Error("You have to be a writer or "
						+ "an administrator");
			else if (statusCode == 406)
				Alerts.alert406Error("The world hasn't been created");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Effect: execute a list of Hex update and refresh world info and 
	 *         clear the critter info (because it may has changed)
	 */
	private void executeHexUpdate(Collection<HexToUpdate> list) {

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
	}

	/**
	 * Print information to the simulation panel
	 */
	private void printToSimulationPanel(String info) {
		worldInfoLabel.setText(info + "You are looking at:\n" +
				"Column " + from_col + " - " + to_col + "\n" +
				"Row      " + from_row + " - " + to_row + "\n");
	}

	/**
	 * Print information to the information panel
	 */
	private void printToInfomationPanel(String info) {
		otherInfoLabel.setText(info);
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
		// things like 321, 231, 312, 312, ...
		int r = Math.abs((species * RGB_OFFSET + (-1)^species * RED_SEED) 
				% RGB_RANGE);
		int g = Math.abs((species * RGB_OFFSET + (-1)^species * GREEN_SEED) 
				% RGB_RANGE);
		int b = Math.abs((species * RGB_OFFSET + (-1)^species * BLUE_SEED) 
				% RGB_RANGE);
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
			return;
		}
		if (selectedHex.size() == 0) {
			Alerts.alertSelectHexToInsert();
			return;
		}
		else if (selectedHex.size() > 1) {
			if (Alerts.alertOnlyOneHexShallBeSelected())
				cleanAllSelected();
			return;
		}
		try {
			ClientPosition loc = selectedHex.get(0).getLoc();
			unselectSelectedHex(selectedHex.get(0));
			int statusCode = myClient.createFoodOrRock(loc, amount, 
					JsonClasses.FOOD);
			if (statusCode == 401) 
				Alerts.alert401Error("You have to be a writer or "
						+ "an administrator");
			else if (statusCode == 406)
				Alerts.alert406Error("The world has not been initialized");
		} catch (Exception err) {
			Alerts.alert401Error("You have to be a writer or "
					+ "an administrator");
		} 
	}

	/**
	 * Clean all selected Hex
	 */
	private void cleanAllSelected() {
		while(!selectedHex.isEmpty()) {
			unselectSelectedHex(selectedHex.get(0));
		}
	}

	/**
	 * Remove {@code hex} from ArrayList {@code selectedHex} and draw it with 
	 * default strock to mark it has been unselected
	 * @param hex
	 */
	private void unselectSelectedHex(GUIHex hex) {
		gc.setStroke(DEFAULT_STROCK_COLOR);
		gc.strokePolyline(hex.xPoints, hex.yPoints, 
				GUIHex.POINTSNUMBER+1);		
		selectedHex.remove(hex);
	}

	/**
	 * Add {@code hex} to ArrayList {@code selectedHex} and drwaw it 
	 * with selected strock to mark it has been seleceted
	 * @param hex
	 */
	private void selectSelectedHex(GUIHex hex) {
		gc.setStroke(SELECTED_STROCK_COLOR);
		gc.strokePolyline(hex.xPoints, hex.yPoints, 
				GUIHex.POINTSNUMBER+1);		
		selectedHex.add(hex);
	}

	/**
	 * Insert a rock at the selected position and 
	 * refresh GUI right after the insertion
	 * @param amount
	 */
	private void insertRock() {
		if (selectedHex.size() == 0) {
			Alerts.alertSelectHexToInsert();
			return;
		}
		else if (selectedHex.size() > 1) {
			if (Alerts.alertOnlyOneHexShallBeSelected())
				cleanAllSelected();
			return;
		}
		try {
			ClientPosition loc = selectedHex.get(0).getLoc();
			unselectSelectedHex(selectedHex.get(0));
			int statusCode = myClient.createFoodOrRock(loc, 
					0, JsonClasses.ROCK);
			if (statusCode == 401)
				Alerts.alert401Error("You have to be a writer "
						+ "or an administator");
			else if (statusCode == 406)
				Alerts.alert406Error("The world hasn't been created");
		} catch (Exception err) {
			Alerts.alert401Error("You have to be a writer "
					+ "or an administator");
		} 
	}

	/**
	 * Delete a selected critter and refresh GUI right after the deletion
	 */
	private void deleteCritter() {
		if (selectedHex.size() == 0) {
			Alerts.alertSelectHexToInsert();
			return;
		}
		else if (selectedHex.size() > 1) {
			if (Alerts.alertOnlyOneHexShallBeSelected())
				cleanAllSelected();
			return;
		}
		GUIHex current = selectedHex.get(0);
		unselectSelectedHex(selectedHex.get(0));
		if (clientWorld.board.get(current.loc) == null ||
				!clientWorld.board.get(current.loc).type.
				equals(JsonClasses.CRITTER)) {
			Alerts.alertSelectCritterToDelete();
			return;
		}
		try {
			int statusCode = myClient.removeCritter(
					clientWorld.board.get(current.loc).id);
			if (statusCode == 401) 
				Alerts.alert401Error("You are not the owner of the critter "
						+ "or an administrator");
			else if (statusCode == 406)
				Alerts.alert406Error("The world has not been initialized");
		} catch (IOException e) {
			Alerts.alert401Error("You are not the owner of the critter "
					+ "or an administrator");
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
			int statusCode = myClient.createCritter(critterFile, 
					null, number);
			if (statusCode == 401)
				Alerts.alert401Error("You have to be a writer or "
						+ "an administrator");
			else if (statusCode == 406)
				Alerts.alert406Error("The world hasn't been created");
		} catch (Exception expt) {
			Alerts.alert401Error("You have to be a writer or "
					+ "an administrator");
		}
	}

	private void insertCritter() {
		if (critterFile == null) {
			Alerts.alertChooseCritterFile();
			return;
		}
		if (selectedHex.size() == 0) {
			Alerts.alertSelectHexToInsert();
			return;
		}
		try {
			ArrayList<ClientPosition> tmp = new ArrayList<>();
			while (!selectedHex.isEmpty()) {
				GUIHex current = selectedHex.get(0);
				unselectSelectedHex(selectedHex.get(0));
				ClientPosition loc = current.getLoc();
				tmp.add(loc);
			}
			int statusCode = myClient.createCritter(
					critterFile, tmp, tmp.size());
			if (statusCode == 401)
				Alerts.alert401Error("You have to be a writer or "
						+ "an administrator");
			else if (statusCode == 406)
				Alerts.alert406Error("The world hasn't been created");
		} catch (Exception err) {
			err.printStackTrace();
			Alerts.alert401Error("You have to be a writer or "
					+ "an administrator");
		} 
	}


	private File loadFile(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open A File");
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
					GUIHex.classifyPoint(x, y, clientWorld.row, clientWorld.col);
			if (nearestHexIndex[0] == -1 ||
					nearestHexIndex[1] == -1)
				return;
			GUIHex newSelected = new GUIHex(nearestHexIndex[0], nearestHexIndex[1], 
					PositionInterpreter.getY(clientWorld.col, clientWorld.row));
			// if the selected hex is not within the current visible world
			if (!withinCurrentDrawnGUIWorld(newSelected))
				return;
			// un-select click
			if (selectedHex.contains(newSelected)) {
				unselectSelectedHex(newSelected);
			}
			// select click
			else {
				selectSelectedHex(newSelected);
			}
			// check if there is a critter in the selected hex,
			// if so, need to display the critter info
			ClientElement elem = clientWorld.board.get(newSelected.getLoc());
			if (elem != null) {
				otherInfoLabel.setText(elem.toString());
			}
			else
				otherInfoLabel.setText("");
		}
	}

	/**
	 * Check whether the selected hex is within the current Drawn GUI world
	 * @param newSelected
	 * @return
	 */
	private boolean withinCurrentDrawnGUIWorld(GUIHex newSelected) {
		int from_x = PositionInterpreter.getX(from_col, from_row);
		int from_y = PositionInterpreter.getY(from_col, from_row);
		int to_x = PositionInterpreter.getX(to_col, to_row);
		int to_y = PositionInterpreter.getY(to_col, to_row);

		// out of range
		if (newSelected.loc.x > to_x || newSelected.loc.x < from_x || 
				newSelected.loc.y > to_y || newSelected.loc.y < from_y)
			return false;
		else
			return true;
	}

	/**
	 * Pop an input dialog to ask user enter an number
	 * @param title
	 * @param headerText
	 * @return
	 */
	private int getAmountInput(String title, String headerText, int lowerBound,
			int upperBound) {
		// Create the custom dialog.
		Dialog<Integer> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(headerText + " \n Within range " + lowerBound 
				+ " - " + upperBound);

		// Set the button types.
		ButtonType submitButtonType = 
				new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(
				submitButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField amount = new TextField();
		amount.setPromptText(lowerBound + " - " + upperBound);


		grid.add(new Label("Your Input:"), 0, 0);
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
				try {
					int tmp = Integer.parseInt(amount.getText());
					if (tmp > upperBound || tmp < lowerBound) {
						Alerts.alertNumberOutOfBound(lowerBound, upperBound);
						return 0;
					}
					return tmp;
				} catch (Exception e) {
					e.printStackTrace();
					Alerts.alertInputAnInteger();
					return 0;
				}

			}
			return 0;
		});

		Optional<Integer> result = dialog.showAndWait();

		result.ifPresent(amountNumber -> {
			System.out.println("amount=" + amountNumber);
		});

		if (!result.isPresent())
			return 0;
		else
			return result.get();

	}


	/**
	 * Pop an input dialog to ask user enter an string
	 * @param title
	 * @param headerText
	 * @return
	 */
	private String getStringInput(String title, String headerText) {
		// Create the custom dialog.
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);

		// Set the button types.
		ButtonType submitButtonType = 
				new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(
				submitButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField amount = new TextField();
		amount.setPromptText(LOCAL_HOST_URL);
		amount.setText(LOCAL_HOST_URL);

		grid.add(new Label("Your Input:"), 0, 0);
		grid.add(amount, 1, 0);

		dialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> amount.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == submitButtonType) {
				try {
					return amount.getText();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}
			return null;
		});

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(amountNumber -> {
			System.out.println("amount=" + amountNumber);
		});

		if (!result.isPresent())
			return null;
		else {
			if (result.get().equals("1"))
				return null;
			else if (result.get().equals("2"))
				return PUBLIC_TEST_HOST_URL;
			else 
				return result.get();
		}


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

		//		Request focus on the username field by default.
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
			System.out.println("Username=" + usernamePassword.getKey() + 
					", Password=" + usernamePassword.getValue());
		});

		// if result is present has been handled above
		if (!result.isPresent())
			return null;
		else
			return result.get();

	}

}
