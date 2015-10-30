package console;


import java.io.IOException;
import java.util.Scanner;

import exceptions.SyntaxError;
import simulate.Critter;
import simulate.World;

/** The console user interface for Assignment 5. */
public class Console {
    private Scanner scan;
    public boolean done;
    public World world;

    public static void main(String[] args) throws IOException, SyntaxError {
        Console console = new Console();
        String current = new java.io.File( "." ).getCanonicalPath();
        System.out.println("Current dir:"+current);
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);

        while (!console.done) {
            System.out.print("Enter a command or \"help\" for a list of commands.\n> ");
            console.handleCommand();
        }
    }

    /**
     * Processes a single console command provided by the user.
     * @throws SyntaxError 
     * @throws IOException 
     */
    void handleCommand() throws IOException, SyntaxError {
        String command = scan.next();
        switch (command) {
        case "new": {
            newWorld();
            break;
        }
        case "load": {
            String filename = scan.next();
            loadWorld(filename);
            break;
        }
        case "critters": {
            String filename = scan.next();
            int n = scan.nextInt();
            loadCritters(filename, n);
            break;
        }
        case "step": {
            int n = scan.nextInt();
            advanceTime(n);
            break;
        }
        case "info": {
            worldInfo();
            break;
        }
        case "hex": {
            int c = scan.nextInt();
            int r = scan.nextInt();
            hexInfo(c, r);
            break;
        }
        case "help": {
            printHelp();
            break;
        }
        case "exit": {
            done = true;
            break;
        }
        default:
            System.out.println(command + " is not a valid command.");
        }
    }

    /**
     * Constructs a new Console capable of reading the standard input.
     */
    public Console() {
        scan = new Scanner(System.in);
        done = false;
    }

    /**
     * Starts new random world simulation.
     * Initialize the constant of the new world from constant.txt file
     * @throws IOException 
     */
    public void newWorld() {
    	world = new World();
    }

    /**
     * Starts new simulation with world specified in filename.
     * Initialize the constant of the new world from constant.txt file
     * @param filename
     */
    public void loadWorld(String filename) {
    	world = World.loadWorld(filename);
    }

    /**
     * Loads critter definition from filename and randomly places 
     * n critters with that definition into the world.
     * 
     * Check: There are enough slots in the world to place {@code n} critters
     * @param filename
     * @param n
     * @throws SyntaxError 
     * @throws IOException 
     */
    public void loadCritters(String filename, int n) throws IOException, 
    SyntaxError {
    	Critter.loadCrittersIntoWorld(world, filename, n);
    }

    /**
     * Advances the world by n time steps.
     * @param n
     */
    public void advanceTime(int n) {
        for(int i = 0;i < n;i++)
        	world.lapse();
    }

    /**
     * Prints current time step, number of critters, and world
     * map of the simulation.
     */
    public void worldInfo() {
        world.printASCIIMap();
        world.printCoordinatesASCIIMap();
    }

    /**
     * Prints description of the contents of hex (c,r).
     * @param c column of hex
     * @param r row of hex
     */
    public void hexInfo(int c, int r) {
        world.hex(r, c);
    }

    /**
     * Prints a list of possible commands to the standard output.
     */
    private void printHelp() {
        System.out.println("- new: \n        start a new simulation with "
        		+ "a random world");
        System.out.println("- load <world_file>: \n        start a new "
        		+ "simulation with the world loaded from world_file");
        System.out.println("- critters <critter_file> <n>: \n        add n "
        		+ "critters defined by critter_file randomly into the world");
        System.out.println("- step <n>: \n        advance the world "
        		+ "by n timesteps");
        System.out.println("- info: \n        print current timestep, number "
        		+ "of critters living, and map of world");
        System.out.println("- hex <c> <r>: \n        print contents of hex at "
        		+ "column c, row r");
        System.out.println("- exit: \n        exit the program");
    }
}
