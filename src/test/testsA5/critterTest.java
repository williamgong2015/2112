package test.testsA5;


import java.io.IOException;

import org.junit.Test;

import game.exceptions.SyntaxError;
import servlet.element.Critter;

public class critterTest {

	@Test
	public void test() throws IOException, SyntaxError {
		String file = critterTest.class.getResource("critter1.txt").getPath();
		Critter c = new Critter(file, 1, 0);
		System.out.println(c);
		
		//test if the memory size will be set to MIN_MEMORY
		file = critterTest.class.getResource("critter2.txt").getPath();
		c = new Critter(file, 2, 0);
		System.out.println(c);
	}

}
