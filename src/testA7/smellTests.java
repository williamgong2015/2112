package testA7;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ast.smell;
import exceptions.SyntaxError;
import simulate.Critter;
import simulate.Position;
import simulate.World;

public class smellTests {

	@Test
	public void test() throws IOException, SyntaxError {
		World world = new World();
		world = World.loadWorld("world.txt");
		File f = new File("critter1.txt");
		Critter c = new Critter(f);
		c.setDir(1);
		c.setPosition(new Position(5,6));
		smell s = new smell();
				
		//test unreachable food
		c.setPosition(new Position(11,11));
		assertTrue(s.eval(c, world).equals("1000000"));
		
		//test when we have rocks
		c.setPosition(new Position(6,4));
		assertTrue(s.eval(c, world).equals("5000"));
	}

}
