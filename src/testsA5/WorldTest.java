package testsA5;

import static org.junit.Assert.*;

import org.junit.Test;

import simulate.Critter;
import simulate.Position;
import simulate.Rock;
import simulate.World;

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

	@Test
	public void test() {
		World w = new World(10,15,"test");
		Rock r = new Rock();
		w.setElemAtPosition(r, new Position(1,2));
		w.setElemAtPosition(r, new Position(3,7));
		w.setElemAtPosition(r, new Position(2,8));
		w.printASCIIMap();
	}

}
