package console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import exceptions.SyntaxError;
import intial.Constant;
import simulate.Critter;
import simulate.Food;
import simulate.Position;
import simulate.Rock;
import simulate.World;
import util.RandomGen;

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
    	try {
    		Constant.init();
    	} catch (Exception e) {
    		System.out.println("can't find constant.txt,"
    				+ "the constant has not been initialized");
    	}
    	world = new World();
    }

    /**
     * Starts new simulation with world specified in filename.
     * Initialize the constant of the new world from constant.txt file
     * @param filename
     */
    public void loadWorld(String filename) {
    	try{
    		FileReader r = new FileReader(new File(filename));
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(r);
    		String s = br.readLine();
    		String name = s.substring(5);
    		s = br.readLine();
    		String[] temp = s.split(" ");
    		int column = Integer.parseInt(temp[1]);
    		int row = Integer.parseInt(temp[2]);
    		world = new World(row,column,name);
    		System.out.println(name);
    		while((s = br.readLine()) != null) {
    			if(s.startsWith("//"))
    				continue;
    			temp = s.split(" ");
    			if(temp.length == 0)
    				continue;
    			Position pos;
    			if(temp[0].equals("rock")) {
    				pos = new Position(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
    				world.setElemAtPosition(new Rock(), pos);
    			}
    			if(temp[0].equals("food")) {
    				int amount = Integer.parseInt(temp[3]);
    				pos = new Position(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
    				world.setElemAtPosition(new Food(amount), pos);
    			}
    			if(temp[0].equals("critter")) {
    				String file = temp[1];
    				pos = new Position(Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
    				int dir = Integer.parseInt(temp[4]);
    				Critter c = new Critter(file);
    				c.setDir(dir);
    				if (world.checkPosition(pos) && world.getElemAtPosition(pos) == null) {
	    				world.setElemAtPosition(c, pos);
	    				world.addCritterToList(c);
    				}
    			}
    			Constant.init();
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		System.err.println("No such file");
    	}
    }

    /**
     * Loads critter definition from filename and randomly places 
     * n critters with that definition into the world.
     * @param filename
     * @param n
     * @throws SyntaxError 
     * @throws IOException 
     */
    public void loadCritters(String filename, int n) throws IOException, SyntaxError {
        for(int i = 0;i < n;) {
        	Critter c = new Critter(filename);
        	int a = RandomGen.randomNumber(world.getRow());
        	int b = RandomGen.randomNumber(world.getColumn());
        	int dir = RandomGen.randomNumber(6);
        	c.setDir(dir);
        	Position pos = new Position(b, a);
        	if(world.checkPosition(pos) && world.getElemAtPosition(pos) == null) {
        		world.setElemAtPosition(c, pos);
        		world.addCritterToList(c);
        		i++;
        	}
        }
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
