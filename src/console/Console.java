package console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import exceptions.SyntaxError;
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
    private World world;

    public static void main(String[] args) throws IOException, SyntaxError {
        Console console = new Console();

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
     */
    public void newWorld() {
        new World();
    }

    /**
     * Starts new simulation with world specified in filename.
     * @param filename
     */
    private void loadWorld(String filename) {
    	try{
    		FileReader r = new FileReader(new File(filename));
			BufferedReader br = new BufferedReader(r);
    		String s = br.readLine();
    		String name = s.substring(5);
    		s = br.readLine();
    		String[] temp = s.split(" ");
    		int column = Integer.parseInt(temp[1]);
    		int row = Integer.parseInt(temp[2]);
    		World world = new World(row,column,name);
    		System.out.println(name);
    		while((s = br.readLine()) != null) {
    			if(s.startsWith("//"))
    				continue;
    			temp = s.split(" ");
    			if(temp.length == 0)
    				continue;
    			Position pos;
    			if(temp[0].equals("rock")) {
    				pos = new Position(Integer.parseInt(temp[2]),Integer.parseInt(temp[1]));
    				world.setElemAtPosition(new Rock(), pos);
    			}
    			if(temp[0].equals("food")) {
    				int amount = Integer.parseInt(temp[3]);
    				pos = new Position(Integer.parseInt(temp[2]),Integer.parseInt(temp[1]));
    				world.setElemAtPosition(new Food(amount), pos);
    			}
    			if(temp[0].equals("critter")) {
    				String file = temp[1];
    				pos = new Position(Integer.parseInt(temp[3]),Integer.parseInt(temp[2]));
    				int dir = Integer.parseInt(temp[4]);
    				Critter c = new Critter(file);
    				c.setDir(dir);
    				world.setElemAtPosition(c, pos);
    				world.addCritter(c);
    			}
    		}
    	} catch(Exception e) {
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
    private void loadCritters(String filename, int n) throws IOException, SyntaxError {
        for(int i = 0;i < n;) {
        	Critter c = new Critter(filename);
        	int a = RandomGen.randomNumber(world.getRow());
        	int b = RandomGen.randomNumber(world.getColumn());
        	Position pos = new Position(a,b);
        	if(world.checkPosition(pos) && world.getElemAtPosition(pos) == null) {
        		world.setElemAtPosition(c, pos);
        		world.addCritter(c);
        		i++;
        	}
        }
    }

    /**
     * Advances the world by n time steps.
     * @param n
     */
    private void advanceTime(int n) {
        for(int i = 0;i < n;i++)
        	world.lapse();
    }

    /**
     * Prints current time step, number of critters, and world
     * map of the simulation.
     */
    private void worldInfo() {
        //TODO implement
    }

    /**
     * Prints description of the contents of hex (c,r).
     * @param c column of hex
     * @param r row of hex
     */
    private void hexInfo(int c, int r) {
        world.hex(r, c);
    }

    /**
     * Prints a list of possible commands to the standard output.
     */
    private void printHelp() {
        System.out.println("new: start a new simulation with a random world");
        System.out.println("load <world_file>: start a new simulation with "
                + "the world loaded from world_file");
        System.out.println("critters <critter_file> <n>: add n critters "
                + "defined by critter_file randomly into the world");
        System.out.println("step <n>: advance the world by n timesteps");
        System.out.println("info: print current timestep, number of critters "
                + "living, and map of world");
        System.out.println("hex <c> <r>: print contents of hex "
                + "at column c, row r");
        System.out.println("exit: exit the program");
    }
}
