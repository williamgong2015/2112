package test.testsA5;


import java.io.IOException;

import org.junit.Test;

import game.constant.Constant;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import servlet.element.Food;
import servlet.element.Rock;
import servlet.world.Position;
import servlet.world.World;

/**
 * Black box test of World, 
 * create a new world and initialize it with some elements, 
 * check it can be printed out as expected
 * 
 * Random test of World,
 * create a random size of new world and randomly insert a lot of elements,
 * then print the world out, check if the program will crash
 *
 */
public class WorldTest {
	
	int session_id = 0;
	
	@Test 
	public void defaultWorldTest() throws IOException, SyntaxError {
		World w = new World();
		w.printCoordinatesASCIIMap();
		w.printASCIIMap();
	}

	/**
	 * Black box test of World #1 
	 * Insert Food, Rock and Food into it, them print it in two ways
	 */
	@Test
	public void blackBoxTest1() throws IOException, SyntaxError {
		World w = new World(16,10,"test");
		Constant.init();
		Rock r = new Rock();
		w.setElemAtPosition(r, new Position(1,2));
		w.setElemAtPosition(r, new Position(3,7));
		w.setElemAtPosition(r, new Position(2,8));
		String file = "critter1.txt";
		Critter c = new Critter(file, w.critterIDCount++, session_id);
		c.setDir(5);
		w.setElemAtPosition(c, new Position(3,3));
		file = "critter2.txt";
		c = new Critter(file, w.critterIDCount++, session_id);		
		w.setElemAtPosition(c, new Position(4,3));
		Food f = new Food(10);
		w.setElemAtPosition(f, new Position(0,1));
		w.printASCIIMap();
		System.out.println(w.printCoordinatesASCIIMap());
	}
	
	/**
	 * Black box test of World #2
	 * Insert a Critter into the world, print world to check the result
	 * Get the Critter and move it, print world to check the result,
	 * Delete the Critter from the world, print world to check the result
	 */
	@Test
	public void blackBoxTest2() throws IOException, SyntaxError {
		World w = new World(6,8,"test");
		w.printCoordinatesASCIIMap();
		String file = "critter1.txt";
		Critter c = new Critter(file, w.critterIDCount++, session_id);
		c.setDir(5);
		Position origin = new Position(3,3);
		w.setElemAtPosition(c, origin);
		w.printASCIIMap();
		
		System.out.println("-------------------");
		
		// critter move ahead one step toward direction 5
		c = (Critter) w.getElemAtPosition(origin);
		Position p = c.getPosition();
		p = p.getRelativePos(1, c.getDir());
		w.removeElemAtPosition(origin);
		w.setElemAtPosition(c, p);
		w.printASCIIMap();
		
		System.out.println("-------------------");
		
		// delete critter
		w.removeElemAtPosition(p);
		System.out.println(w.printASCIIMap());
	}

}
