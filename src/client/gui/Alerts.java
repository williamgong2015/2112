package client.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * A bunch of alerts used in GUI to help user using this application.  
 *
 */
public class Alerts {
	
	/**
	 * Welcome the user and tell user some basic information
	 */
	public static void alertWellcome() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Welcome to the Critter World");
		String s = "Please first create a new world, then add some critters.\n"
				+ "Click help button for more information.";
		alert.setContentText(s);
		alert.show();
    }
	
	/**
	 * Ask user to create a new world
	 */
	public static void alertCreateNewWorld() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Please create a new world before adding critter");
		String s = "Please click new world button to create a new world";
		alert.setContentText(s);
		alert.show();
	}
	
	/**
     * Show an alert to notice user specify the number of critter to add
     */
    public static void alertSpecifyNumOfCritter() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText("Hasn't specify number of "
				+ "critter to add");
		String s ="Please enter a positive number";
		alert.setContentText(s);
		alert.show();
    }
	
    /**
     * Show an alert to notice user choose a critter file
     */
    public static void alertChooseCritterFile() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText("Hasn't specify a critter file");
		String s = "Please choose a critter file use "
				+ "Load Critter button";
		alert.setContentText(s);
		alert.show();
    }
    
    /**
     * Show an alert to notice user the critter file is illegal
     */
    public static void alertCritterFileIllegal() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText("The critter file has illegal syntax");
		String s ="Please load an legal critter file";
		alert.setContentText(s);
		alert.show();
    }
    
    /**
     * Show an alert to notice user select a hex to insert critter
     */
    public static void alertSelectHexToInsert() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText("Hasn't select a legal hex to insert");
		String s ="Please select an empty hex to insert";
		alert.setContentText(s);
		alert.show();
    }
    
    /**
     * Display instruction of how to use this app after user click help button
     */
    public static void alertDisplayHelpInfo() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Welcome to the Critter World");
		String s ="Click 'New World' Button to generate or load a new world.\n"
				+ "Click 'Load Critter' Button to load a critter file.\n"
				+ "Specify an amount. Then click 'Add' to add some critters.\n"
				+ "Click a hex. Then click 'Insert' to insert critter at "
				+ "that specific hex.\n"
				+ "Click 'Run' to start simulation, you may adjust the "
				+ "simulation speed using the slider.\n"
				+ "Click 'Stop' to stop the simulation.\n"
				+ "Click 'Step' to proceeed one step of the simulation.\n";
		alert.setContentText(s);
		alert.show();
    }

    
    /**
     * Display description of the Critter World game 
     */
    public static void alertDisplayAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("About");
		String s ="This is CS 2112 final project: Critter World! \n"
				+ "built by Yining Gong, Yuxin Cao in Fall 2015.\n";
		alert.setContentText(s);
		alert.show();
    }
    
    /**
     * Ask the user to select a hex to delete 
     */
	public static void alertSelectCritterToDelete() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText("Hasn't select a critter to delete");
		String s ="Please select an critter hex to delete";
		alert.setContentText(s);
		alert.show();
	}
}
