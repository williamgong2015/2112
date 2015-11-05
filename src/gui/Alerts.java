package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Alerts show in GUI
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
    public static void alertSelectHexToInsertCritter() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText("Hasn't select a legal hex to insert critter");
		String s ="Please select an empty hex to insert critter";
		alert.setContentText(s);
		alert.show();
    }
}
